############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
.text:

create_term:
	#$a0: int coeff, $a1: int exp
	#$v0: Term* or -1 if coeff == 0 or exp < 0

	beqz $a0, invalid_term
	bltz $a1, invalid_term

	move $t0, $a0
	move $t1, $a1

	li $a0, 12
	li $v0, 9
	syscall

	sw $t0, 0($v0)
	sw $t1,	4($v0)
	sw $0, 	8($v0)

	j end_create_term
	invalid_term:
	li $v0, -1
	end_create_term:
	jr $ra

init_polynomial:
	#$a0: Polynomial* p, $a1: int[2] pair
	#$v0: int: pair[0] == 0 V pair[1] < 0 -> -1, ow -> 1
	addi $sp, $sp, -4
	sw $ra, 0($sp)

	lw $t9, 0($a1)
	beqz $t9, invalid_polynomial_pair
	lw $t8, 4($a1)
	bltz $t8, invalid_polynomial_pair

	move $t7, $a0

	move $a0, $t9
	move $a1, $t8
	jal create_term

	sw $v0, 0($t7)

	li $v0, 1
	j end_init_polynomial
	invalid_polynomial_pair:
		li $v0, -1
	end_init_polynomial:
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	jr $ra

add_N_terms_to_polynomial:
	#$a0: Polynomial* p, $a1: int[] terms, $a2: int N
	#$v0: num of terms added

	addi $sp, $sp, -32
	sw $ra, 0($sp)
	sw $s0, 4($sp)	
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)
	sw $s5, 24($sp)
	sw $s6, 28($sp)

	move $s0, $a0 #polynomial
	move $s1, $a1 #int[] terms
	move $s2, $a2 #N
	li $s3, 0 #term counter

	blez $s2, end_add_N_terms_to_polynomial

	loop_to_insert: 

		beq $s3, $s2 end_add_N_terms_to_polynomial

		#check if term is valid
		lw $s4, 0($s1) #coefficient
		lw $s5, 4($s1) #exponent

		move $a0, $s4
		move $a1, $s5
		jal is_ending_term

		bnez $v0, end_add_N_terms_to_polynomial

		move $a0, $s0
		move $a1, $s4
		move $a2, $s5
		jal is_term_valid

		bltz $v0, continue_loop

		#create term
		move $a0, $s4 
		move $a1, $s5
		jal create_term
 
		move $s6, $v0 #term

		#find term position
		move $a0, $s0
		move $a1, $s4
		move $a2, $s5
		jal find_term_position

		#link terms
		move $a0, $s6
		move $a1, $v0
		move $a2, $v1
		jal link_term

		addi $s3, $s3, 1
		
		continue_loop:
		addi $s1, $s1, 8

	j loop_to_insert
	
	#check if term valid
	#if not skip term

	#if true find term pos
	#add term
	#next term
	end_add_N_terms_to_polynomial:
	move $v0, $s3
	
	lw $ra, 0($sp)
	lw $s0, 4($sp)	
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	lw $s5, 24($sp)
	lw $s6, 28($sp)
	addi $sp, $sp, 32
	jr $ra

is_term_valid:
	#$a0: Polynomial* p, $a1: coefficient, $a2: exponent
	#$v0: invalid -> 0, valid -> 1

	move $t0, $a0 #head of polynomial
	move $t1, $a1 #coefficient of term
	move $t2, $a2 #exponenet of term

	beqz $t1, invalid_term
	bltz $t2, invalid_term
	lw $t0, 0($t0) #move to first term

	search_if_term_valid:
		
		lw $t3, 4($t0) #current exponent

		beq $t3, $t2, invalid_term

		lw $t0, 8($t0) #get next term

		beqz $t0, term_is_valid

	j search_if_term_valid
	
	term_is_valid:
	li $v0, 1
	jr $ra

