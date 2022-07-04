#ifndef LISTMACROS_H
#define LISTMACROS_H

#define WSIZE	2	/*Word size (bytes)*/
#define	RSIZE	8 	/*Row size (bytes)*/

/*Pack size, allocation bits into a row*/
#define PACK(size, alloc)	((size) | (alloc))

#define GET(p)							(*(unsigned long *)(p))
#define PUT(p, val)						(*(unsigned long *)(p) = (val))

#define GETBITS(p)						(*(sf_block *)(p))
#define PUTBITS(p, bits)				((p) = (p) | (bits))

#define ALLOC 							1
#define PREV_ALLOC  					2

#define GET_SIZE(p)						(p & ~0x3f)
#define GET_PREV_ALLOC(p)				(GET(p) & 0x2)
#define GET_ALLOC(p)					(GET(p) & 0x1)


#define HDRP(bp)						(bp + RSIZE)
#define PREV_FTRP(bp)					(bp)

// #define NEXT_BLKP(bp)


void *init_mem();

void create_header(sf_block *bp, size_t size, int allocation_status);
void create_block(sf_block *bp, size_t size, int allocation_status);
long get_size_of_block(sf_block *bp);
sf_block *split_block(sf_block *bp, size_t size);
sf_block *get_free_block(size_t size);
int get_allocation_status(sf_block *bp);

int validate_pointer(void *pp);
sf_block *coalesce(sf_block *bp);
void set_footer(sf_block *curr);
void store_block_in_list(sf_block *bp);

#endif