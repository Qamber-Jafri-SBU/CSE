#ifndef QUEUE_H
#define QUEUE_H


struct head_of_queue{
	RECIPE *head;
} head_of_queue;

void enqueue(RECIPE *recipe, struct head_of_queue *queuep);
RECIPE *dequeue(struct head_of_queue *queuep);
int is_empty(struct head_of_queue *queuep);
void print_queue(struct head_of_queue *queuep);

#endif