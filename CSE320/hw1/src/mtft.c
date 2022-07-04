/*
 * MTF: Encode/decode using "move-to-front" transform and Fibonacci encoding.
 */

#include <stdlib.h>
#include <stdio.h>

#include "mtft.h"
#include "debug.h"
#include "asciicodes.h"
#define CHAR_SIZE sizeof(char)
#define LONG_SIZE sizeof(long)

struct zeckendorf
{
    unsigned long zeck_rep;
    int num_times_shifted_right;
    int leading_zeros;
};

struct zeckendorf int_to_zeckendorf(int value);
int zeckendorf_to_int(struct zeckendorf zep);
int get_largest_fib_less_than_n(int n);
int get_index_of_largest_fib_less_than_n(int n);
int get_nth_fib_number(int n);
void shift_array_after_code(CODE code);
CODE shift_array(SYMBOL sym);
SYMBOL shift_array_to_code(CODE code);
int power(int base, int exp);

void clear_node(MTF_NODE *node);
MTF_NODE* get_new_node();
void expand_tree();
void print_tree_helper();
void print_node(MTF_NODE *node);
void recycle_node(MTF_NODE *node);
int array_initialized = 0;
int number_of_bytes = 0;

int depth_of_tree = -1;
int size_of_alphabet = 0;
int nodes_used = 0;

/**
 * print tree X
 * adding a leaf node X
 * one for getting a new node to use X
 * one for recylcing a node X
 * one for expanding the tree X
 * clearing out a node X
 * global var for depth of tree which should be incremented after expanding tree X
 * post order traversal X
 * removing a leaf node X
 */

//assumes given offset is correct
//offset is right aligned
//if depth of tree == 0, expand tree should be called before
MTF_NODE* add_node(MTF_NODE *node, OFFSET offset, int height, SYMBOL symbol){
    /**
     * if height == 0 -> return
     * read offset, bit by bit to reach where to place node (offset >> (sizeof(offset)*8 - 1))
     *      -if bit == 0, increment current node's left child, update current node info
     *          -if current node.left_child == null -> get_new_node and link parent and child
     *          add_node(node->left_child, offset << 1, height - 1)
     * else -if  bit == 1, increment, current node's right child, update current node info
     *          -if current node.right_child == null -> get_new_node and link parent and child
     *          add_node(node->right_child, offset << 1, height - 1)
     */
    //debug("height : %i", height);
    //print_node(node);
    if(height == 0){
        node->symbol = symbol;
        return node;
    }
    MTF_NODE *leaf = NULL;
    //debug("node : %p", node);
    int child_direction = (offset >> (height - 1)) & 1;
    if(child_direction == 0){
        node->left_count = node->left_count + 1; //test if ++ syntax works
        if(node->left_child == NULL){
            MTF_NODE *new_child = get_new_node();
            clear_node(new_child);
            //debug("new left child!!!");
            //print_node(new_child);
            node->left_child = new_child;
            new_child->parent = node;
        }
        //need to access next bit
        leaf = add_node(node->left_child, offset, height - 1, symbol);
    }
    else if(child_direction == 1){
        node->right_count = node->right_count + 1; //test if ++ syntax works
        if(node->right_child == NULL){
            MTF_NODE *new_child = get_new_node();
            clear_node(new_child);
            //debug("new right child!!!");
            //print_node(new_child);
            node->right_child = new_child;
            new_child->parent = node;
        }
        //need to access next bit
        leaf = add_node(node->right_child, offset, height - 1, symbol);
    }
    return leaf;
}

MTF_NODE* add_leaf_node_to_tree(OFFSET offset, SYMBOL symbol){
    //initialize root
    if(mtf_map == NULL){
        mtf_map = (node_pool + first_unused_node_index++);
        clear_node(mtf_map);
        mtf_map->symbol = symbol;
        depth_of_tree++;
        return mtf_map;
    }
    if(depth_of_tree == 0 || offset + 1 > (1 << depth_of_tree)){
        expand_tree();
    }
    //debug("offset : %li", offset );
    //debug("depth : %i", depth_of_tree);
    return add_node(mtf_map, offset, depth_of_tree, symbol);
}

