#include "cookbook.h"
#include "functions.h"
#include "queue.h"

void enqueue(RECIPE *recipe, struct head_of_queue *queuep){
	//if head of list empty, set
	if(queuep == NULL){
		fprintf(stderr, "head of queue is null");
		exit(EXIT_FAILURE);
	}
	if(queuep->head == NULL){
		queuep->head = recipe;
		return;
	}
	if(queuep->head->next == NULL){
		queuep->head->next = recipe;
		return;
	}
	RECIPE *temp = queuep->head;
	while(temp->next != NULL){
		temp = temp->next;
	}
	temp->next = recipe;
	recipe->next = NULL;

}

RECIPE *dequeue(struct head_of_queue *queuep){
	if(queuep == NULL){
		fprintf(stderr, "head of queue is null");
		exit(EXIT_FAILURE);
	}
	if(queuep->head == NULL){
		fprintf(stderr, "queue is empty");
		exit(EXIT_FAILURE);
	}
	RECIPE *temp = queuep->head;
	if(queuep->head->next != NULL){
		queuep->head = queuep->head->next;
	}
	else{
		queuep->head = NULL;
	}
	return temp;
}

int is_empty(struct head_of_queue *queuep){
	if(queuep == NULL){
		fprintf(stderr, "head of queue is null");
		exit(EXIT_FAILURE);
	}
	if(queuep->head == NULL){
		return 1;
	}

	return 0;
}
void print_queue(struct head_of_queue *queuep){
	if(queuep == NULL){
		fprintf(stderr, "head of queue is null");
		exit(EXIT_FAILURE);
	}
	RECIPE *curr = queuep->head;
	while(curr != NULL){
		// print_recipe(curr);
		print_leaf(curr);
		curr = curr->next;
	}
}