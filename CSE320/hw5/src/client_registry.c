#include "client_registry.h"
#include "csapp.h"
#include <sys/socket.h>

typedef struct client_node {
	int fd;
	int count;
	struct client_node *prev;
	struct client_node *next;
} CLIENT_NODE;


sem_t sem;

CLIENT_REGISTRY *creg_init(){
	CLIENT_NODE *head = Malloc(sizeof(CLIENT_NODE));
	head->count = 0, head->fd = 0, head->prev = NULL, head->next = NULL;
	Sem_init(&sem,0,1);
	return (CLIENT_REGISTRY *)head;
}

void creg_fini(CLIENT_REGISTRY *cr){
	free(cr);
}

void creg_register(CLIENT_REGISTRY *cr, int fd){
	P(&sem);
	//create new  creg in register
	CLIENT_NODE *new_node = Malloc(sizeof(CLIENT_NODE));
	new_node->fd = fd;

	CLIENT_NODE *temp = (CLIENT_NODE *)cr;
	while(temp->next != NULL){
		if(temp->prev == NULL){
			temp->count++;
		}
		else{
		temp->count = temp->prev->count;
	}
		temp = temp->next;
	}
	new_node->count = temp->count;
	temp->next = new_node;
	new_node->prev = temp;
	V(&sem);
}


void creg_unregister(CLIENT_REGISTRY *cr, int fd){
	P(&sem);
	CLIENT_NODE *temp = (CLIENT_NODE *)cr;
	while(temp != NULL && temp->fd != fd){
		temp = temp->next;
		temp->count--;
	}
	temp->prev->next = temp->next;
	temp->next->prev = temp->prev;
	free(temp);
	close(fd);
	V(&sem);
}

void creg_wait_for_empty(CLIENT_REGISTRY *cr){
	// Sem_init(&sem, 0, 1);
	// P(&)
	CLIENT_NODE *temp = (CLIENT_NODE *)cr;
	while(temp->count != 0){

	}
}

void creg_shutdown_all(CLIENT_REGISTRY *cr){
	//shut_rd
	CLIENT_NODE *temp = (CLIENT_NODE *)cr;
	while(temp != NULL){
		shutdown(temp->fd, SHUT_RD);
	}

}