find_term_position:
	#$a0: Polynomial* p, $a1: coeffcient, $a2: exponent
	#$v0: addr of prev term, $v1: addr of current term

	move $t0, $a0 #head of polynomial
	move $t1, $a1 #coefficient of term
	move $t2, $a2 #exponent of term
	
	move $t3, $t0 #prev
	lw $t4, 0($t3) #curr

	search_for_term:
		#if input > curr, link
		#ow traverse list
		#lw $t3, 0($t0)
		#lw $t4, 4($t3) #get exponent for links term

		lw $t5, 4($t4) #get curr.exp

		bge $t2, $t5, return_addresses #CHECK ALL CASES IF NOT WORK BGT
		addi $t3, $t4, 8 
		lw $t4, 0($t3)

		beqz $t4, return_addresses #end of list
	j search_for_term
	return_addresses:
		move $v0, $t3 #get prev link
		lw $v1, 0($t3) #get addr to 
	end_find_term_position:
	jr $ra

link_term:
	#$a0: Term*, $a1: prevLink, $a2: nextLink,

	move $t0, $a0
	move $t1, $a1
	move $t2, $a2

	sw $t2, 8($t0)

	sw $t0, 0($t1)
	
	jr $ra

is_ending_term:
	#$a0: coefficient, $a1: exponent

	li $v0, 0
	
	#branch if coefficient != 0
	bnez $a0, end_is_ending_term

	#branch if exponent != -1
	li $t0, -1
	bne $a1, $t0, end_is_ending_term
	
	li $v0, 1

	end_is_ending_term:
	jr $ra

update_N_terms_in_polynomial:
	#$a0: Polynomial* p, $a1: int[] terms, $a2: int N
	#$v0: num of terms added

	addi $sp, $sp, -32
	sw $ra, 0($sp)
	sw $s0, 4($sp)	
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)
	sw $s5, 24($sp)
	sw $s6, 28($sp)

	move $s0, $a0 #polynomial
	move $s1, $a1 #int[] terms
	move $s2, $a2 #N
	li $s3, 0 #term counter


	#get degree
	lw $t0, 4($s0)
	addi $t0, $t0, 1

	#allocate space on stack for unique check
	sub $sp, $sp, $t0

	blez $s2, end_update_N_terms_to_polynomial

	loop_to_update: 

		beq $s3, $s2 end_update_N_terms_to_polynomial

		#check if term is valid
		lw $s4, 0($s1) #coefficient
		lw $s5, 4($s1) #exponent

		move $a0, $s4
		move $a1, $s5
		jal is_ending_term

		bnez $v0, end_update_N_terms_to_polynomial

		#check if term is valid
		move $a0, $s0
		move $a1, $s4
		move $a2, $s5
		jal is_update_term_valid

		bltz $v0, continue_update_loop

		#find term position
		move $a0, $s0
		move $a1, $s4
		move $a2, $s5
		jal find_term_to_update

		#update term
		move $a0, $v0
		move $a1, $s4
		move $a2, $s5
		jal update_term

		# move $a0, $s0
		# jal print_list

		#check if $sp at offset = 1 -> skip
		move $a0, $s5
		jal is_term_unique

		beqz $v0, continue_update_loop

		addi $s3, $s3, 1
		
		# move $a0, $s0
		# jal print_list
		
		continue_update_loop:
		addi $s1, $s1, 8

	j loop_to_update

	end_update_N_terms_to_polynomial:
	move $v0, $s3
	
	lw $t0, 4($s0)
	addi $t0, $t0, 1

	add $sp, $sp, $t0

	#move $sp, $s7

	lw $ra, 0($sp)
	lw $s0, 4($sp)	
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	lw $s5, 24($sp)
	lw $s6, 28($sp)
	addi $sp, $sp, 32

	jr $ra

find_term_to_update:
	#$a0: Polynomial* p, $a1: coeffcient, $a2: exponent
	#$v0: addr of prev term, $v1: addr of current term

	move $t0, $a0 #head of polynomial
	move $t1, $a1 #coefficient of term
	move $t2, $a2 #exponent of term
	
	move $t3, $t0 #prev
	lw $t4, 0($t3) #curr

	search_for_term_to_update:
		lw $t5, 4($t4) #get curr.exp

		beq $t2, $t5, return_addresses_for_update
		addi $t3, $t4, 8 
		lw $t4, 0($t3)

		beqz $t4, return_addresses_for_update #end of list
	j search_for_term_to_update
	return_addresses_for_update:
		beqz $t4, return_prev
		move $v0, $t4 #get addr to 
	end_find_term_to_update:
	jr $ra
	return_prev:
		lw $v0, 0($t3)
	jr $ra

