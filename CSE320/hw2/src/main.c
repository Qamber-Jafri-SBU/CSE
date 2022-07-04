
/*
 * File system browser.
 * E. Stark
 * 11/3/93
 */

#include <sys/param.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <curses.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <getopt.h>

#include "browse.h"
#include "options.h"


int main(int argc, char *argv[])
{
  int err;
  char *getcwd(), *base;
  sort_key = -1;

  static struct option long_options[] = {
    {"sort-key",        required_argument,  0, 's'},
    {"human-readable",  no_argument,        0,  'h' },
    {0,                 0,                  0,  0 }
  };

  int human_readable_flag_passed = 0;
  int sort_flag_passed = 0;

  while((option = getopt_long(argc, argv, "s:", long_options, &option_index)) != -1){

    option_index = 0;
    switch(option){
      case 'h':
        if(!human_readable_flag_passed){
            human_readable = 1;
            human_readable_flag_passed = 1;
        }else{
          fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
          exit(1);
        }
        break;
      case 's':
        if(!sort_flag_passed){
        //string comp name|date|size|none
          if(!strcmp("name", optarg)){
            sort_key = 0;
          }
          else if(!strcmp("date", optarg)){
            sort_key = 1;
          }
          else if(!strcmp("size", optarg)){
            sort_key = 2;
          }
          else if(!strcmp("none", optarg)){
            sort_key = 3;
          }
          else{
            fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
            exit(1);
          }
          sort_flag_passed = 1;
        }
        else{
          //sort flag passed twice
          fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
          exit(1);
        }
      break;
      default:
        fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
        exit(1);
    }
  }


  if(argc - optind > 1){
          fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
          exit(1);
  }

  int dir_index = 0;
  if((argc - optind) == 1){
    dir_index = argc - 1;
  }

  if((argc - optind) == 1) {
    if(chdir(argv[dir_index]) < 0) {
      fprintf(stderr, "Can't change directory to %s\n", argv[dir_index]);
      exit(1);
    }
  } else if((argc - optind) != 1 && (argc - optind) != 0) {
    fprintf(stderr, "Usage: %s [dir]\n", argv[0]);
    exit(1);
  }
  base = getcwd(NULL, MAXPATHLEN+1);
  if((cursor_node = get_info(base)) == NULL) {
    fprintf(stderr, "Can't stat %s\n", base);
    exit(1);
  }
  initdisplay();
  cursor_line = 0;
  do redisplay(); while(!(err = command(0)));
  enddisplay();
  exit(err < 0 ? EXIT_FAILURE : EXIT_SUCCESS);
}
