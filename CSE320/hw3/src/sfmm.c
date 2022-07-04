/**
 * Do not submit your assignment with a main function in this file.
 * If you submit with a main function in this file, you will get a zero.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "debug.h"
#include "sfmm.h"
#include "listmacros.h"
#include "errno.h"

int initialized = 0;

void *sf_malloc(size_t size) {

    if(!size){
        return NULL;
    }

    if(!initialized){
        init_mem();
        initialized = 1;
    }

    //account for heading padding (8 bytes) since size is payload size
    size = size + 8;

    //ensure size is multiple of 64
    if(size % 64 != 0){
        size += 64 - (size % 64);
    }

    //get free block from array
    sf_block * new_block = get_free_block(size);

    if(!new_block){
        sf_errno = ENOMEM;
        return NULL;
    }

    //split block if needed; store unused portion into array
    //split when desired size is at least 64 bytes less than new block size
    if(get_size_of_block(new_block) - size >= 64){
        split_block(new_block, size);
    }else{
        create_header(new_block, size, (get_allocation_status(new_block) | ALLOC));
    }

    //return addr of payload of new block
    return (void *) ((char *)new_block + 2*RSIZE);
}

void sf_free(void *pp) {
    if(!validate_pointer(pp)){
        abort();
    }

    //payload pointer - 16 == sf_block
    sf_block *freed_block = (sf_block *)((char *)pp - 16);
    int prev_alloc = get_allocation_status(freed_block) & 0x2;

    //set allocation status to 0, set footer
    create_header(freed_block, get_size_of_block(freed_block), prev_alloc);
    set_footer(freed_block);

    sf_block *next_block = (sf_block *)((char *)freed_block + get_size_of_block(freed_block));
    create_header(next_block, get_size_of_block(next_block), (get_allocation_status(next_block) & 0x1));
    freed_block = coalesce(freed_block);

    debug("");
    // sf_show_free_lists();
    // sf_show_block(freed_block);
    store_block_in_list(freed_block);

    return;
}

void *sf_realloc(void *pp, size_t rsize) {
    if(!validate_pointer(pp)){
        sf_errno = EINVAL;
        return NULL;
    }

    if(!rsize){
        sf_free(pp);
        return NULL;
    }

    sf_block *block = (sf_block *)(( char *) pp - 16);

    rsize += 8;

    if(rsize % 64 != 0){
        rsize += 64 - (rsize % 64);
    }

    int size = get_size_of_block(block);
    if(size < rsize){

        sf_block *new_block = sf_malloc(rsize - 8);

        if(!new_block){
            return NULL;
        }

        // sf_block *temp = (sf_block *)((char *)block);

        memcpy(new_block, block + 16, size - 8);

        sf_free((void *)((char *)block + 16));

        return new_block;
    }else{
        if(rsize == size){
           return (sf_block *)(( char *) block + 16);
        }

        debug("");
        sf_block *remainder = split_block(block, rsize);
        // sf_show_block(remainder);
        remainder = coalesce(remainder);
// sf_show_block(remainder);
        sf_block *prev  = remainder->body.links.prev;
        sf_block *next  = remainder->body.links.next;
        prev->body.links.next = next;
        next->body.links.prev = prev;
        store_block_in_list(remainder);

        // remainder->body.links.prev = prev;
        sf_show_free_lists();
        // store_block_in_list(remainder);
    }

    return (sf_block *)(( char *) block + 16);
}

void *init_mem() {

    void *base_of_heap = sf_mem_start();
    sf_mem_grow();

    if(!base_of_heap){
        return NULL;
    }

    //prologue
    sf_block *prologue = (sf_block *)((char *)base_of_heap + 48);
    prologue->header = 64;
    PUTBITS(prologue->header, ALLOC);


    //first free block
    sf_block *first_block = (sf_block *)((char *)base_of_heap + 48 + 64);
    // first_block->header = (PAGE_SZ - 2*RSIZE*8);
    // PUTBITS(first_block->header, PREV_ALLOC);

    create_block(first_block, PAGE_SZ - 2*RSIZE*8, PREV_ALLOC);

    //epilogue
    sf_block *epilogue = (sf_block *)((char *)(sf_mem_end())- 16);
    epilogue->header = (0);
    PUTBITS(epilogue->header, ALLOC);
    epilogue->prev_footer = PACK((PAGE_SZ - 2*RSIZE*8), PREV_ALLOC);

    //initialize free list array
    for(int i = 0; i < NUM_FREE_LISTS - 1; i++){
        sf_free_list_heads[i].body.links.prev = &sf_free_list_heads[i];
        sf_free_list_heads[i].body.links.next = &sf_free_list_heads[i];
    }

    //link sentinel to first block
    sf_free_list_heads[8].body.links.prev = first_block;
    sf_free_list_heads[8].body.links.next = first_block;

    first_block->body.links.next = &sf_free_list_heads[8];
    first_block->body.links.prev = &sf_free_list_heads[8];

    return first_block;
}

void create_header(sf_block *bp, size_t size, int allocation_status) {
    bp->header = size;
    bp->header = ((bp->header) >> 2) << 2;
    PUTBITS(bp->header, allocation_status);
}

void set_footer(sf_block *curr){
    sf_header h = curr->header;
    long size = get_size_of_block(curr);
    curr = (sf_block *)((char *) curr + size);
    curr->prev_footer = h;
}

int find_size_class(size_t size){
    int fib[8] = {1, 2, 3, 5, 8, 13, 21, 34};
    for(int i = 0; i < NUM_FREE_LISTS - 1; i++){
        if(size <= (fib[i]*64)){
            return i;
        }
    }
    return 8;
}

void store_block_in_list(sf_block *bp){
    int size_class =find_size_class(GET_SIZE(bp->header));

    bp->body.links.next = sf_free_list_heads[size_class].body.links.next;
    sf_free_list_heads[size_class].body.links.next = bp;

    bp->body.links.next->body.links.prev = bp;
    bp->body.links.prev = &sf_free_list_heads[size_class];

}

long get_size_of_block(sf_block *bp){
    return (bp->header & ~0x3f);
}

int get_allocation_status(sf_block *bp){
    return (bp->header & 0x3);
}

sf_block *get_free_block(size_t size){

    //expand heap if free list found is too small
    int size_class = find_size_class(size);
    int block_found = 0;

    //access free list array, and search for first block with size >= size
    for(;size_class < NUM_FREE_LISTS; size_class++){
        if(sf_free_list_heads[size_class].body.links.next != &sf_free_list_heads[size_class]){
            break;
        }
    }

    sf_block *curr_block = &sf_free_list_heads[size_class];
    curr_block = curr_block->body.links.next;
    do{

        if(get_size_of_block(curr_block) >= size){
            block_found = 1;
            break;
        }

        curr_block = curr_block->body.links.next;
    }while(curr_block->body.links.next != sf_free_list_heads[size_class].body.links.next);

    if(block_found){
        //set next's previous
        curr_block->body.links.next->body.links.prev = curr_block->body.links.prev;

        //set prev's next
        curr_block->body.links.prev->body.links.next = curr_block->body.links.next;

        curr_block->body.links.next->header = (curr_block->body.links.next->header >> 2) << 2;
        PUTBITS((curr_block->body.links.next->header), PREV_ALLOC);

        curr_block->body.links.next->body.links.next->prev_footer = (curr_block->body.links.next->body.links.next->prev_footer >> 2) << 2;
        PUTBITS((curr_block->body.links.next->body.links.next->prev_footer), PREV_ALLOC);

        //remove links insides curr block
        // curr_block->body.links.prev = NULL;
        // curr_block->body.links.next = NULL;

        return curr_block;
    }else{
        //block not found; expand heap

        //remove old epilogue
        debug("frogen");

        //grow memory
        void *memory = sf_mem_grow();
        if(!memory){
            sf_errno = ENOMEM;
        }

        //set epilogue to end of new page
        create_block((sf_block *)((char *)sf_mem_end() - 16), 0, ALLOC);

        //new block
        sf_block *new_block = (sf_block *)((char *)memory - 16);

        create_header(new_block, PAGE_SZ, new_block->header & PREV_ALLOC);

        //coalesce
        new_block = coalesce(new_block);

        //store new block in array
        store_block_in_list(new_block);

        //get free block
        return get_free_block(size);
    }

    return NULL;
}

sf_block *split_block(sf_block *bp, size_t size){

    int current_size = get_size_of_block(bp);
    sf_block *next = bp->body.links.next;
    sf_block *prev = bp->body.links.prev;

    prev->body.links.next = next;
    next->body.links.prev = prev;

    //?????????
    int allocation_status = get_allocation_status(bp);

    sf_block *remainder = (sf_block *)((char *)bp + size);

    //reformat header with new size using create header
    create_header(bp, size, (allocation_status | ALLOC));
    set_footer(bp);

    //reformat remainder chunk header/footer with current_size - size
    create_header(remainder, (current_size - size), PREV_ALLOC);
    set_footer(remainder);

    int remainder_size_class = find_size_class((current_size - size));
    next = sf_free_list_heads[remainder_size_class].body.links.next;
    prev = sf_free_list_heads[remainder_size_class].body.links.prev;

    remainder->body.links.next = next;
    remainder->body.links.prev = prev;

    //store remainder in free list
    store_block_in_list(remainder);

    //return remainder
    return remainder;
}

void create_block(sf_block *bp, size_t size, int allocation_status){
    create_header(bp, size, allocation_status);

    if(!(allocation_status & 1)){
        //create free block
        set_footer(bp);
    }
}


int validate_pointer(void *pp){
    // int is_valid =
    if(pp == NULL){
        return 0;
    }

    if((long)pp % 64 != 0){
        return 0;
    }

    //check if header of pp is before beginning of heap
    if((long)&(((sf_block *)pp)->header) < (long)sf_mem_start()){
        return 0;
    }

    //check if footer of pp is after end of heap
    if((long)&((((sf_block *)pp) + get_size_of_block(pp))->prev_footer) < (long)sf_mem_end()){
        return 0;
    }

    if(get_allocation_status(pp) & ALLOC){
        return 0;
    }

    //get allocation status for prev block
    int prev_block_allocation = (((sf_block *)pp)->prev_footer) & 0x1;
    if(!(get_allocation_status(pp) & PREV_ALLOC) && prev_block_allocation){
        return 0;
    }

    return 1;
}

sf_block *coalesce(sf_block *bp){

    //if prev not alloc
    // merge with current bp

    // prev_alloc, curr_alloc
    int prev_allocation_status = (bp->header & 0x2);
    // debug("prev alloc : %d", prev_allocation_status);
    int allocation_status = get_allocation_status(bp);
    int current_size = get_size_of_block(bp);

    sf_block *next = (sf_block *)((char *)bp + current_size);

    int next_allocation_status = get_allocation_status(next);


    //if prev not allocated
    if(!(prev_allocation_status)){
        //get prev size
        int prev_size = (bp->prev_footer & ~0x3f);
        sf_block *prev = (sf_block *)((char *)bp - prev_size);

        prev->body.links.prev->body.links.next = prev->body.links.next;
        prev->body.links.next->body.links.prev = prev->body.links.prev;

        // prev->body.links.next = bp->body.links.next;

        //set prev header
        create_header(prev, prev_size + current_size, ((prev_allocation_status & PREV_ALLOC)));
        //create new footer
        set_footer(prev);

        bp = prev;
    }

    //if next not alloc
    // merge with current bp
    if(!(allocation_status & ALLOC) & !(next_allocation_status & ALLOC)){
        //set bg prev
        int next_size = get_size_of_block(next);

        // debug("%p ", next->body.links.prev);
        next->body.links.prev->body.links.next = next->body.links.next;

        next->body.links.next->body.links.prev = next->body.links.prev;

        create_header(bp, get_size_of_block(bp) + next_size, (get_allocation_status(bp) & PREV_ALLOC));
        set_footer(bp);

    }

    return bp;

    // sf_show_block(bp);

}