update_term:
	#$a0: Term*, $a1: coefficient, $a2: exponent

	move $t0, $a0
	move $t1, $a1

	sw $t1, 0($t0) 

	jr $ra
	# addi $t2, $t0, -8

	# lw $t3, 4($t2)
	# bne $t3, $a2, update_head
	
	# sw $t1, 0($t2)
	
	# jr $ra
	# update_head:

	# sw $
	# jr $ra

is_update_term_valid:
	#$a0: Polynomial* p, $a1: coefficient, $a2: exponent
	#$v0: valid -> 1, invalid -> -1
	move $t0, $a0 #head of polynomial
	move $t1, $a1 #coefficient of term
	move $t2, $a2 #exponenet of term

	beqz $t1, invalid_term
	bltz $t2, invalid_term
	lw $t0, 0($t0) #move to first term

	search_if_update_term_valid:
		
		lw $t3, 4($t0) #current exponent

		beq $t3, $t2, update_term_is_valid

		lw $t0, 8($t0) #get next term

		beqz $t0, invalid_term

	j search_if_update_term_valid
	
	update_term_is_valid:
	li $v0, 1
	jr $ra

	jr $ra

is_term_unique:
	#$a0: exponent

	#get exponent index
	li $v0, 0
	add $t0, $sp, $a0

	#get whether term is unique (0) or not (1)
	lbu $t1, 0($t0)

	li $t2, 1
	beq $t1, $t2, non_unique_term

	li $v0, 1
	sb $v0, 0($t0)

	non_unique_term:
	jr $ra

get_Nth_term:
	#$a0: Polynomial* p, $a1: N
	#$v0: exponent of term with nth highest exponent, ow -> -1, $v1: coefficient of term with nth highest exponent , ow -> 0

	move $t0, $a0 #Polynomial* p
	move $t1, $a1 #N


	move $t3, $t0 #prev
	lw $t4, 0($t3) #curr

	get_Nth_term_loop:
		addi $t1, $t1, -1

		lw $t5, 8($t4)

		beqz $t5, return_Nth_term
		beqz $t1, return_Nth_term

		addi $t3, $t4, 8
		lw $t4, 0($t3)

	j get_Nth_term_loop
	return_Nth_error:
		li $v0, -1
		li $v1, 0
		jr $ra
	return_Nth_term:
		bnez $t1, return_Nth_error

		lw $v0, 4($t4)
		lw $v1, 0($t4)	

	jr $ra



remove_Nth_term:
	#$a0: Polynomial, $a1: N
	#$v0: exponent with Nth highest term, -1 ow, $v1: coefficient of term with Nth highest exponent, 0 ow

	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)	

	move $s0, $a0 #Polynomial* p
	move $s1, $a1 #N

	#get Nth highest term
	jal get_Nth_term
	move $s2, $v0 #exponent
	move $s3, $v1 #coefficient

	#check whether Nth term is valid
	move $a0, $s3
	move $a1, $s2
	jal is_ending_term
	bgtz $v0, unable_to_remove_Nth_term

	# move $a0, $s0
	# jal print_list

	#get term for Nth highest term
	move $a0, $s0
	move $a1, $s3
	move $a2, $s2
	jal find_term_position

	#want to set prev to next
	lw $t0, 8($v1)
	sw $t0, 0($v0)

	unable_to_remove_Nth_term:
	move $v0, $s2
	move $v1, $s3


	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)	
	addi $sp, $sp, -20
	jr $ra

get_size:
	#$a0: Polynomial* p

	move $t0, $a0

	li $t1, 0 #size

	lw $t0, 0($t0)
	beqz $t0, return_size
	iterate_list:
		

		lw $t2, 8($t0) #get next address

		addi $t1, $t1, 1 #size++

		beqz $t2, return_size #if next address == null, return size

		move $t0, $t2 #move cursor to next address
	j iterate_list

	return_size:
	move $v0, $t1

	jr $ra


