#define TEST_TIMEOUT 15
#include "__grading_helpers.h"

/*
 * Do one malloc and check that the prologue and epilogue are correctly initialized
 */
Test(sf_memsuite_grading, initialization, .timeout = TEST_TIMEOUT)
{
	size_t sz = 1;
	void *p  = sf_malloc(sz);
	cr_assert(p != NULL, "The pointer should NOT be null after a malloc");
	_assert_heap_is_valid();
}

/*
 * Single malloc tests, up to the size that forces a non-minimum block size.
 */
Test(sf_memsuite_grading, single_malloc_1, .timeout = TEST_TIMEOUT)
{
	size_t sz = 1;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 64);
	_assert_heap_is_valid();
	_assert_free_block_count(8000, 1);

	_assert_errno_eq(0);
}

Test(sf_memsuite_grading, single_malloc_32, .timeout = TEST_TIMEOUT)
{
	size_t sz = 32;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 64);
	_assert_heap_is_valid();
	_assert_free_block_count(8000, 1);

	_assert_errno_eq(0);
}

Test(sf_memsuite_grading, single_malloc_64, .timeout = TEST_TIMEOUT)
{
	size_t sz = 64;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 128);
	_assert_heap_is_valid();
	_assert_free_block_count(7936, 1);

	_assert_errno_eq(0);
}

/*
 * Single malloc test, of a size exactly equal to what is left after initialization.
 * Requesting the exact remaining size (leaving space for the header)
 */
Test(sf_memsuite_grading, single_malloc_exactly_one_page_needed, .timeout = TEST_TIMEOUT)
{
	size_t sz = 8000;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 8064);
	_assert_heap_is_valid();
	_assert_free_block_count(0, 0);

	_assert_errno_eq(0);
}

/*
 * Single malloc test, of a size just larger than what is left after initialization.
 */
Test(sf_memsuite_grading, single_malloc_more_than_one_page_needed, .timeout = TEST_TIMEOUT)
{
	size_t sz = 8064;

	void *x = sf_malloc(sz);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info(((sf_block *)((char *)x - 16)), 1, 8128);
	_assert_heap_is_valid();
	_assert_free_block_count(8128, 1);

	_assert_errno_eq(0);
}

/*
 * Single malloc test, of multiple pages.
 */
Test(sf_memsuite_grading, single_malloc_three_pages_needed, .timeout = TEST_TIMEOUT)
{
	size_t sz = 24000;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info(((sf_block *)((char *)x - 16)), 1, 24064);
	_assert_heap_is_valid();
	_assert_free_block_count(384, 1);

	_assert_errno_eq(0);
}

/*
 * Single malloc test, unsatisfiable.
 * There should be one single large block.
 */
Test(sf_memsuite_grading, single_malloc_max, .timeout = TEST_TIMEOUT)
{
	size_t sz = 131072;
	void *x = sf_malloc(sz);
	_assert_null_payload_pointer(x);
	_assert_heap_is_valid();
	_assert_free_block_count(130944, 1);

	_assert_errno_eq(ENOMEM);
}

/*
 * Malloc/free with/without coalescing.
 */
Test(sf_memsuite_grading, malloc_free_no_coalesce, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 300;
	size_t sz3 = 400;

	void * x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);

	void * y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);

	void * z = sf_malloc(sz3);
	_assert_nonnull_payload_pointer(z);

	sf_free(y);

	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);
	_assert_block_info((sf_block *)((char *)y - 16), 0, 320);
	_assert_block_info((sf_block *)((char *)z - 16), 1, 448);
	_assert_heap_is_valid();
	_assert_free_block_count(320, 1);
	_assert_free_block_count(7040, 1);

	_assert_errno_eq(0);
}

Test(sf_memsuite_grading, malloc_free_coalesce_lower, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 300;
	size_t sz3 = 400;
	size_t sz4 = 500;

	void * x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);

	void * y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 320);

	void * z = sf_malloc(sz3);
	_assert_nonnull_payload_pointer(z);
	_assert_block_info((sf_block *)((char *)z - 16), 1, 448);

	void * w = sf_malloc(sz4);
	_assert_nonnull_payload_pointer(w);
	_assert_block_info((sf_block *)((char *)w - 16), 1, 512);

	sf_free(y);
	sf_free(z);

	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);
	_assert_block_info((sf_block *)((char *)y - 16), 0, 768);
	_assert_block_info((sf_block *)((char *)w - 16), 1, 512);
	_assert_heap_is_valid();
	_assert_free_block_count(768, 1);
	_assert_free_block_count(6528, 1);
	
	_assert_errno_eq(0);
}

Test(sf_memsuite_grading, malloc_free_coalesce_upper, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 300;
	size_t sz3 = 400;
	size_t sz4 = 500;

	void * x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);

	void * y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 320);

	void * z = sf_malloc(sz3);
	_assert_nonnull_payload_pointer(z);
	_assert_block_info((sf_block *)((char *)z - 16), 1, 448);

	void * w = sf_malloc(sz4);
	_assert_nonnull_payload_pointer(w);
	_assert_block_info((sf_block *)((char *)w - 16), 1, 512);

	sf_free(z);
	sf_free(y);

	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);
	_assert_block_info((sf_block *)((char *)y - 16), 0, 768);
	_assert_block_info((sf_block *)((char *)w - 16), 1, 512);
	_assert_heap_is_valid();
	_assert_free_block_count(768, 1);
	_assert_free_block_count(6528, 1);

	_assert_errno_eq(0);
}


