import argparse
import dpkt

parser = argparse.ArgumentParser(description="analysis_pcap_tcp")
parser.add_argument('pcap_file', type=str, help='The PCAP file to view.')

sender = "130.245.145.12"
receiver = "128.208.2.198"


class Flow:
    def __init__(self, s_port, s_ip, d_port, d_ip):
        self.source_port = s_port
        self.source_ip = s_ip
        self.destination_port = d_port
        self.destination_ip = d_ip
        self.packets = []
        self.transactions = []
        self.window_size = 0
        self.amount_of_data = 0
        self.start_time = 0
        self.end_time = 0
        self.throughput = 0
        self.end_reached = False
        self.RTT = 0
        self.congestion_windows = []
        self.received_packets = {}
        self.r_dup_packets = {}
        self.seq_dup_packets = {}
        self.sent_packets_seq = {}

    def __str__(self):
        parties = ["Sender", "Receiver"]
        header = f"Source Port: {self.source_port:<20}Source IP: {self.source_ip :<20}Destination Port: " \
                 f"{self.destination_port :<20}Destination IP: {self.destination_ip:<20}" \
                 f"Destination Port : {self.destination_port}\n"
        for i, transaction in enumerate(self.transactions):
            transaction_string = f"\tTransaction {i + 1}:\n"
            transaction_string += f"\t\t{parties[0]:<20}SEQ Number: {transaction[0].seq :<20}ACK Number: " \
                                  f"\t\t{transaction[0].ack :<20}Receive Window Size: {transaction[1].win} " \
                                  f"(Calculated size : {transaction[0].win * 16384})\n"
            header = header + transaction_string
            transaction_string = f"\t\t{parties[1]:<20}SEQ Number: {transaction[1].seq :<20}ACK Number: " \
                                 f"\t\t{transaction[1].ack :<20}Receive Window Size: {transaction[1].win} " \
                                 f"(Calculated size : {transaction[1].win * 16384})\n"
            header = header + transaction_string

        header += f"Throughput: {self.throughput} bytes/sec\n"
        header += f"Congestion Windows: {self.congestion_windows[:3]}\n"
        header += f"Retransmissions (Triple duplicate ACK): {len(set(self.seq_dup_packets.keys() & set(self.r_dup_packets.keys())))}\n"
        header += f"Retransmissions (Timeout): {len(self.seq_dup_packets.keys()) - len(set(self.seq_dup_packets.keys() & set(self.r_dup_packets.keys())))}\n"
        return header

    def __eq__(self, flow):
        return self.source_port == flow.source_port and self.source_ip == flow.source_ip \
               and self.destination_port == flow.destination_port and self.destination_ip == flow.destination_ip

    def get_transaction(self, packet):
        for p in self.packets[self.packets.index(packet) + 1:]:
            if p.sport == packet.dport and p.dport == packet.sport and packet.ack == p.seq and p.ack != packet.seq:
                return packet, p

    def get_next_packet(self, packet):
        for i, p in enumerate(self.packets[self.packets.index(packet) + 1:], start=(self.packets.index(packet) + 1)):
            if p.sport == packet.sport and p.dport == packet.dport and p.seq != packet.seq:
                return p

    def get_packets(self):
        return self.packets

    def add_packet(self, packet):
        self.packets.append(packet)

    def get_congestion_window(self):
        base_time = self.packets[2].timestamp
        congestion_windows = [0] * 100
        index = 0
        for packet in self.packets[2:]:
            x = packet.timestamp - self.packets[0].timestamp
            difference= packet.timestamp - base_time
            y= self.RTT
            if packet.sport == self.source_port and packet.timestamp - base_time >= self.RTT:
                base_time = packet.timestamp
                index += 1
            if packet.sport == self.source_port:
                congestion_windows[index] += 1
        self.congestion_windows = congestion_windows
        return congestion_windows


def main():
    args = parser.parse_args()
    flows = []
    number_of_flows = 0

    def add_packet_to_flow(packet, ts):
        for f in flows:
            same_flow = (f.source_port == packet.sport) or (f.source_port == packet.dport)
            if same_flow:
                # if f.packets and f.packets[len(f.packets) - 1].flags & dpkt.tcp.TH_SYN:
                #     f.start_time = ts
                if len(f.packets) == 3:
                    f.start_time = ts
                if packet.sport == f.source_port and packet.flags & dpkt.tcp.TH_FIN:
                    f.end_time = ts
                    f.end_reached = True
                if packet.sport == f.source_port and not f.end_reached and len(f.packets) >= 3:
                    f.amount_of_data += len(packet)
                    f.sent_packets_seq[packet.seq] = f.sent_packets_seq.get(packet.seq, 0) + 1
                if packet.dport == f.source_port:
                    f.received_packets[packet.ack] = f.received_packets.get(packet.ack, 0) + 1
                f.add_packet(packet)

    with open(args.pcap_file, 'rb') as file:
        pcap = dpkt.pcap.Reader(file)
        flow = None
        for timestamp, buf in pcap:
            eth = dpkt.ethernet.Ethernet(buf)

            # check if packet is not IP
            if eth.type != dpkt.ethernet.ETH_TYPE_IP:
                continue

            ip = eth.data

            # skip if packet is not TCP
            if ip.p != dpkt.ip.IP_PROTO_TCP:
                continue

            tcp = ip.data
            tcp.timestamp = timestamp

            if tcp.flags & dpkt.tcp.TH_SYN and not tcp.flags & dpkt.tcp.TH_ACK:
                flows.append(Flow(tcp.sport, dpkt.utils.inet_to_str(ip.src), tcp.dport, dpkt.utils.inet_to_str(ip.dst)))
                number_of_flows += 1
            add_packet_to_flow(tcp, timestamp)

    for flow in flows:
        flow.RTT = flow.packets[1].timestamp - flow.packets[0].timestamp

    #  r -> s; seq -> dup
    for flow in flows:
        for packet in flow.received_packets:
            if flow.received_packets[packet] > 1:
                flow.r_dup_packets[packet] = flow.received_packets[packet]

    # s -> r; seq -> dup
    for flow in flows:
        for packet in flow.sent_packets_seq:
            if flow.sent_packets_seq[packet] > 1:
                flow.seq_dup_packets[packet] = flow.sent_packets_seq[packet]

    # for flow in flows:
    #     for packet in flow.received_packets:
    #         if flow.received_packets[packet] > 3:

    print("Number of TCP flows: ", number_of_flows, "\n")

    for i, flow in enumerate(flows, start=1):
        transaction_one = flow.get_transaction(flow.get_packets()[2])
        transaction_two = flow.get_transaction(flow.get_next_packet(transaction_one[0]))
        flow.transactions = [transaction_one, transaction_two]
        flow.throughput = flow.amount_of_data/(flow.end_time - flow.start_time)
        flow.get_congestion_window()
        print("Flow ", i)
        print(flow)
        # print("ack -> dup count : ", flow.r_dup_packets)
        # print("seq -> dup count : ", flow.seq_dup_packets)
        # print(len(flow.seq_dup_packets.keys()))
        # print(len(flow.r_dup_packets.keys()))
        # print(len(list(set(flow.dup_packets.keys()) & set(flow.seq_dup_packets.keys()))))
    # {len(self.seq_dup_packets.keys() & set(self.dup_packets.keys())))}


if __name__ == '__main__':
    main()
