.data
.align 2
state:        
.byte 8         # bot_mancala           (byte #0)
    .byte 3         # top_mancala           (byte #1)
    .byte 49         # bot_pockets           (byte #2)
    .byte 49         # top_pockets            (byte #3)
    .byte 10         # moves_executed    (byte #4)
    .byte 'B'    # player_turn                (byte #5)
    # game_board                             (bytes #6-end)
    .asciiz
    "03000300000000010101010101010101010101010101010101010101010101010101010101010101010101010100000101000201010100010101010101010101010101010101010101010101010101010101010101010101010101010100000002000008"
.text
.globl main
main:
la $a0, state
jal print_board
# You must write your own code here to check the correctness of the function implementation.

li $v0, 10
syscall

.include "hw3.asm"
