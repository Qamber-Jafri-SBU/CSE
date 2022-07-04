#include "server.h"
#include "protocol.h"
#include "player.h"
#include <time.h>

#include "csapp.h"

int debug_show_maze;
#ifdef DEBUG
int debug_show_maze = 1;
#endif

CLIENT_REGISTRY *client_registry;

void refactor_packet(MZW_PACKET *pkt, uint8_t type, uint16_t size);

void *mzw_client_service(void *arg){
	//store arg into int, free arg
	int fd = *(int*)arg;
	free(arg);

	//detach thread
	Pthread_detach(pthread_self());

	//register client fd w/ client registry
	creg_register(client_registry, fd);

	//malloc for pkt pointer
	MZW_PACKET *pkt = Malloc(sizeof(MZW_PACKET));
	PLAYER *player = NULL;

	while(1){ //service loop
		//receive request
		void **datap = Malloc(sizeof(datap));
		proto_recv_packet(fd, pkt, datap); //e check

		if(fd == -1){
			break;
		}

		if(player == NULL){ //player is not logged in
			if(pkt->type == MZW_LOGIN_PKT){ //if packet is login
				OBJECT  avatar = pkt->param1;
				char *name = (char *)(*datap);
				player = player_login(fd, avatar, name);

				free(*datap);
				free(datap);
				if(player == NULL){ //unsuccsesful login
					//send in use packet
					refactor_packet(pkt, MZW_INUSE_PKT, 0);
					proto_send_packet(fd, pkt, NULL); //e check
					continue;
				}else{ //successful login
					//send ready pkt
					refactor_packet(pkt, MZW_READY_PKT, 0);
					proto_send_packet(fd, pkt, NULL);
					player_reset(player);
				}
			}
		}else{ //player is logged in
			//free before switch or free after switch
			switch (pkt->type){
				case MZW_MOVE_PKT:
					if((pkt->param1 == 1) || (pkt->param1 == -1)){
						player_move(player, pkt->param1);
					}else{
						break; //error
					}
					break;
				case MZW_TURN_PKT:
					if((pkt->param1 == 1) || (pkt->param1 == -1)){
						player_rotate(player, pkt->param1);
					}else{
						break; //error
					}
					break;
				case MZW_FIRE_PKT:
					player_fire_laser(player);
					break;
				case MZW_REFRESH_PKT:
					player_invalidate_view(player);
					player_update_view(player);
					break;
				case MZW_SEND_PKT:
					player_send_chat(player, *datap, pkt->size);
					free(*datap);
					free(datap);
					break;

			}


		}
		//process request

		//submit request
		if(debug_show_maze){
			show_maze();
		}
	}



	free(pkt);
	return NULL;
}

void set_time(MZW_PACKET *pkt){
	struct timespec x;
	clock_gettime(CLOCK_MONOTONIC, &x);
	pkt->timestamp_sec = x.tv_sec;
	pkt->timestamp_nsec = x.tv_nsec;
}

void refactor_packet(MZW_PACKET *pkt, uint8_t type, uint16_t size){
	pkt->type = type;
	pkt->param1 = 0;
	pkt->param2 = 0;
	pkt->param3 = 0;
	pkt->size = size;
	set_time(pkt);
}