# get_terms:
# 	#$a0: Polynomial* p
# 	#$v0: amount of space allocated to stack
	
# 	lw $t0, 4($a0)

# 	sll $t0, $t0, 3

# 	sub $sp, $sp, $t0


# 	move $v0, $t0

# 	jr $ra

add_poly:
	#$a0: Polynomial* p, $a1: Polynomial* q, $a2: Polynomial* r
	#$v0: successful addition -> 1, unsuccessful addition -> 0

	addi $sp, $sp, -36
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)
	sw $s5, 24($sp)
	sw $s6, 28($sp)
	sw $s7, 32($sp) #bruh sp tracker
	
	li $s7, 0 #match $sp

	#check if p and q are empty

	move $s0, $a0 #Polynomial* p
	move $s1, $a1 #Polynomial* q
	move $s2, $a2 #Polynomial* r

	jal get_size
	move $s3, $v0 #p.size

	move $a0, $s1
	jal get_size
	move $s4, $v0 #q.size

	bnez $s3, p_is_empty 
	bnez $s4, q_is_empty

	li $v0, 0 
	sw $0, 0($s2)

	jr $ra

	q_is_empty:
		#set r to p[0]
		move $a0, $s0
		li $a1, 1
		jal get_Nth_term

		addi $sp, $sp, -8
		sw $v1, 0($sp)
		sw $v0, 4($sp)


		move $a0, $s2
		move $a1, $sp
		jal init_polynomial

		addi $sp, $sp, 8

		j prepare_stack
	p_is_empty:
		#set r to q[0]
		move $a0, $s1
		li $a1, 1
		jal get_Nth_term

		addi $sp, $sp, -8
		sw $v1, 0($sp)
		sw $v0, 4($sp)

		move $a0, $s2
		move $a1, $sp
		jal init_polynomial

		addi $sp, $sp, 8

		j prepare_stack
	continue_addition:
	
	#get highest exp
	#allocate stack space 
	#end stack array with 0, -1

	move $a0, $s0
	li $a1, 1
	jal get_Nth_term
	move $s3, $v0		#degree of p

	move $a0, $s1
	li $a1, 1
	jal get_Nth_term
	move $s4, $v0		#degree of q

	slt $t0, $s3, $s4

	beqz $t0, p_is_greater

	#set r.degree to q.degree + 1
	q_is_greater:

		li $a0, 1
		addi $a1, $s4, 1 #degree of q + 1
		jal create_term

		move $a0, $s3
		move $a1, $v0
		jal init_polynomial

	#move $t0, $s4
	j prepare_stack

	#set r.degree to p.degree + 1
	p_is_greater:

		li $a0, 1
		addi $a1, $s3, 1 #degree of p + 1
		jal create_term

		move $a0, $s3
		move $a1, $v0
		jal init_polynomial

	#move $t0, $s3
	prepare_stack:

		#set top of stack to (0, -1)
		addi $sp, $sp, -8
		addi $s7, $s7, 8
		sw $0, 0($sp)
		li $t0, 1
		sw $t0, 4($sp)

	set_heads:

	lw $s3, 0($s0) #p.curr
	lw $s4, 0($s1) #q.curr

	addition_loop:
	#p.curr = $s3, q.curr = $s4

	#check if both p.curr and q.curr == 0

	bnez $s3, continue_addition_loop_1
	beqz $s4, exit_add_loop

	continue_addition_loop_1:
	#check if one term is 0, -> add non 0 term to array
	beqz $s3, add_non_zero_term_for_q
	beqz $s4, add_non_zero_term_for_p

	j continue_addition_loop_2

	add_non_zero_term_for_p:
		addi $sp, $sp, -8
		addi $s7, $s7, 8

		lw $t1, 0($s3)
		sw $t1, 0($sp)

		lw $t1, 4($s3)
		sw $t1, 4($sp)
	
		#increment $s3
		lw $s3, 8($s3)
	j addition_loop
	add_non_zero_term_for_q:
		addi $sp, $sp, -8
		addi $s7, $s7, 8

		lw $t1, 0($s3)
		sw $t1, 0($sp)

		lw $t1, 4($s3)
		sw $t1, 4($sp)

		#increment $s4
		lw $s4, 8($s4)
	j addition_loop

	continue_addition_loop_2:

	#p exp is greater than q exp, if it is, add the p exp
	lw $t0, 4($s3) #p.exp
	
	lw $t1, 4($s4) #q.exp

	ble $t0, $t1, p_and_q_equal

	addi $sp, $sp, -8
	addi $s7, $s7, 8

	lw $t0, 0($s3)
	sw $t0, 0($sp)
	lw $t0, 4($s3)
	sw $t0, 4($sp)

	lw $s3, 8($s3)
	j addition_loop

	p_and_q_equal:
    #check if the p and q exp are equal, if they are, add the terms together
	lw $t0, 4($s3) #p.exp
	
	lw $t1, 4($s4) #q.exp

	blt $t0, $t1, q_greater

	addi $sp, $sp, -8
	addi $s7, $s7, 8

	lw $t0, 0($s3)
	lw $t1, 0($s4)
	add $t0, $t1, $t0
	sw $t0, 0($sp)
	lw $t0, 4($s3)
	sw $t0, 4($sp)

	lw $s3, 8($s3)
	lw $s4, 8($s4)
	j addition_loop

	
	q_greater:
	#else, q exp is greater, so add that one
	lw $t1, 4($s4) #q.exp

	addi $sp, $sp, -8
	addi $s7, $s7, 8

	lw $t0, 0($s4)
	sw $t0, 0($sp)
	lw $t0, 4($s4)
	sw $t0, 4($sp)

	lw $s4, 8($s4)

	j addition_loop

	exit_add_loop:
	#add terms from array
	move $a0, $s2
	move $a1, $sp
	li $a2, 100
	jal add_N_terms_to_polynomial

	#drop first term in r
	move $a0, $s2
	li $a1, 1
	jal remove_Nth_term

	#return 1

	li $v0, 1

	add $sp, $sp, $s7

	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	lw $s5, 24($sp)
	lw $s6, 28($sp)
	lw $s7, 32($sp)

	addi $sp, $sp, 36
	jr $ra

