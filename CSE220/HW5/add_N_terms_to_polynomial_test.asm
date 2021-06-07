.data
pair: .word 12 8
terms: .word 2 2 4 3 5 0 0 -1
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
    
    move $a0, $v0
    li $v0, 1
    syscall
    
    li $a0, 10
    li $v0, 11
    syscall
    
    #write test code
    
    la $a0, p
    jal print_list
    
    
    li $v0, 10
    syscall

.include "hw5.asm"
