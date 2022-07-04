#include "__grading_helpers.h"
#include "debug.h"
#include "sfmm.h"

int free_list_idx(size_t size) {
    size_t sizes[] = { 64, 128, 192, 320, 512, 832, 1344, 2176, 3520, 5696, 9216, 14912 };
    int i = 0;
    while(i < NUM_FREE_LISTS-1) {
        if(size <= sizes[i])
            return i;
        i++;
    }
    // If we reach here, the block must be in the last free list,
    // which has arbitrarily large blocks.
    return NUM_FREE_LISTS-1;
}

static bool free_list_is_empty()
{
	for(int i = 0; i < NUM_FREE_LISTS; i++) {
	if(sf_free_list_heads[i].body.links.next != &sf_free_list_heads[i] ||
	   sf_free_list_heads[i].body.links.prev != &sf_free_list_heads[i])
		return false;
   }
   return true;
}

static bool block_is_in_free_list(sf_block *abp)
{
	sf_block *bp = NULL;
	size_t block_size = abp->header & ~0x3f;
	int i = free_list_idx(block_size);
	bp = (&sf_free_list_heads[i])->body.links.next;
	while(bp != &sf_free_list_heads[i]) {
		if (bp == abp)
			return true;
		bp = bp->body.links.next;
	}
	return false;
}

void _assert_free_list_is_empty()
{
	cr_assert(free_list_is_empty(), "Free list is not empty");
}

/*
 * Function checks if all blocks are unallocated in free_list.
 * This function does not check if the block belongs in the specified free_list.
 */
void _assert_free_list_is_valid()
{
	for(int i = 0; i < NUM_FREE_LISTS; i++) {
		sf_block *bp = sf_free_list_heads[i].body.links.next;
		int limit = 10000;
		while(bp != &sf_free_list_heads[i] && limit--) {
			cr_assert(!(bp->header & THIS_BLOCK_ALLOCATED),
			  "Block %p in free list is marked allocated", &(bp->header));
			bp = bp->body.links.next;
		}
		cr_assert(limit != 0, "Bad free list");
	}
}

/**
 * Checks if a block follows documentation constraints
 *
 * @param bp pointer to the block to check
 */
void _assert_block_is_valid(sf_block *bp)
{
	// proper block alignment & size
  size_t aligned_payload = ((size_t)((bp)->body.payload) + 63) & ~63;
	cr_assert(bp->body.payload == (void *)aligned_payload,
	  "Block %p is not properly aligned", &(bp->header));
	  
	size_t block_size = bp->header & ~0x3f;
	cr_assert(block_size >= 64 && block_size <= 100 * PAGE_SZ && ((block_size & 0x3f) == 0),
	  "Block size is invalid for %p. Got: %lu", &(bp->header), block_size);

	sf_block *next_block = (sf_block *)((char *)bp + block_size);
	cr_assert((char *)next_block == (char*)(sf_mem_end() - 16)
		|| ((next_block->header & PREV_BLOCK_ALLOCATED) != 0) == ((bp->header & THIS_BLOCK_ALLOCATED) != 0),
		"Prev allocated bit is not correctly set for %p. Should be: %d",
		&(next_block)->header, (bp->header & 0x1) != 0);

	// other issues to check
	cr_assert((bp->header & THIS_BLOCK_ALLOCATED) || (next_block->prev_footer == bp->header),
		"Block's footer does not match header for %p", &(bp->header));

	if ((bp->header & THIS_BLOCK_ALLOCATED) != 0) {
		cr_assert(!block_is_in_free_list(bp),
			"Allocated block at %p is also in the free list", &(bp->header));
	} else {
		cr_assert(block_is_in_free_list(bp),
		  "Free block at %p is not contained in the free list", &(bp->header));
	}
}

void _assert_heap_is_valid(void)
{
	sf_block *bp;
    int limit = 10000;
    if (sf_mem_start() == sf_mem_end())
        cr_assert(free_list_is_empty(),
            "The heap is empty, but the free list is not");

    size_t align_start = ((size_t)(sf_mem_start() + 8) + 63) & ~63;
    sf_block *prologue = (sf_block *)(align_start - 16);
    sf_block *epilogue = sf_mem_end() - 16;
    char *first_block = ((char *)prologue + 64);

    bp = (sf_block *)first_block;
    while (limit-- && bp < epilogue) {
        _assert_block_is_valid(bp);
        bp = (sf_block *)((char *)bp + (bp->header & ~0x3f));
    }
    _assert_free_list_is_valid();
}

/**
 * Asserts a block's info.
 *
 * @param bp pointer to the beginning of the block.
 * @param alloc The expected allocation bit for the block.
 * @param b_size The expected block size.
 */
void _assert_block_info(sf_block *bp, int alloc, size_t b_size)
{
	cr_assert((bp->header & THIS_BLOCK_ALLOCATED) == alloc,
		  "Block %p has wrong allocation status (got %d, expected %d)",
		  &(bp->header), bp->header & THIS_BLOCK_ALLOCATED, alloc);

	cr_assert((bp->header & ~0x3f) == b_size,
		  "Block %p has wrong block_size (got %lu, expected %lu)",
		  &(bp->header), (bp->header & ~0x3f), b_size);
}

/**
 * Asserts payload pointer is not null.
 *
 * @param pp payload pointer.
 */
void _assert_nonnull_payload_pointer(void *pp)
{
	cr_assert(pp != NULL, "Payload pointer should not be NULL");
}

/**
 * Asserts payload pointer is null.
 *
 * @param pp payload pointer.
 */
void _assert_null_payload_pointer(void * pp)
{
	cr_assert(pp == NULL, "Payload pointer should be NULL");
}

/**
 * Assert the total number of free blocks of a specified size.
 * If size == 0, then assert the total number of all free blocks.
 *
 * @param size the size of free blocks to count.
 * @param count the expected number of free blocks to be counted.
 */
void _assert_free_block_count(size_t size, int count)
{
	int cnt = 0;
	for(int i = 0; i < NUM_FREE_LISTS; i++) {
		sf_block *bp = sf_free_list_heads[i].body.links.next;
		while(bp != &sf_free_list_heads[i]) {
			if(size == 0 || size == (bp->header & ~0x3f))
				cnt++;
			bp = bp->body.links.next;
		}
	}
	if(size)
		cr_assert_eq(cnt, count, "Wrong number of free blocks of size %ld (exp=%d, found=%d)",
			 size, count, cnt);
	else
		cr_assert_eq(cnt, count, "Wrong number of free blocks (exp=%d, found=%d)",
			 count, cnt);
}


/**
 * Assert the sf_errno.
 *
 * @param n the errno expected
 */
void _assert_errno_eq(int n)
{
	cr_assert_eq(sf_errno, n, "sf_errno has incorrect value (value=%d, exp=%d)", sf_errno, n);
}
