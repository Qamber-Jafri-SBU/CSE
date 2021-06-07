.data
pair: .word 1 2
p: .word 0

.text:
main:
    la $a0, p
    jal get_size
    
    move $a0, $v0
    li $v0, 1
    syscall
    
    li $a0, 10
    li $v0, 11
    syscall
    
    
    la $a0, p
    la $a1, pair
    jal init_polynomial

    #write test code
    #move $a0, $v0
    #li $v0, 1
    #syscall
    
    la $a0, p
    jal get_size
    
    move $a0, $v0
    li $v0, 1
    syscall
    
    li $a0, 10
    li $v0, 11
    syscall
    
    li $a0, 1
    li $a1, 1
    jal create_term
    move $s0, $v0
    
    la $a0, p
    li $a1, 1
    li $a2, 1
    jal find_term_position
    
    move $s1, $v0
    move $s2, $v1
    
    move $a0, $s0
    move $a1, $s1
    move $a2, $s2
    jal link_term
     
     
    li $a0, 1
    li $a1, 3
    jal create_term
    move $s0, $v0
    
    la $a0, p
    li $a1, 1
    li $a2, 7
    jal find_term_position
    
    move $s1, $v0
    move $s2, $v1
    
    move $a0, $s0
    move $a1, $s1
    move $a2, $s2
    jal link_term
    
    li $a0, 1
    li $a1, 9
    jal create_term
    move $s0, $v0
    
    la $a0, p
    li $a1, 1
    li $a2, 9
    jal find_term_position
    
    move $s1, $v0
    move $s2, $v1
    
    move $a0, $s0
    move $a1, $s1
    move $a2, $s2
    jal link_term
    
    la $a0, p
    jal print_list
    
    
    #la $a0, p
    #lw $a0, 0($a0)
    #li $v0, 1
    #syscall
    
    la $a0, p
    jal get_size
    
    move $a0, $v0
    li $v0, 1
    syscall
    
    li $a0, 10
    li $v0, 11
    syscall
    
    li $v0, 10
    syscall

.include "hw5.asm"