void remove_child(MTF_NODE* parent, MTF_NODE* child){
    //CHANGE (parent->left_child == NULL && parent->right_child == NULL)
    if(parent == NULL || (parent->left_child == NULL && parent->right_child == NULL)){
        return;
    }
    if(parent->left_child == child){
        parent->left_child = NULL;
    }
    else if(parent->right_child == child){
        parent->right_child = NULL;
    }
 }

int delete_leaf_node_from_tree(MTF_NODE *node){
    //node is node to delete
    MTF_NODE *curr_node = node->parent;
    MTF_NODE *child_node = node;
    int rank = 0;

    //delete leaf
    if(curr_node->left_child == child_node){
        rank += curr_node->right_count;
        curr_node->left_count = curr_node->left_count - 1;
        curr_node->left_child = NULL;

        clear_node(child_node);
        recycle_node(child_node);
    }

    else if(curr_node->right_child == child_node){
        curr_node->right_count = curr_node->right_count - 1;
        curr_node->right_child = NULL;

        clear_node(child_node);
        recycle_node(child_node);

    }

    for(int i = 0; i < depth_of_tree; i++){

        child_node = curr_node;
        curr_node = child_node->parent;


        if(curr_node == NULL){
            return rank;
        }


        if(curr_node->left_child == child_node){
            rank += curr_node->right_count;
            curr_node->left_count = curr_node->left_count - 1;

            // if(child_node->right_child == NULL && child_node->left_child == NULL){

            //     clear_node(child_node);
            //     recycle_node(child_node);
            // }
        }

        else if(curr_node->right_child == child_node){
            curr_node->right_count = curr_node->right_count - 1;

            // if(child_node->right_child == NULL && child_node->left_child == NULL){
            //     clear_node(child_node);
            //     recycle_node(child_node);
            // }

        }

        if(child_node->left_child == NULL && child_node->right_child == NULL){
            remove_child(curr_node, child_node);
            clear_node(child_node);
            recycle_node(child_node);
        }

    }

    return rank;
}

//clears never before used nodes
MTF_NODE* get_new_node(){
    //check if recycle node list is not empty
    MTF_NODE *new_node;
    if(recycled_node_list != NULL){
        new_node = recycled_node_list;
        recycled_node_list = recycled_node_list->left_child;
        clear_node(new_node);
        //debug("debug node like bruh");
        //print_node(new_node);
        return new_node;
    }
    //if recycling empty return new node
    new_node = node_pool + first_unused_node_index++;
    clear_node(new_node);
    nodes_used++;
    return (new_node);
}

MTF_NODE* get_to_leaf_helper(MTF_NODE* node, OFFSET offset, int height){
    if(height == 0){
        return node;
    }
    MTF_NODE *leaf = NULL;
    //debug("node : %p", node);
    int child_direction = (offset >> (height - 1)) & 1;

    if(child_direction == 0){
        //need to access next bit
        leaf = get_to_leaf_helper(node->left_child, offset, height - 1);
    }
    else if(child_direction == 1){

        leaf = get_to_leaf_helper(node->right_child, offset, height - 1);
    }
    return leaf;
}

//returns leaf node
MTF_NODE* get_to_leaf(OFFSET offset){
    return get_to_leaf_helper(mtf_map, offset, depth_of_tree);
}

//put node into recycling list
void recycle_node(MTF_NODE *node){

    //clear node
    clear_node(node);

    //put node in recycled node list

    // if(recycled_node_list == NULL){
    //     recycled_node_list = node;
    //     return;
    // }
    node->left_child = recycled_node_list;
    recycled_node_list = node;
}

//adds a new root to the tree
void expand_tree(){

    //add new root,
    MTF_NODE *new_root = get_new_node();
    clear_node(new_root);
    //debug("new_root : %p", new_root);
    new_root->left_child = mtf_map;
    new_root->right_child = NULL;
    new_root->left_count = mtf_map->left_count + mtf_map->right_count;
    if(mtf_map->left_child == NULL && mtf_map->right_child == NULL){
        new_root->left_count = 1;
    }
    new_root->right_count = 0;
    new_root->symbol = NO_SYMBOL;
    mtf_map->parent = new_root;

    mtf_map = new_root;
    depth_of_tree++;
}

