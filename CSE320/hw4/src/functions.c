#include <unistd.h>
#include "cookbook.h"
#include "functions.h"
#include "queue.h"

struct head_of_queue *queue_pointer = NULL;

void initialize(COOKBOOK *cookbook, RECIPE *recipe){
	queue_pointer = calloc(1, sizeof(struct head_of_queue));
	dependency_analysis(cookbook, recipe);
	// print_queue(queue_pointer);
}
void dependency_analysis(COOKBOOK *cookbook, RECIPE *recipe){
	//traverse cookbook and use main recipe name
	//traverse all recipes that this recipe depends on and mark their state with 1
	//once you have reached leaves add to queue

	//if recipe already added to queue

	if(recipe == NULL){
		return;
	}

	//recipe->state = 1
	RECIPE_STATE *rs = calloc(1, sizeof(RECIPE_STATE));
	rs->in_main_recipe = 1;
	recipe->state = (void *)rs;

	if(IN_QUEUE(recipe) == 1){
		return;
	}

	//if recipe->this_depends_on == NULL then add to queue and delink parent and child
	if(recipe->this_depends_on == NULL){
		//add to queue
		//if recipe->depends_on_this && recipe->depends_on_this->recipe->state == 1 (check if state is non-null)
		//then delink ()

		//set queue bit
		SET_IN_QUEUE(recipe, 1);
		// debug("leaf : %s", recipe->name);

		//unlink from dependency list
		// disconnect_recipe(recipe);

		//enqueue
		// debug("nnver?");
		enqueue(recipe, queue_pointer);
		return;
	}
	else{
		//else recipe is not a leaf recipe

		RECIPE_LINK *recipe_link = recipe->this_depends_on;
		if(recipe_link == NULL){
			return;
		}
		do{
			dependency_analysis(cookbook, recipe_link->recipe);
			recipe_link = recipe_link->next;
		}while(recipe_link != NULL); //while recipe->this_depends_on != null then initialize recipe
	}
}

//disconnects recipe from dependency tree when all children have been completed
void disconnect_recipe(RECIPE *recipe){
	//disconnect child from marked parent's this_depends_on
	// debug("child : %s", recipe->name);
	RECIPE_LINK *parent = recipe->depend_on_this;

	//search for parent that is in the main recipe
	while(parent != NULL){

		//check for recipes in main recipe
		if(parent->recipe->state != NULL ){
			// debug("parent name : 	%s", parent->name);
			RECIPE_LINK *child = parent->recipe->this_depends_on;
			if(child != NULL && child->next == NULL){
				if(strcmp(recipe->name, child->name) == 0){
					//set childs next to the childs next next (disconnecting desired node)
					// debug("child2 : %s", child->name);
					// child->next = child->next->next; //remove from dependency list!!!!!!!!!!!!!!
					// break;
				}
			}
			else{
			// search for child with same name as recipe
				while(child->next != NULL){
					// debug("new child: 	%s", child->name);
					if(strcmp(recipe->name, child->next->name) == 0){
						//set childs next to the childs next next (disconnecting desired node)
						// debug("child2 : %s", child->next->name);
						child->next = child->next->next; //remove from dependency list!!!!!!!!!!!!!!!!!!!
						break;
					}
					child = child->next;

				}
			}

		}
		parent = parent->next;
	}
}

void task_process(TASK *task){
	//perform steps in task (each step uses fork())
	STEP *s = task->steps;
	while(s != NULL){
		if(fork() == 0){
			// debug("concat : %s", concat("util", s[0]))
			// execvp(s->words[0], );
		}

		s = s->next;
	}
}

void cook_process(RECIPE *recipe){
/*	//carry out tasks for the recipe in sequence
	//get tasks from recipe
	TASK *task = recipe->tasks;
	if(task->input_file != NULL){
	//(if task has input redirect: set stdin of task to that,
		// if it doesnt exist exit(exit_failure))
	}
	if(task->output_file != NULL){
	//if task has output redirect : set stdout of task to output file
		//if it doesnt exist create it
	}

	//default stdout of each step is stdin of next

	//run fork() for the first task (will be used for rest of tasks)
	int pid = fork();
	if(pid < 0){
		//error
	}else if(pid == 0){
		//for(steps in task){ fork() execvp(step[0], step[1...n])}
		STEP *s = task->steps;
		int stepfork = fork();
		if(stepfork < 0){

		}else if(stepfork == 0){

		}
		// while(s != NULL){
		// 	int pid2 =fork();
		// 	if(pid2 < 0){

		// 	}else if(){

		// 	}
		// 	s = s->next;
		// }

	}*/


	//executes tasks for the recipe in sequence (1 cook)
	// TASK *t = recipe->tasks;

}



// void processing(){
// 	if(is_empty(queue_pointer)){

// 	}

// 	if(!(is_empty(queue_pointer)) && ){

// 	}
// }

//input: parent
//checks if node has completed children nodes
int add_parent_to_queue(RECIPE *recipe){
	RECIPE_LINK *temp = recipe->this_depends_on;
	int children_completed = 1;
	while(temp != NULL){
		if(IS_COMPLETED(temp->recipe) == 0){
			return children_completed = 0;
		}
	}

	if(children_completed){
		SET_IN_QUEUE(recipe, 1);
		enqueue(recipe, queue_pointer);
	}
	return children_completed;
}

void print_recipe_helper(RECIPE_LINK *recipe_link, int depth){
	if(recipe_link == NULL){
		return;
	}
	for(int i = 0; i < depth; i++){
        printf(" ");
    }
	do{
		printf("name : %-30s in queue : %d\n", recipe_link->name, IN_QUEUE(recipe_link->recipe));
		print_recipe_helper(recipe_link->recipe->this_depends_on, depth + 1);
		recipe_link = recipe_link->next;
	}while(recipe_link != NULL);

}

void print_recipe(RECIPE *recipe){
	print_recipe_helper(recipe->this_depends_on, 0);
}

void print_leaf(RECIPE *leaf){
	printf("name : %-30s in main recipe : %d\n", leaf->name, IN_MAIN_RECIPE(leaf));
}

void print_list(RECIPE *recipe){
	RECIPE_LINK *next_link = recipe->this_depends_on;
	if(next_link == NULL){
		return;
	}
	do{
		printf("name : %s\n", next_link->name);
		next_link = next_link->next;
	}while(next_link != NULL);
}