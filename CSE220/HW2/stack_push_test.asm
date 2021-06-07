.data
Newline: .asciiz "\n"
WrongArgMsg: .asciiz "You must provide exactly one argument"
BadToken: .asciiz "Unrecognized Token"
ParseError: .asciiz "Ill Formed Expression"
ApplyOpError: .asciiz "Operator could not be applied"

val_stack : .word 0
op_stack : .word 0
cringe : .asciiz "123+456"
.text
.globl main
main:
	la $a0, cringe
	li $a1, 0
	jal get_number
	
	move $a0, $v0
	li $v0, 1
	syscall
	
  # add code to call and test stack_push function

end:
  # Terminates the program
  li $v0, 10
  syscall

.include "hw2-funcs.asm"