//clear node
void clear_node(MTF_NODE *node){
    node -> left_child = NULL;
    node -> right_child = NULL;
    node -> parent = NULL;
    node -> left_count = 0;
    node -> right_count = 0;
    node -> symbol = NO_SYMBOL;
}

void print_node(MTF_NODE *node){
    printf("{node = %p, left_child = %p, right_child = %p, parent = %p, left_count = %i, right_count = %i, symbol = %i/%c}\n", node,
        node->left_child, node->right_child, node->parent, node->left_count, node->right_count, node-> symbol, (char)(node->symbol));
}

//print in preorder traversal
void print_tree(MTF_NODE *node, int depth){
    if(node == NULL){
        return;
    }

    print_node(node);

    for(int i = 0; i < depth; i++){
        printf("\t");
    }
    print_tree(node->left_child, depth + 1);

    printf("\n");
    for(int i = 0; i < depth; i++){
        printf("\t");
    }
    print_tree(node->right_child, depth + 1);
}

void print_tree_helper(){
    printf("DEPTH : %i\n", depth_of_tree);
    print_tree(mtf_map, depth_of_tree);
    printf("\n");
}

//post order traversal
int check_tree(MTF_NODE *node, MTF_NODE *parent, int depth) {
    if(node == NULL){
        return 0;
    }
    if(node->left_child == NULL && node->right_child == NULL){
        return 1;
    }

    int number_of_children = 0;

    int left_children = check_tree(node->left_child, node, depth + 1);

    int right_children = check_tree(node->right_child, node, depth + 1);

    //check if left children matches with node
    if(left_children != node->left_count){
        debug("NODE %p @ DEPTH %i   :   actual left count, %i, does not match node's recorded left count, %i!!!", node, depth, left_children, node->left_count);
    }

    //check if right children matches with node
    if(right_children != node->right_count){
        debug("NODE %p @ DEPTH %i   :   actual right count, %i, does not match node's recorded right count, %i!!!", node, depth, right_children, node->right_count);
    }

    //if sym != no_sym and left_child != null || right_child != null error
    if(node->symbol != NO_SYMBOL && (node->left_child != NULL || node->right_child != NULL)){
        debug("NODE %p @ DEPTH %i   :   symbol in NONLEAF node, LEFT child : %p, RIGHT child : %p!!!", node, depth, node->left_child, node->right_child);
    }

    //check if parent matches with node
    if(node->parent != parent){
        debug("NODE %p @ DEPTH %i   :   actual parent, %p, does not match the node's recorded parent, %p", node, depth, parent, node->parent);
    }
    return number_of_children = left_children + right_children;
}

void check_tree_helper(){
    check_tree(mtf_map, NULL, depth_of_tree);
}

void initialize_offsets(){
    if(!array_initialized){
        for(int i = 0; i< SYMBOL_MAX; i++){
            *(last_offset + i) = NO_SYMBOL;
        }
        array_initialized = 1;
    }
}

MTF_NODE* get_leaf_from_rank(CODE rank){
    MTF_NODE* node = mtf_map;

    for(int i = 0; i < depth_of_tree; i++){
        if(node == NULL){
            debug("crybaby time");
            return NULL;
        }
        if(rank < node->right_count){
            node =  node->right_child;
        }
        else{
            rank = rank - node->right_count;
            node = node->left_child;
        }
    }

    return node;

}


/**
 * @brief  Given a symbol value, determine its encoding (i.e. its current rank).
 * @details  Given a symbol value, determine its encoding (i.e. its current rank)
 * according to the "move-to-front" transform described in the assignment
 * handout, and update the state of the "move-to-front" data structures
 * appropriately.
 *
 * @param sym  An integer value, which is assumed to be in the range
 * [0, 255], [0, 65535], depending on whether the
 * value of the BYTES field in the global_options variable is 1 or 2.
 *
 * @return  An integer value in the range [0, 511] or [0, 131071]
 * (depending on the value of the BYTES field in the global_options variable),
 * which is the rank currently assigned to the argument symbol.
 * The first time a symbol is encountered, it is assigned
 * a default rank computed by adding 256 or 65536 to its value.
 * A symbol that has already been encountered is assigned a rank in the
 * range [0, 255], [0, 65535], according to how recently it has occurred
 * in the input compared to other symbols that have already been seen.
 * For example, if this function is called twice in succession
 * with the same value for the sym parameter, then the second time the
 * function is called the value returned will be 0 because the symbol will
 * have been "moved to the front" on the previous call.
 *
 * @modifies  The state of the "move-to-front" data structures.
 */
