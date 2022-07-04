#include <stdio.h>
#include "sfmm.h"
#include "listmacros.h"


int main(int argc, char const *argv[]) {
    // double* ptr = sf_malloc(sizeof(double));


    void *x = sf_malloc(80);
    // sf_show_heap();
    x=x;
    sf_realloc(x, 50);

    // sf_show_heap();
    return EXIT_SUCCESS;
}
