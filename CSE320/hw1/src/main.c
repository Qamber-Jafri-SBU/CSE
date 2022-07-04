#include <stdio.h>
#include <stdlib.h>

#include "mtft.h"
#include "debug.h"

#ifdef _STRING_H
#error "Do not #include <string.h>. You will get a ZERO."
#endif

#ifdef _STRINGS_H
#error "Do not #include <strings.h>. You will get a ZERO."
#endif

#ifdef _CTYPE_H
#error "Do not #include <ctype.h>. You will get a ZERO."
#endif

int main(int argc, char **argv)
{
    if(validargs(argc, argv)){
        global_options = 0x0;
        USAGE(*argv, EXIT_FAILURE);
    }
    if(global_options & HELP_OPTION){
        USAGE(*argv, EXIT_SUCCESS);
    }
    // TO BE IMPLEMENTED
    //debug("%x\n", global_options);

    if((global_options & 0xf0000000) == 0x40000000){
        int return_code = mtf_encode();
        if(!return_code){
            return EXIT_SUCCESS;
        }
    }

    if((global_options & 0xf0000000) == 0x20000000){
        int return_code = mtf_decode();
        if(!return_code){
            return EXIT_SUCCESS;
        }
    }

    return EXIT_FAILURE;
}

/*
 * Just a reminder: All non-main functions should
 * be in another file not named main.c
 */