CODE mtf_map_encode(SYMBOL sym) {
    // TO BE IMPLEMENTED.

/** ARRAY IMPLEMENTATION
    if(sym == NO_SYMBOL){
        return NO_SYMBOL;
    }

    int shift = 0;

    if(number_of_bytes == 1){
        shift = 256;
    }

    if(number_of_bytes == 2){
        shift =  65536;
    }

    if(!array_initialized){
        for(int i = 0; i < shift*2; i++){
            *(symbol_rank + i) = -1;
        }
        array_initialized = 1;
    }


    int *symbol_array = symbol_rank;

    for(int i = 0; i < shift*2; i++){
        if(sym == *(symbol_array + i)){
            shift_array(sym);
            *symbol_array = sym;
            return i;
        }
    }

    //if not in array
    *(symbol_array + shift + sym) = sym;
    shift_array(sym);
    *symbol_array = sym;
    return (shift + sym);
*/
/** TREE IMPLEMENTATION */
    if(sym == NO_SYMBOL){
        return NO_SYMBOL;
    }

    int shift = 0;

    if(number_of_bytes == 1){
        shift = 256;
    }

    if(number_of_bytes == 2){
        shift =  65536;
    }

    initialize_offsets();
    int rank = sym + shift;

    OFFSET offset = *(last_offset + sym);

    if(offset != NO_SYMBOL){
        MTF_NODE* node = get_to_leaf(offset);
        //debug("bruh node : %p", node);
        rank = delete_leaf_node_from_tree(node);
    }
    add_leaf_node_to_tree(current_offset, sym);
    //check_tree_helper();
    //print_tree_helper();
    //debug("RANKG :DDD       : %i", rank);
    *(last_offset + sym) = current_offset;
    current_offset++;
    return rank;

}

/**
 * @brief Given an integer code, return the symbol currently having that code.
 * @details  Given an integer code, interpret the code as a rank, find the symbol
 * currently having that rank according to the "move-to-front" transform
 * described in the assignment handout, and update the state of the
 * "move-to-front" data structures appropriately.
 *
 * @param code  An integer value, which is assumed to be in the range
 * [0, 511] or [0, 131071], depending on the value of the BYTES field in
 * the global_options variable.
 *
 * @return  An integer value in the range [0, 255] or [0, 65535]
 * (depending on value of the BYTES field in the global_options variable),
 * which is the symbol having the specified argument value as its current rank.
 * Argument values in the upper halves of the respective input ranges will be
 * regarded as the default ranks of symbols that have not yet been encountered,
 * and the corresponding symbol value will be determined by subtracting 256 or
 * 65536, respectively.  Argument values in the lower halves of the respective
 * input ranges will be regarded as the current ranks of symbols that
 * have already been seen, and the corresponding symbol value will be
 * determined in accordance with the move-to-front transform.
 *
 * @modifies  The state of the "move-to-front" data structures.
 */