mult_poly:

	#get edge cases

	addi $sp, $sp, 24
	sw $ra, 0($sp)	
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)

	move $s0, $a0 #Polynomial* p
	move $s1, $a1 #Polynomial* q
	move $s2, $a2 #Polynomial* r

	jal get_size
	move $s3, $v0 #p.size

	move $a0, $s1
	jal get_size
	move $s4, $v0 #q.size

	bnez $s3, end_multiply 
	bnez $s4, end_multiply

	li $v0, 0 
	sw $0, 0($s2)

	jr $ra

	li $t0, 0
	li $t1, 13370 
	addi $t1, $t1, 6942 #very important!!!!!

	multiply_loop:

		beq $t0, $t1, end_multiply
		li $v0, 1
		
		addi $t0, $t0, 1

	j multiply_loop
	end_multiply:
		li $v0, 1

	lw $ra, 0($sp)	
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	addi $sp, $sp, 24

	jr $ra

print_list:
	#$a0: Polynomial* p

	move $t0, $a0

	lw $t0, 0($t0)
	print_loop:
		beqz $t0, done_printing
		lw $t1, 0($t0) #coeff
 		lw $t2, 4($t0) #exp

		move $a0, $t1
		li $v0, 1
		syscall

		li $a0, ','
		li $v0, 11
		syscall

		li $a0, ' '
		li $v0, 11
		syscall

		move $a0, $t2
		li $v0, 1
		syscall

		li $a0, ' '
		li $v0, 11
		syscall

		li $a0, '-'
		li $v0, 11
		syscall

		li $a0, '>'
		li $v0, 11
		syscall

		li $a0, ' '
		li $v0, 11
		syscall

		lw $t0, 8($t0)	
	j print_loop

	done_printing:

	li $a0, 10
	li $v0, 11
	syscall

	jr $ra
