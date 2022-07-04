#include <stdlib.h>

#include "client_registry.h"
#include "maze.h"
#include "player.h"
#include "debug.h"
#include "server.h"

#include "getopt.h"
#include "csapp.h"
#include "errno.h"

static void terminate(int status);

volatile sig_atomic_t end = 0;

static void sighup_handler(int sig){
    int old_errno = errno;
    end = 1;
    errno = old_errno;
}

static char *default_maze[] = {
  "******************************",
  "***** %%%%%%%%% &&&&&&&&&&& **",
  "***** %%%%%%%%%        $$$$  *",
  "*           $$$$$$ $$$$$$$$$ *",
  "*##########                  *",
  "*########## @@@@@@@@@@@@@@@@@*",
  "*           @@@@@@@@@@@@@@@@@*",
  "******************************",
  NULL
};

CLIENT_REGISTRY *client_registry;

int main(int argc, char* argv[]){
    // Option processing should be performed here.
    // Option '-p <port>' is required in order to specify the port number
    // on which the server should listen.

    int port = -1;
    char *template_file = 0;
    template_file = template_file;
    int opt;
    while((opt = getopt(argc, argv, "p:t:")) != -1){
        switch(opt){
            case 'p':
                port = atoi(optarg);
                break;
            case 't':
                template_file = optarg;
                break;
            default:
                error("arg fail");
                exit(1);
        }
    }

    if(port == -1){
        error("no port given");
        exit(1);
    }

    //get template line by line and put into default maze
{




}
    // Perform required initializations of the client_registry,
    // maze, and player modules.
    client_registry = creg_init();
    maze_init(default_maze);
    player_init();
    debug_show_maze = 1;  // Show the maze after each packet.

    // TODO: Set up the server socket and enter a loop to accept connections
    // on this socket.  For each connection, a thread should be started to
    // run function mzw_client_service().  In addition, you should install
    // shutdown of the server.


    // a SIGHUP handler, so that receipt of SIGHUP will perform a clean
    int listenfd = Open_listenfd(port);

    int old_errno = errno;

    struct sigaction signal = {0};
    signal.sa_handler = sighup_handler;
    // signal.sa_flags &= ~(SIGPIPE);
    sigemptyset(&signal.sa_mask);

    if(sigaction(SIGHUP, &signal, NULL) < 0){
        errno = 1;
        debug("fail sig install");
        return -1;
    }

    errno = old_errno;

    int *connfdp;
    pthread_t tid;
    socklen_t clientlen;
    struct sockaddr_storage clientaddr;

    while(1){
        clientlen=sizeof(struct sockaddr_storage);
        if(end){
            terminate(EXIT_SUCCESS);
        }
        connfdp = Malloc(sizeof(int));
        if(((*connfdp = accept(listenfd, (SA *) &clientaddr, &clientlen)) != -1) && errno != EINTR){
            Pthread_create(&tid, NULL, mzw_client_service, connfdp);
        }
    }

    terminate(EXIT_FAILURE);
}

/*
 * Function called to cleanly shut down the server.
 */
void terminate(int status) {
    // Shutdown all client connections.
    // This will trigger the eventual termination of service threads.
    creg_shutdown_all(client_registry);

    debug("Waiting for service threads to terminate...");
    creg_wait_for_empty(client_registry);
    debug("All service threads terminated.");

    // Finalize modules.
    creg_fini(client_registry);
    player_fini();
    maze_fini();

    debug("MazeWar server terminating");
    exit(status);
}