SYMBOL mtf_map_decode(CODE code) {
    // TO BE IMPLEMENTED.
/** ARRAY IMPLEMENTATION :D
    if(code == NO_SYMBOL){
        return NO_SYMBOL;
    }

    int shift = 0;

    if(number_of_bytes == 1)
        shift = 256;

    if(number_of_bytes == 2)
        shift =  65536;


    if(code > shift*2){
        return -1;
    }

    if(!array_initialized){
        for(int i = 0; i < shift*2; i++){
            *(symbol_rank + i) = -1;
        }
        array_initialized = 1;
    }

    int *symbol_array = symbol_rank;

    if(code < shift){
        SYMBOL s = *(symbol_array + code);
        if(*(symbol_array + code) == NO_SYMBOL){
            *(symbol_array + code) = code;
            s = *(symbol_array + code);
        }
        shift_array_to_code(code);
        *symbol_array = s;
        return s;
    }

    //if not in array
    *(symbol_array + code) = code - shift;
    shift_array_to_code(code);
    *symbol_array = code - shift;
    return *(symbol_rank);
*/
/** TREE IMPLEMENTATION **/

    initialize_offsets();

    if(code == NO_SYMBOL){
        return NO_SYMBOL;
    }

    int shift = 0;

    if(number_of_bytes == 1)
        shift = 256;

    if(number_of_bytes == 2)
        shift =  65536;


    SYMBOL sym = code - shift;
    if(sym <= NO_SYMBOL){
        //debug("SYMBOL : %i", sym);
        MTF_NODE *node = get_leaf_from_rank(code);
        debug("noed smyjmojiolds node:      %p", node);
        //print_tree_helper();
        sym = node->symbol;
        delete_leaf_node_from_tree(node);
        //print_tree_helper();
    }

    add_leaf_node_to_tree(current_offset, sym);
    current_offset++;
    debug("SYMBOL       :   %i", sym);
    return sym;

}

/**
 * @brief  Perform data compression.
 * @details  Read uncompressed data from stdin and write the corresponding
 * compressed data to stdout, using the "move-to-front" transform and
 * Fibonacci coding, as described in the assignment handout.
 *
 * Data is read byte-by-byte from stdin, and each group of one or two
 * bytes is interpreted as a single "symbol" (according to whether
 * the BYTES field of the global_options variable is 1 or 2).
 * Multi-byte symbols are constructed according to "big-endian" byte order:
 * the first byte read is used as the most-significant byte of the symbol
 * and the last byte becomes the least-significant byte.  The "move-to-front"
 * transform is used to map each symbol read to its current rank.
 * As described in the assignment handout, the range of possible ranks is
 * twice the size of the input alphabet.  For example, 1-byte input symbols
 * have values in the range [0, 255] and their ranks have values in the
 * range [0, 511].  Ranks in the lower range are used for symbols that have
 * already been encountered in the input, and ranks in the upper range
 * serve as default ranks for symbols that have not yet been seen.
 *
 * Once a symbol has been mapped to a rank r, Fibonacci coding is applied
 * to the value r+1 (which will therefore always be a positive integer)
 * to obtain a corresponding code word (conceptually, a sequence of bits).
 * The successive code words obtained as each symbol is read are concatenated
 * and the resulting bit string is blocked into 8-bit bytes (according to
 * "big-endian" bit order).  These 8-bit bytes are written successively to
 * stdout.
 *
 * When the end of input is reached, any partial output byte is "padded"
 * with 0 bits in the least-signficant positions to reach a full 8-bit
 * byte, which is emitted as the final byte in the output.
 *
 * Note that the transformation performed by this function is to be done
 * in an "online" fashion: output is produced incrementally as input is read,
 * without having to read the entire input first.
 * Note also that this function does *not* attempt to "close" its input
 * or output streams.
 *
 * @return 0 if the operation completes without error, -1 otherwise.
 */
int mtf_encode() {
    // TO BE IMPLEMENTED

    number_of_bytes = global_options & 0x0000000f;
    int length_of_representation = 0;
    int error = 0;
    long representation = 0;

    long input = getchar();

    while(input != -1){
        if(number_of_bytes == 2){
            int input2 = getchar();
            //debug("char1 : %c   char2 : %c", (char)(input), input2);
            if(input2 == -1){ //ba na na \n (-1)
                error = -1;
                input = -1;
            }else{
                input = input << 8;
                input = input | input2;
                //debug("input : %ld", input);
            }

        }
        //debug("input %c", (char)input);
        CODE x = mtf_map_encode(input);
        //debug("nodes used :     %i", nodes_used);
        //debug("current_offset :     %li", current_offset);
        //debug("x : %i", x);
        struct zeckendorf zrep = int_to_zeckendorf(x);
        //debug("zrep : %lx", zrep.zeck_rep);

        if(!(zrep.num_times_shifted_right == -1)){
            zrep.zeck_rep = zrep.zeck_rep >> length_of_representation;
            representation = representation | zrep.zeck_rep;
            length_of_representation += zrep.num_times_shifted_right + 1;
        }


        while(length_of_representation >= 8){
            putchar((representation & ~(-1ul >> 8)) >> 8*(sizeof(representation)- 1));
            representation = representation << 8;
            length_of_representation -= 8;;
        }
        input = getchar();
    }

    while(representation){
            putchar((representation & ~(-1ul >> 8)) >> sizeof(representation)*7);
            representation = representation << 8;
    }
    if(error){
        return -1;
    }
    return 0;

}

