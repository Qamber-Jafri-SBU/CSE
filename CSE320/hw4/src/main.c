#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>


#include <getopt.h>
#include <debug.h>

#include "cookbook.h"
#include "functions.h"
#include "queue.h"

int main(int argc, char *argv[]) {
    COOKBOOK *cbp;
    int err = 0;
    char *cookbook = "rsrc/cookbook.ckb";
    char *main_recipe  = "";
    int max_cooks = 1;
    FILE *in;

    int opt;
    while((opt = getopt(argc, argv, "f:c:")) != -1) {
        switch (opt) {
            case 'f':
                cookbook = optarg;
            break;
            case 'c':
                if((max_cooks = atoi(optarg)) < 1){
                    //ERROR OUT
                    fprintf(stderr, "Max number of cooks cannot be less than 1\n");
                    exit(EXIT_FAILURE);
                }
            break;
            default:
                //ERROR OUT
                fprintf(stderr, "Invalid arguments\n");
                exit(EXIT_FAILURE);

        }
    }

    if((in = fopen(cookbook, "r")) == NULL) {
	fprintf(stderr, "Can't open cookbook '%s': %s\n", cookbook, strerror(errno));
	exit(1);
    }
    cbp = parse_cookbook(in, &err);
    if(err) {
	fprintf(stderr, "Error parsing cookbook '%s'\n", cookbook);
	exit(1);
    }
    // unparse_cookbook(cbp, stdout);

    if(!cbp){
        //error
    }
    if(optind < argc){
        if(optind < argc - 1){
            //ERROR OUT
            fprintf(stderr, "Too many arguments\n");
            exit(EXIT_FAILURE);
        }
        main_recipe = argv[optind];

        //check if main_recipe in cookbook
        int recipe_in_cookbook = 0;
        RECIPE *r = cbp->recipes;
        while(r != NULL){
            if(strcmp(r->name, main_recipe) == 0){
                recipe_in_cookbook = 1;
            }
            r = r->next;
        }
        //if main_recipe not in cookbook exit_failure
        if(!recipe_in_cookbook){
            fprintf(stderr, "Main recipe '%s' not found in cookbook '%s'", main_recipe, cookbook);
            exit(EXIT_FAILURE);
        }
    }else{
        //default value of main_recipe is the first recipe in cookbook
        main_recipe = cbp->recipes[0].name;
    }


    // debug("main_recipe :    %s", main_recipe);

    debug("name %s", (&cbp->recipes[0])->name);
    debug("cookbook : %s, main_recipe: %s", cookbook, main_recipe);
    RECIPE *recipe = &cbp->recipes[0];
    //initialize cookbook for queuing (setting state)
    initialize(cbp, recipe);
    print_recipe(recipe);



    //
    exit(0);
}
