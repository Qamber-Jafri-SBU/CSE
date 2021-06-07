.data
pair: .word 4 3
terms: .word 2 2 5 0 0 -1
new_terms: .word 1 3 3 3 1 0 0 -1
p: .word 0
N: .word -1

.text:
main:
    la $a0, p
    la $a1, pair
    jal init_polynomial

    
    la $a0, p
    la $a1, terms
    lw $a2, N
    jal add_N_terms_to_polynomial
    
    la $a0, p
    jal print_list  
    
    la $a0, p
    la $a1, new_terms
    lw $a2, N
    jal update_N_terms_in_polynomial
    move $s0, $v0
    #write test code
    
    la $a0, p
    #jal print_list   
    
    move $a0, $s0
    li $v0, 1
    syscall
    
    li $v0, 10
    syscall

.include "hw5.asm"
