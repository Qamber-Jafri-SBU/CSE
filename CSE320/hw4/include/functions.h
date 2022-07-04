#ifndef FUNCTIONS_H
#define FUNCTIONS_H

#include <debug.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

typedef struct RECIPE_STATE{
	unsigned int is_completed:1;
	unsigned int in_main_recipe:1;
	unsigned int in_queue:1;
}RECIPE_STATE;


#define IS_COMPLETED(recipe)				(((RECIPE_STATE *)recipe->state)->is_completed)
#define IN_MAIN_RECIPE(recipe)				(((RECIPE_STATE *)recipe->state)->in_main_recipe)
#define IN_QUEUE(recipe)					(((RECIPE_STATE *)recipe->state)->in_queue)

#define SET_COMPLETED(recipe, x)			(((RECIPE_STATE *)recipe->state)->is_completed = (x))
#define SET_IN_MAIN_RECIPE(recipe, x)		(((RECIPE_STATE *)recipe->state)->in_main_recipe = (x))
#define SET_IN_QUEUE(recipe, x)				(((RECIPE_STATE *)recipe->state)->in_queue = (x))


void initialize(COOKBOOK *cookbook, RECIPE *recipe);
void dependency_analysis(COOKBOOK *cookbook, RECIPE *recipe);
void disconnect_recipe(RECIPE *recipe);

void print_leaf(RECIPE *leaf);
void print_recipe_helper(RECIPE_LINK *recipe_link, int depth);
void print_recipe(RECIPE *recipe);
void print_list(RECIPE *r);
#endif