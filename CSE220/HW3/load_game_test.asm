.data
board_filename: .asciiz "game01.txt"
.align 2
state:        
    .byte 0         # bot_mancala       	(byte #0)
    .byte 1         # top_mancala       	(byte #1)
    .byte 6         # bot_pockets       	(byte #2)
    .byte 6         # top_pockets        	(byte #3)
    .byte 2         # moves_executed	(byte #4)
    .byte 'B'    # player_turn        		(byte #5)
    # game_board                     		(bytes #6-end)
    .asciiz
    "01080700"
.text
.globl main
main:

la $a0, state
la $a1, board_filename
jal load_game
# You must write your own code here to check the correctness of the function implementation.


la $t0, state
addi $a0, $t0, 6
li $v0, 4
syscall

move $a0, $v1
li $v0, 1
syscall


li $v0, 10
syscall
.include "hw3.asm"