//changepoint
Test(sf_memsuite_grading, malloc_free_coalesce_both, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 300;
	size_t sz3 = 400;
	size_t sz4 = 500;

	void * x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);

	void * y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 320);

	void * z = sf_malloc(sz3);
	_assert_nonnull_payload_pointer(z);
	_assert_block_info((sf_block *)((char *)z - 16), 1, 448);

	void * w = sf_malloc(sz4);
	_assert_nonnull_payload_pointer(w);
	_assert_block_info((sf_block *)((char *)w - 16), 1, 512);

	sf_free(x);
	sf_free(z);
	sf_free(y);

	_assert_block_info((sf_block *)((char *)x - 16), 0, 1024);
	_assert_heap_is_valid();
	_assert_free_block_count(1024, 1);
	_assert_free_block_count(6528, 1);

	_assert_errno_eq(0);
}

Test(sf_memsuite_grading, malloc_free_first_block, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 300;

	void *x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);

	void *y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 320);

	sf_free(x);

	_assert_block_info((sf_block *)((char *)x - 16), 0, 256);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 320);
	_assert_heap_is_valid();
	_assert_free_block_count(256, 1);
	_assert_free_block_count(7488, 1);

	_assert_errno_eq(0);
} 


Test(sf_memsuite_grading, malloc_free_last_block, .timeout = TEST_TIMEOUT)
{
	size_t sz1 = 200;
	size_t sz2 = 7792;
	void *x = sf_malloc(sz1);
	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);
	void *y = sf_malloc(sz2);
	_assert_nonnull_payload_pointer(y);
	_assert_block_info((sf_block *)((char *)y - 16), 1, 7808);

	sf_free(y);

	_assert_block_info((sf_block *)((char *)x - 16), 1, 256);
	_assert_block_info((sf_block *)((char *)y - 16), 0, 7808);
	_assert_free_block_count(7808, 1);
	_assert_heap_is_valid();

	_assert_errno_eq(0);
}
/*
 * Check that malloc leaves no splinter.
 */
Test(sf_memsuite_grading, malloc_with_splinter, .timeout = TEST_TIMEOUT)
{
	size_t sz = 8001;
	void *x = sf_malloc(sz);

	_assert_nonnull_payload_pointer(x);
	_assert_block_info((sf_block *)((char *)x - 16), 1, 8064);
	_assert_heap_is_valid();
	_assert_free_block_count(0, 0);

	_assert_errno_eq(0);
}

/*
 * Determine if the existing heap can satisfy an allocation request
 * of a specified size.  The heap blocks are examined directly;
 * freelists are ignored.
 */
static int can_satisfy_request(size_t size) {
	size_t asize = (size + sizeof(sf_header) + 71) & ~63;
	if(asize < 64)
		asize = 64;
	sf_block *bp = (sf_block *)(sf_mem_start() + 112);
	while(bp < (sf_block *)(sf_mem_end() - 16)) {
		if(!(bp->header & THIS_BLOCK_ALLOCATED) && asize <= (bp->header & ~0x3f))
			return 1;  // Suitable free block found.
		bp = (sf_block *)((char *)bp + (bp->header & ~0x3f));
	}
	return 0;  // No suitable free block.
}

/*
 *	Allocate small blocks until memory exhausted.
 */
Test(sf_memsuite_grading, malloc_to_exhaustion, .timeout = TEST_TIMEOUT)
{
	size_t size = 90;  // Arbitrarily chosen small size.
	size_t asize = 128;
	// To prevent looping, set an arbitrary limit on the number of allocations.
	// There isn't any way to check here how much the heap might be able to grow.
	int limit = 10000;

	void *x;
	size_t bsize = 0;
	while(limit > 0 && (x = sf_malloc(size)) != NULL) {
		sf_block *bp = (sf_block *)((char *)x - 16);
		bsize = (bp->header & ~0x3f);
		// The allocated block could be slightly larger than the adjusted size,
		// due to splitting restrictions.  In this test, this can occur when the
			// last block in the current heap is big enough, but would leave a splinter
		// when split.	However, we don't check to verify that it is the last block.
		cr_assert(!bsize || bsize == asize || bsize == (asize + 16),
			  "Block has incorrect size (was: %lu, exp: %lu or %lu)",
			  bsize, asize, asize + 16);
		limit--;
	}
	cr_assert_null(x, "Allocation succeeded, but heap should be exhausted.");
	_assert_errno_eq(ENOMEM);
	cr_assert_null(sf_mem_grow(), "Allocation failed, but heap can still grow.");
	cr_assert(!can_satisfy_request(size),
				"Allocation failed, but there is still a suitable free block.");
}