/**
 * @brief  Perform data decompression, inverting the transformation performed
 * by mtf_encode().
 * @details Read compressed data from stdin and write the corresponding
 * uncompressed data to stdout, inverting the transformation performed by
 * mtf_encode().
 *
 * Data is read byte-by-byte from stdin and is parsed into individual
 * Fibonacci code words, using the fact that two consecutive '1' bits can
 * occur only at the end of a code word.  The terminating '1' bits are
 * discarded and the remaining bits are interpreted as describing the set
 * of terms in the Zeckendorf sum representing a positive integer.
 * The value of the sum is computed, and one is subtracted from it to
 * recover a rank.  Ranks in the upper half of the range of possible values
 * are interpreted as the default ranks of symbols that have not yet been
 * seen, and ranks in the lower half of the range are interpreted as ranks
 * of symbols that have previously been seen.  Using this interpretation,
 * together with the ranking information maintained by the "move-to-front"
 * heuristic, the rank is decoded to obtain a symbol value.  Each symbol
 * value is output as a sequence of one or two bytes (using "big-endian" byte
 * order), according to the value of the BYTES field in the global_options
 * variable.
 *
 * Any 0 bits that occur as padding after the last code word are discarded
 * and do not contribute to the output.
 *
 * Note that (as for mtf_encode()) the transformation performed by this
 * function is to be done in an "online" fashion: the entire input need not
 * (and should not) be read before output is produced.
 * Note also that this function does *not* attempt to "close" its input
 * or output streams.
 *
 * @return 0 if the operation completes without error, -1 otherwise.
 */
int mtf_decode() {
    // TO BE IMPLEMENTED
    number_of_bytes = global_options & 0x0000000f;

    int length_of_representation = 0;
    long representation = 0;
    int prev_is_one = 0;

    long input = getchar();
    long  temp = 0;
    int temp_length = 0;

    while(input != -1){
        temp = input << 8*(LONG_SIZE - CHAR_SIZE);
        temp_length += CHAR_SIZE*8;

        while(temp_length){

            representation = representation | ((temp & ~(-1ul >> 1)) >> length_of_representation);
            length_of_representation++;

            if(prev_is_one & ((temp & ~(-1ul >> 1)) >> (8*(LONG_SIZE) - 1))){


                struct zeckendorf z;
                z.zeck_rep = representation;
                z.num_times_shifted_right = length_of_representation - 1;

                CODE code = zeckendorf_to_int(z);
                int c = mtf_map_decode(code);;
                if(c == -1 ){
                    return -1;
                }

                if(number_of_bytes == 2){
                    //debug("c1   : %c", c >> 8);
                    putchar(c >> 8);
                }
                //debug("c2   : %c", c);
                putchar(c);

                representation = representation <<  length_of_representation;
                length_of_representation = 0;

                prev_is_one = 0;
                temp = temp << 1;
                temp_length--;
                continue;
            }

            prev_is_one = ((temp & ~(-1ul >> 1)) >> (8*(LONG_SIZE) - 1));
            temp = temp << 1;
            temp_length--;

        }

        input = getchar();
    }


    return 0;
}

/**
 * @brief Validates command line arguments passed to the program.
 * @details This function will validate all the arguments passed to the
 * program, returning 0 if validation succeeds and -1 if validation fails.
 * Upon successful return, the various options that were specified will be
 * encoded in the global variable 'global_options', where it will be
 * accessible elsewhere in the program.  For details of the required
 * encoding, see the assignment handout.
 *
 * @param argc The number of arguments passed to the program from the CLI.
 * @param argv The argument strings passed to the program from the CLI.
 * @return 0 if validation succeeds and -1 if validation fails.
 * @modifies global variable "global_options" to contain an encoded representation
 * of the selected program options.
 */

