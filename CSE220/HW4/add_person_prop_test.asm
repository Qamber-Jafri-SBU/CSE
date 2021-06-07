# add test cases to data section
# Test your code with different Network layouts
# Don't assume that we will use the same layout in all our tests
.data
Name1: .asciiz "Cacophonix"
Name2: .asciiz "Getafix"
Name3: .asciiz "jimmy"
Name_prop: .asciiz "NAME"

Network:
  .word 5   #total_nodes (bytes 0 - 3)
  .word 10  #total_edges (bytes 4- 7)
  .word 12  #size_of_node (bytes 8 - 11)
  .word 12  #size_of_edge (bytes 12 - 15)
  .word 3   #curr_num_of_nodes (bytes 16 - 19)
  .word 0   #curr_num_of_edges (bytes 20 - 23)
  .asciiz "NAME" # Name property (bytes 24 - 28)
  .asciiz "FRIEND" # FRIEND property (bytes 29 - 35)
   # nodes (bytes 36 - 95)    
  .byte 116 105 109 109 121 0 0 0 0 0 0 0 106 111 104 110 32 100 111 101 0 0 0 0 97 108 105 32 116 111 117 114 114 101 0 0 0 0 0 0 0 00 0 0 
   # set of edges (bytes 96 - 215)
  .word 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0






.text:
main:
	la $a0, Network
	jal create_person
	move $s0, $v0
	
	la $a0, Network
	addi $a1, $a0, 72
	la $a2, Name_prop
	la $a3, Name3
	jal add_person_property
	
	#write test code
	move $a0,$v0
	li $v0, 1
	syscall
	
	la $t0, Network
	addi $a0, $t0, 72
	li $v0, 4
	syscall
	
	li $v0, 10
	syscall
	
.include "hw4.asm"
# add test cases to data section
# Test your code with different Network layouts
# Don't assume that we will use the same layout in all our tests
# .data
# Name1: .asciiz "Cacophonix"
# Name2: .asciiz "Getafix"
# Name_prop: .asciiz "NAME"

# Network:
#   .word 5   #total_nodes (bytes 0 - 3)
#   .word 10  #total_edges (bytes 4- 7)
#   .word 12  #size_of_node (bytes 8 - 11)
#   .word 12  #size_of_edge (bytes 12 - 15)
#   .word 0   #curr_num_of_nodes (bytes 16 - 19)
#   .word 0   #curr_num_of_edges (bytes 20 - 23)
#   .asciiz "NAME" # Name property (bytes 24 - 28)
#   .asciiz "FRIEND" # FRIEND property (bytes 29 - 35)
#    # nodes (bytes 36 - 95)	
#   .byte 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0	
#    # set of edges (bytes 96 - 215)
#   .word 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0

# .text:
# main:
# 	la $a0, Network
# 	jal create_person
# 	move $s0, $v0

#   	la $a0, Network
# 	jal create_person
# 	move $s0, $v0
	
# 	la $a0, Network
# 	move $a1, $s0
# 	la $a2, Name_prop
# 	la $a3, Name1
# 	jal add_person_property

#   	la $a0, Network
# 	jal create_person
# 	move $s0, $v0
	
# 	la $a0, Network
# 	move $a1, $s0
# 	la $a2, Name_prop
# 	la $a3, Name1
# 	jal add_person_property
	
# 	#write test code

#   move  $a0, $v0
#   li    $v0, 1
#   syscall

#   li  $a0, 10
#   li  $v0, 11
#   syscall

#   la $a0, Network
#   addi  $a0, $a0, 60
#   li    $v0, 4
#   syscall

	
# 	li $v0, 10
# 	syscall
	
# .include "hw4.asm"
