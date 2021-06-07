.data
ErrMsg: .asciiz "Invalid Argument"
WrongArgMsg: .asciiz "You must provide exactly two arguments"
EvenMsg: .asciiz "Even"
OddMsg: .asciiz "Odd"
left_of_fraction : .asciiz "1."

arg1_addr : .word 0
arg2_addr : .word 0
num_args : .word 0

.text:
.globl main
main:
	sw $a0, num_args

	lw $t0, 0($a1)
	sw $t0, arg1_addr
	lw $s1, arg1_addr

	lw $t1, 4($a1)
	sw $t1, arg2_addr
	lw $s2, arg2_addr

	j start_coding_here

# do not change any line of code above this section
# you can add code to the .data section
start_coding_here:
	
	lw $s0, num_args
	li $t0, 2

	bne $s0, $t0, not_two_args #if num_args != 2 go to not_two_args
	
	lw $t0, arg1_addr #get first arg (String/Char)
	lbu $t0, 0($t0) #get first char of first arg

	lw $t2, arg2_addr #get second arg (hexadecimal or decimal number)
	lb $t4, 0($t2)
	li $t5, '0'
	bne $t4, $t5, not_correct_arg
	lb $t4, 1($t2)
	li $t5, 'x'
	bne $t4, $t5, not_correct_arg
	li $t4, 48 #ASCII value for 0
	li $t5, 57 #ASCII value for 9
	li $t6, 2 #counter
	li $t7, 65 #ASCII value for A
	li $t8, 70 #ASCII value for F
	li $t1, 10 #the max size of arg2

	dec_check:
		bge	$t6, $t1, store_hex	# if $t6 >= $t1 then store_hex
		lb $t3, 2($t2) #$t3 is a char in the second arg
		blt	$t3, $t4, not_correct_arg	# if $t3 < $t4 then not_correct_arg
		bgt	$t3, $t5, hex_check	# if $t3 > $t5 then hex_check
		continue_loop: 	
		
		addi $t2, $t2, 1
		addi $t6, $t6, 1
		j dec_check


	store_hex: #stores hex arg (arg2) into register $s3
		li $t6, 2
		li $s3, 0
		lw $t2, arg2_addr
		addi $t2, $t2, 2

		loop:

			lbu $t3, 0($t2)
			addi $t2, $t2, 1
			addi $t6, $t6, 1
			bge  $t3, $t7, hex_chars
			
			addi $t3, $t3, -48
			add $s3, $s3, $t3
			bge	$t6, $t1, case_O
			sll $s3, $s3, 4
			j loop

	case_O:
		li $t1, 'O'
		bne $t0, $t1, case_S
		lui $t2, 0xFC00 
		and $t3, $t2, $s3 #get first 6 bits i.e. 0000 00XX XXXX XXXX XXXX XXXX XXXX XXXX 
		srl $t3, $t3, 26
		move $a0, $t3
		li $v0, 1
		syscall
		j end
	case_S:
		li $t1, 'S'
		bne $t0, $t1, case_T
		lui $t2, 0x03E0
		and $t3, $t2, $s3 #get next 5 bits i.e. XXXX XX00 000X XXXX XXXX XXXX XXXX XXXX
		srl $t3, $t3, 21
		move $a0, $t3
		li $v0, 1
		syscall
		j end
	case_T:
		li $t1, 'T'
		bne $t0, $t1, case_I
		lui $t2, 0x001F
		and $t3, $t2, $s3 #get next 5 bits i.e. XXXX XXXX XXX0 0000 XXXX XXXX XXXX XXXX
		srl $t3, $t3, 16
		move $a0, $t3
		li $v0, 1
		syscall
		j end
	case_I:
		li $t1, 'I'
		bne $t0, $t1, case_E
		move $t3, $s3
		sll $t3, $t3, 16
		sra $t3, $t3, 16 #get next last 16 bits with sign i.e. SSSS SSSS SSSS SSSS 0000 0000 0000 0000
		move $a0, $t3
		li $v0, 1
		syscall
		j end
	case_E:
		li $t1, 'E'
		bne $t0, $t1, case_C
		andi $t3, $s3, 1 #get last bit XXXX XXXX XXXX XXXX XXXX XXXX XXXX XXX0
		beqz $t3, print_even
		la $a0, OddMsg
		li $v0, 4
		syscall
		j end
	case_C:
		li $t1, 'C'
		bne $t0, $t1, case_X
		move $t3, $s3
		li $t2, 0 #counter
		li $t4, 2
		count_ones:
			div $t3, $t4
			mfhi $t5
			beqz $t5, skip_counter
			addi $t2, $t2, 1
			skip_counter:
			srl $t3, $t3, 1
			bgtz $t3, count_ones
		move $a0, $t2
		li $v0, 1
		syscall
		j end
	case_X:
		li $t1, 'X'
		bne $t0, $t1, case_M
		lui $t2, 0x7F80
		and $t3, $t2, $s3 #get exponents bits given IEEE 754 standard i.e. X000 0000 0XXX XXXX XXXX XXXX XXXX XXXX
		srl $t3, $t3, 23
		addi $t3, $t3, -127
		move $a0, $t3
		li $v0, 1
		syscall
		j end
	case_M:
		li $t1, 'M'
		bne $t0, $t1, default
		lui $t2, 0x007F
		ori $t2, $t2, 0xFFFF
		and $t3, $t2, $s3
		sll $t3, $t3, 9
		la $a0, left_of_fraction
		li $v0, 4
		syscall
		move $a0, $t3
		li $v0, 35
		syscall
		j end
	default:
		j not_correct_arg


hex_chars: #converts A-F chars to value stored in register $s3
	addi $t3, $t3, -55
	add $s3, $s3, $t3
	bge	$t6, $t1, case_O
	sll $s3, $s3, 4
	j loop
	
hex_check: #checks if char is between A-F, else not_correct_arg
	blt $t3, $t7, not_correct_arg
	bgt $t3, $t8, not_correct_arg
	j continue_loop

not_two_args:
	la $a0, WrongArgMsg
	j print_end
		
not_correct_arg:
	la $a0, ErrMsg
	j print_end

print_even:
	la $a0, EvenMsg
	li $v0, 4
	syscall
	j end
	
print_end:
	li $v0, 4
	syscall
	j end

end:
	li $v0, 10
	syscall
