
/*
 * Process keyboard input and dispatch commands
 */

#include <sys/param.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <curses.h>
#include <dirent.h>
#include <string.h>
#include <strings.h>

#include "browse.h"
#include "options.h"

static void next_line();
static void previous_line();
static void next_screen();
static void previous_screen();
static void open_directory(NODE *dir);

/*
 * Process commands
 * vmode is 1 if in view mode, 0 if in normal mode
 */

int command(int vmode)
{
  int c;

  switch(c = getch()) {
  case 'n':
    next_line();
    break;
  case 'N':
    next_screen();
    break;
  case 'p':
    previous_line();
    break;
  case 'P':
    previous_screen();
    break;
  case 'q':
    if(!vmode) return(1);
    else feep("");
    break;
  case 'o':
    if(!vmode) open_directory(cursor_node);
    else feep("");
    break;
  case 'c':
    if(!vmode) close_directory(cursor_node);
    else feep("");
    break;
  case 'v':
    if(!vmode) view_file(cursor_node);
    else feep("");
    break;
  case 033:  /* ESC */
    if(vmode) return(1);
    else feep("");
    break;
  case '\f':
    refreshdisplay();
    break;
  case ERR:
    return -1;
  default:
    feep("");
    break;
  }
  return(0);
}

/*
 * Move cursor to the next line of information
 */

static void next_line()
{
  if(cursor_node->next != NULL) {
    cursor_node = cursor_node->next;
    if(cursor_line < screen_height-1) cursor_line++;
  } else {
    feep("");
  }
}

/*
 * Move cursor to the previous line of information
 */

static void previous_line()
{
  if(cursor_node->prev != NULL) {
    cursor_node = cursor_node->prev;
    if(cursor_line > 0) cursor_line--;
  } else {
    feep("");
  }
}

/*
 * Move cursor to the next screen of information
 */

static void next_screen()
{
  int i;

  for(i = 0; i < screen_height-1; i++) {
    if(cursor_node->next != NULL) {
      cursor_node = cursor_node->next;
      if(cursor_line < screen_height-1) cursor_line++;
    }
  }
}

/*
 * Move cursor to the previous screen of information
 */

static void previous_screen()
{
  int i;

  for(i = 0; i < screen_height-1; i++) {
    if(cursor_node->prev != NULL) {
      cursor_node = cursor_node->prev;
      if(cursor_line > 0) cursor_line--;
    }
  }
}


int compare_nodes(NODE *a, NODE *b){
  switch(sort_key){
    case 0: //name

      return strcasecmp(a->info->path, b->info->path);
    case 1: //date
      if(a->info->stat.st_mtime > b->info->stat.st_mtime){
        return -1;
      }
      else if(a->info->stat.st_mtime == b->info->stat.st_mtime){
        return 0;
      }
      else{
        return 1;
      }
    case 2: //size
      if(a->info->stat.st_size < b->info->stat.st_size){
        return -1;
      }
      else if(a->info->stat.st_size == b->info->stat.st_size){
        return 0;
      }
      else{
        return 1;
      }
    default:
      return -2;
  }
}

NODE *sort_node(NODE *dir, NODE *new){

  if(dir->next == NULL || dir->next->info->parent != dir){
    return insert_node(dir, new);
  }

  NODE *next = dir->next;
  NODE *prev;
  while(next != NULL && next->info->parent == dir){
    if(compare_nodes(next, new) <= 0){
      if(next->next == NULL){
        return insert_node(next, new);
      }
      prev = next;
      next = next->next;
    }
    else{
      return insert_node(next->prev, new);
    }
  }

  return insert_node(prev, new);
}

/*
 * Insert nodes for the contents of a directory
 */

static void open_directory(NODE *dir)
{
  close_directory(dir);
  NODE *node, *new;
  DIR *d;
  struct dirent *dp;
  char path[MAXPATHLEN+1];

  /* Make sure it's a directory */
  if(dir->info == NULL
     || (dir->info->stat.st_mode & S_IFMT) != S_IFDIR) {
    feep("Not a directory");
    return;
  }
  if((d = opendir(dir->info->path)) == NULL) {
    feep("Can't read directory");
    return;
  }
  node = dir;
  while((dp = readdir(d)) != NULL) {
    /* Don't display ".." */
    if(!strcmp(dp->d_name, "..")) continue;
    strcpy(path, dir->info->path);
    if(strcmp(path, "/")) strcat(path, "/");
    strcat(path, dp->d_name);
    if((new = get_info(path)) == NULL) continue;
    if(new->info->stat.st_dev == dir->info->stat.st_dev
       && new->info->stat.st_ino == dir->info->stat.st_ino) {
      free(new->info);

      free(new->data); //NEW FREE

      free(new);
      continue;       /* Don't display '.' */
    }
    //node = insert_node(node, new);
    node = sort_node(dir, new);
    node->info->parent = dir;
    node->info->level = dir->info->level+1;
  }

  closedir(d);
}

/*
 * Delete nodes under a particular directory
 */

void close_directory(NODE *dir)
{
  while(dir->next != NULL
	&& dir->next->info != NULL
	&& dir->next->info->parent == dir) {
    if((dir->next->info->stat.st_mode & S_IFMT) == S_IFDIR) {
      close_directory(dir->next);
      delete_node(dir);
    } else {
      delete_node(dir);
    }
  }
}
