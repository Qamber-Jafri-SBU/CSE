import sys
import dpkt
import struct
import binascii


class ARPHeader:
    def __init__(self, hardware_type = 0, protocol_type = 0, hardware_size = 0, protocol_size = 0, opcode = 0, s_MAC = 0, s_IP = 0, t_MAC = 0, t_IP = 0):
        self.hardware_type = binascii.hexlify(hardware_type)
        self.protocol_type = binascii.hexlify(protocol_type)
        self.hardware_size = binascii.hexlify(hardware_size)
        self.protocol_size = binascii.hexlify(protocol_size)
        self.opcode = binascii.hexlify(opcode)
        self.sender_MAC = self.to_mac(binascii.hexlify(s_MAC))
        self.sender_IP = self.to_ipv4(binascii.hexlify(s_IP))
        self.target_MAC = self.to_mac(binascii.hexlify(t_MAC))
        self.target_IP = self.to_ipv4(binascii.hexlify(t_IP))

    @staticmethod
    def to_ipv4(hex_num):
        ipv4 = [hex_num[i:i+2] for i in range(0, len(hex_num), 2)]
        ipv4 = [int(x, 16) for x in ipv4]
        return ".".join(str(x) for x in ipv4)

    @staticmethod
    def to_mac(hex_num):
        mac = [hex_num[i: i+2].decode("utf-8") for i in range(0, len(hex_num), 2)]
        return ":".join(str(x) for x in mac)

    def __str__(self):
        return f"hardware type: {self.hardware_type}\n" \
               f"protocol type: {self.protocol_type}\n" \
               f"hardware size: {self.hardware_size}\n" \
               f"protocol size: {self.protocol_size}\n" \
               f"opcode: {self.opcode}\n" \
               f"sender MAC address: {self.sender_MAC}\n" \
               f"sender IP address: {self.sender_IP}\n" \
               f"target MAC address: {self.target_MAC}\n" \
               f"target IP address: {self.target_IP}\n" \



def main(pcap_file):
    with open(pcap_file, 'rb') as file:
        pcap = dpkt.pcap.Reader(file)
        arp_headers = []
        i = 1
        for timestamp, buf in pcap:
            ethernet = struct.unpack("3s3s3s3s2s", buf[0:14])
            arp_type = (2054).to_bytes(2, byteorder='big')
            if ethernet[-1] == arp_type:
                packet_bytes = struct.unpack("2s2s1s1s2s6s4s6s4s", buf[14:42])
                header = ARPHeader(*packet_bytes)
                print("ARP PACKET", str(i) + ":")
                print(header)
                arp_headers.append(header)
                i += 1


if __name__ == '__main__':
    main(sys.argv[1])

