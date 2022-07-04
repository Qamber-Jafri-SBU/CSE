# analysis_pcap_arp

## Instructions
1. Ensure you are in the same directory as `analysis_pcap_arp.py`
2. Call the program using `python analysis_pcap_arp.py <pcap-file>` where `pcap-file` is the name of the pcap file
3. The program outputs the analysis of the pcap file in the terminal

## Brief Overview
The program takes in a pcap file as an argument and returns a trace of the pcap file. It opens the pcap file using the dpkt Reader class, and then performs all byte-level analysis within the code itself. It only performs analysis on ARP type packets and can discern a packet's type by comparing it to 0x0806/2054 which is the type for the ARP packet. The struct library is also used in order to do the byte-level analysis to pack and unpack the packets. Once a packet is unpacked, it is stored inside of an ARPheader class that stores all of the information related to an ARP header. The IP address and MAC addresses of the sender and target are also stored but they are not stored in binary like the other data. The IP addresses are stored as strings formatted in the IPv4 format, and the MAC address are stored as string formatted in the MAC address format.
