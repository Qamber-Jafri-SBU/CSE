.data
coeff: .word 1
exp: .word 2

.text:
main:
    lw $a0, coeff
    lw $a1, exp
    jal create_term

    #write test code
    move $a0, $v0
    li $v0, 1
    #syscall
    
    li $a0, 0
    li $a1, -1
    jal is_ending_term
    
    move $a0, $v0
    li $v0, 1
    syscall
    
    li $v0, 10
    syscall

.include "hw5.asm"