int validargs(int argc, char **argv) {
    // TO BE IMPLEMENTED

    if(argc < 2){
        return -1;
    }
    argv++;

    int help_not_passed_first = 0;
    int decode_checked = 0;
    int encode_checked = 0;
    for(char **p = argv; *p != NULL; p++){

        char *char_of_p = *p;

        if( *char_of_p == ASCII_DASH){
            if(!*(char_of_p + 1)){
                return -1;
            }
            else if( *(char_of_p + 1) == ASCII_H){
                if(*(char_of_p + 2) != 0){
                    return -1;
                }
                if(help_not_passed_first){
                    return -1;
                }
                global_options = HELP_OPTION;
                return 0;
            }
            else if( *(char_of_p + 1) == ASCII_D && !encode_checked){
                //decode settings
                if(*(char_of_p + 2) != 0){
                    return -1;
                }
                if(decode_checked){
                    return -1;
                }
                global_options = DECODE_OPTION;
                global_options = global_options | 1;
                decode_checked = 1;
            }
            else if( *(char_of_p + 1) == ASCII_E && !decode_checked){
                //encode settings
                if(*(char_of_p + 2) != 0){
                    return -1;
                }
                if(encode_checked){
                    return -1;
                }
                global_options = ENCODE_OPTION;
                global_options = global_options | 1;
                encode_checked = 1;
            }
            else if( *(char_of_p + 1) == ASCII_B && (encode_checked || decode_checked)){
                if(*(char_of_p + 2) != 0){
                    return -1;
                }
                p++;
                char_of_p = *p;
                if(*p == NULL){
                    return -1;
                }
                // DO IF BYTE START WITH 0
                char *pptr = *p;
                while(*char_of_p == ASCII_ZERO){
                    char_of_p = pptr++;
                }
                if(*char_of_p == ASCII_ONE || *char_of_p == ASCII_TWO){
                if(*(char_of_p + 1) != 0){
                    return -1;
                }
                    global_options = global_options & 0xf0000000;
                    global_options = global_options | (*char_of_p - ASCII_ZERO);
                }else{
                    return -1;
                }
            }else{
                //garbage args
                return -1;
            }
            //in case -h is passed after some other flag
            help_not_passed_first = 1;
        }else{
            //garbage args
            return -1;
        }


    }

    return 0;
}

/**
 * @brief Gets largest fibonacci number less than n
 *
 * @param n The integer to get the largest fibonacci number less than it
 * @return largest fibonacci number less than n
 */
int get_largest_fib_less_than_n(int n){

    int fib_curr = 1, fib_prev = 1;
    int temp = 0;

    if(n < 1){
        return 0;
    }
    while(fib_curr <= n){
        temp = fib_curr;
        fib_curr += fib_prev;
        fib_prev = temp;
    }

    return fib_prev;

}

/**
 * @brief Gets index of the largest fibonacci number less than n
 *
 * @param n The integer to get the index of the largest fibonacci number less than it
 * @return  index of the largest fibonacci number less than n
 */
int get_index_of_largest_fib_less_than_n(int n){

    int fib_curr = 1, fib_prev = 1;
    int index = 0, temp = 0;

    if(n < 1){
        return 0;
    }
    while(fib_curr <= n){
        temp = fib_curr;
        fib_curr += fib_prev;
        fib_prev = temp;
        index++;
    }

    return index;
}

/**
 * @brief Gets nth fibonacci number
 *
 * @param n The index of the nth fibonacci number
 * @return  the nth fibonacci number
 */
int get_nth_fib_number(int n){
    int fib_curr = 1, fib_prev = 1;
    int temp = 0;

    if(n < 1){
        return 0;
    }
    for(int i = 1; i <= n; i++){
        temp = fib_curr;
        fib_curr += fib_prev;
        fib_prev = temp;
    }

    return fib_prev;
}

