#include "protocol.h"
#include "errno.h"
#include <arpa/inet.h>

#include "csapp.h"
int proto_send_packet(int fd, MZW_PACKET *pkt, void *data){
	//sends packet over network connection
	//the socket to send (write) to is fd
	//pkt is a pointer to the packet header
	// data is payload which may be null
	//need to convert multi byte fields in pkt to network byte
		//order use htonl()


	//convert header to network byte order
	uint16_t size = pkt->size;
	pkt->size = htons(pkt->size);
	pkt->timestamp_sec = htonl(pkt->timestamp_sec);
	pkt->timestamp_nsec = htonl(pkt->timestamp_nsec);

	//write header to the network connection


		int count = 0;
		while(count < sizeof(MZW_PACKET)){
			if((count += write(fd, pkt, sizeof(MZW_PACKET) - count)) == -1){
				return -1;
			}
		}

		//if header->size > 0, write data to network connection
		if(size > 0){
			count = 0;
			while(count < size){
				count += write(fd, data, size - count);
			}
		}



	return 0;
}


int proto_recv_packet(int fd, MZW_PACKET *pkt, void **datap){

	//read to get fixed size packet from network
		int current_read, count = 0;
		while(count < sizeof(MZW_PACKET)){
			current_read = read(fd, pkt, sizeof(MZW_PACKET) - count);
			if((current_read == 0) || (current_read == -1)) {
				return -1;
			}
			count += current_read;
		}



	//convert to host byte
	pkt->size = ntohs(pkt->size);
	pkt->timestamp_sec = ntohl(pkt->timestamp_sec);
	pkt->timestamp_nsec = ntohl(pkt->timestamp_nsec);

	//if size field in header != 0, read again for payload


		if(pkt->size > 0){
			void *payloadp = malloc(pkt->size);
			int current_read, count = 0;
			while(count < pkt->size){
				current_read = read(fd, payloadp, pkt->size - count);
				if((current_read == 0) || (current_read == -1)){
					return -1;
				}
				count += current_read;

			}
			*datap = payloadp;
		}



	return 0;
}