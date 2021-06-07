.data
Newline: .asciiz "\n"
WrongArgMsg: .asciiz "You must provide exactly one argument"
BadToken: .asciiz "Unrecognized Token"
ParseError: .asciiz "Ill Formed Expression"
ApplyOpError: .asciiz "Operator could not be applied"

val_stack : .word 0
op_stack : .word 0
lame : .asciiz "8008+1337"
.text
.globl main
main:
	la $a0, lame
	jal eval
  # add code to call and test eval function

end:
	# Terminates the program
	li $v0, 10
	syscall

.include "hw2-funcs.asm"