//RETURN POINTER TO STRUCT INSTEAD
/**
 * @brief Converts an integer to it Zeckendorf representation. (int_to_zeckendorf : W -> N)
 *
 * @param value The value of the integer to be converted
 * @return string containing the zeckendorf representation
 */
struct zeckendorf int_to_zeckendorf(int value){
    unsigned long zeckendorf_rep = ~(-1ul >> 1);
    struct zeckendorf output_rep;
    output_rep.zeck_rep = -1;
    output_rep.num_times_shifted_right = -1;
    output_rep.leading_zeros = -1;
    if(value == -1){
        return output_rep;
    }
    value++;

    int curr_index = get_index_of_largest_fib_less_than_n(value);

    int number_of_times_shifted_right = 0;

    int fib = get_largest_fib_less_than_n(value);
    int leading_zeroes = 0;
    while(value >= 1){
        leading_zeroes = 0;
        zeckendorf_rep = zeckendorf_rep >> 1;
        number_of_times_shifted_right++;
        zeckendorf_rep = zeckendorf_rep | ~(-1ul >> 1);
        value = value - fib;

        int prev_index = curr_index;
        curr_index = get_index_of_largest_fib_less_than_n(value);
        fib = get_largest_fib_less_than_n(value);

        for(int i = 1; i < prev_index - curr_index; i++){
            zeckendorf_rep = zeckendorf_rep >> 1;
            number_of_times_shifted_right++;
            leading_zeroes++;
        }
    }

    output_rep.zeck_rep = zeckendorf_rep;
    output_rep.num_times_shifted_right = number_of_times_shifted_right;
    output_rep.leading_zeros = leading_zeroes;
    return output_rep;
}

/**
 * @brief Converts Zeckendorf representation of a number to its integer value
 *
 * @param zeckendorf_rep The zeckendorf representation (string) of a number
 * @return integer value of the zeckendorf representation, or 0 if an error
 */
int zeckendorf_to_int(struct zeckendorf rep){
    //if error return 0
    unsigned long bitstring = rep.zeck_rep;
    int value = 0;
    // debug("bruh %li", bitstring);
    // debug("bruh %lx", bitstring);
    for(int i = 1; i <= rep.num_times_shifted_right; i++){
        //debug("%lx", bitstring);
        //debug("%li", (bitstring & ~(-1ul >> 1)));
        if(bitstring & ~(-1ul >> 1)){
            value += get_nth_fib_number(i);
            //debug("%i", value);
        }
    bitstring = bitstring << 1;
    }
    return value - 1;
}


void shift_array_after_code(CODE code){
    CODE *arrptr = symbol_rank;
    SYMBOL curr_val = *arrptr;
    SYMBOL next_val;

    //int number_of_bytes = global_options & 0xf;
    int max = 0;

    if(number_of_bytes == 1){
        max =  256;
    }
    if(number_of_bytes == 2){
        max = 65536;
    }

    for(int i = code;i < max*2; i++){
        next_val = *(arrptr + i);
        *(arrptr + i) = curr_val;
        curr_val = next_val;
    }

    //return (arrptr - symbol_rank);
}

/**
 * @brief Shifts the mtft array
 *
 * @param sym The symbol in the array to which we shift
 * @return  the rank of sym in the array prior to shifting
 */
CODE shift_array(SYMBOL sym){

    CODE *arrptr = symbol_rank;
    SYMBOL curr_val = *arrptr;
    SYMBOL next_val;

    for(int i = 0;curr_val != sym; i++){
        next_val = *(arrptr + 1);
        *(arrptr + 1) = curr_val;
        curr_val = next_val;
        arrptr++;
    }

    return (arrptr - symbol_rank);
}

SYMBOL shift_array_to_code(CODE code){
    CODE *arrptr = symbol_rank;
    SYMBOL curr_val = *arrptr;
    SYMBOL next_val;

    for(int i = 0;i != code; i++){
        next_val = *(arrptr + 1);
        *(arrptr + 1) = curr_val;
        curr_val = next_val;
        arrptr++;
    }

    return (arrptr - symbol_rank);
}

int power(int base, int exp){
    int result = base;
    if(exp == 0){
        return 1;
    }
    for(int i = 1; i < exp; i++){
        result *= base;
    }
    return result;
}