# Qamber Jafri
# qjafri
# 112710107

############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################

.text

load_game:
	addi $sp $sp, -12
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)

	move $s0, $a1 #$s0 filename
	move $s1, $a0 #$s1 game state
	
	move $a0, $a1 #move filename into $a0
	jal open_file
	bltz $v0, file_error #return -1 if file error

	move $a0, $v0
	move $a1, $s1
	jal read_file

	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	addi $sp, $sp, 12
	jr $ra

open_file: #file descriptor open_file(filename)
	li $a1, 0
	li $v0, 13
	syscall #open board game file

	jr $ra

read_file: #alters game_state struct, read_file(file descriptor, struct)
	addi $sp, $sp, -32
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $fp, 20($sp)
	sw $s4, 24($sp)
	sw $s5, 28($sp)
	move $s1, $a1
	move $s5, $a1

	li $t0, 0 #line number
	li $t5, 48 #ASCII code for 0
	read_line:
		#addi $t0, $t0, 1 #line number++
		li $t1, 0 #value of number
		li $t2, 0 #stack size

		li $t3, 69
		move $fp, $sp #move frame pointer to initial stack position
		j newline_loop
		sp_reset:
			move $sp, $fp
		newline_loop:

			addi $sp, $sp, -4 #move $sp to allocate more space
			addi $t2, $t2, 4 #increment stack counter

			move $a1, $sp
			li $a2, 1
			li $v0, 14
			syscall #read 1 char from file and store onto $sp

			lw $t3, 0($sp) 

			li $t4, 13 #\r ASCII value
			beq $t3, $t4, read_carriage_return
			li $t4, 10 #\n ASCII value
			beq $t3, $t4, skip_carriage_return

		j newline_loop

		read_carriage_return:
			move $a1, $sp
			li $v0, 14
			syscall #skip \n after \r 
		skip_carriage_return:
			addi $sp, $sp, 4
			addi $t2, $t2 -4
			j parse_int 

	parse_int:
		# addi $sp, $sp, 4 #increment $sp for extra -4
		# addi $t2, $t2, -4 #decrement stack counter for extra +4
		li $t7, 10
		li $t8, 3
		beq $t0, $t8, pocket_parse
		li $t8, 4
		beq $t0, $t8, pocket_parse
		li $t1, 0

		reverse_stack:
			move $sp, $fp
			addi $sp, $sp, -4
		char_to_int:
			beqz $t2, write_to_struct #branch if stack size == 0
			mul $t1, $t1, $t7 #multiply number by 10
			mflo $t1
			lw $t6, 0($sp)
			#parse integer and store into register
			sub $t6, $t6, $t5 #get integer value from ASCII
			add $t1, $t1, $t6

			#for each char subtract 4 to $sp
			addi $sp, $sp, -4 #get next value
			addi $t2, $t2, -4 #decrenent stack size
		j char_to_int 
	pocket_parse:
		#reverse stack
		move $sp, $fp
		addi $sp, $sp, -4 #get bottom of stack
		#li $s3, 0 #number of pockets
		li $s4, 0 #total number of beads
		li $t6, 0#beads in pocket
		li $t4, 10
		li $t9, 48
		li $t5, 4

		addi $t0, $t0, 1 #increment newline counter
		
		bne $t0, $t5, bottom_player_pockets #if newline counter != 4 switch branches
		addi $t7, $s5, 6 #offset
		#move $t7, $a1 #offset
		div $s1, $t4
		mflo $t5
		add $t5, $t5, $t9 #get first digit of top mancala in ASCII

		sb $t5, 0($t7)
		addi $t7, $t7, 1 #store first digit in gameboard

		mfhi $t5
		add $t5, $t5, $t9 #get second digit of top mancala in ASCII

		sb $t5, 0($t7)
		addi $t7, $t7, 1 #store second digit in gameboard
		convert_to_int:
			#we want to get bottom 2 and put in string
			#check if first char is \r or \n
			lbu $t8, 0($sp) #get first char of pocket

			li $t5, 13
			beq $t8, $t5, sp_reset #skip \r
			li $t5, 10
			beq $t8, $t5, sp_reset #skip \n
			beqz $t2, sp_reset #branch if stack size == 0

			sb $t8, 0($t7) #store first ASCII char in gameboard


			move $t5, $t8
			sub $t5, $t5, $t9
			mul $t5, $t5, $t4  #multiply by 10
			mflo $t5 #get left digit integer value

			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size

			addi $t7, $t7, 1

			lbu $t8, 0($sp) #get second char of pocket

			sb $t8, 0($t7) #store second ASCII char in gameboard
			
			sub $t6, $t8, $t9
			add $t5, $t5, $t6
			add $s4, $s4, $t5 #add number of beads in pocket to running total

			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size
			addi $t7, $t7, 1
			#addi $s3, $s3, 1
		j convert_to_int 
	bottom_player_pockets:
		li $t5, 5
		bne $t0, $t5, file_read #if newline counter != 5 jump to end of function

		addi $t7, $s5, 8 #offset by first six bytes + top mancala
		li $t9, 2
		mul $t8, $s3, $t9
		add $t7, $t7, $t8
		li $t9, 48
		convert_bottom_to_int:
			#we want to get bottom 2 and put in string
			#check if first char is \r or \n
			lbu $t8, 0($sp) #get first char of pocket

			li $t5, 13
			beq $t8, $t5, append_bot_mancala #skip \r
			li $t5, 10
			beq $t8, $t5, append_bot_mancala #skip \n
			beqz $t2, append_bot_mancala #branch if stack size == 0

			sb $t8, 0($t7) #store first ASCII char in gameboard


			move $t5, $t8
			sub $t5, $t5, $t9
			mul $t5, $t5, $t4  #multiply by 10
			mflo $t5 #get left digit integer value

			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size

			addi $t7, $t7, 1

			lbu $t8, 0($sp) #get second char of pocket

			sb $t8, 0($t7) #store second ASCII char in gameboard
			
			sub $t6, $t8, $t9
			add $t5, $t5, $t6
			add $s4, $s4, $t5 #add number of beads in pocket to running total

			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size
			addi $t7, $t7, 1
			#addi $s3, $s3, 1
		j convert_bottom_to_int 
	count_pieces:
		add $s4, $s4, $s1
		add $s4, $s4, $s2
		j file_read
	append_bot_mancala:
		
		li $t9, 48

		div $s2, $t4
		mflo $t5
		add $t5, $t5, $t9 #get first digit of top mancala in ASCII

		#addi $t7, $t7, 1

		sb $t5, 0($t7)
		addi $t7, $t7, 1 #store first digit in gameboard

		mfhi $t5
		add $t5, $t5, $t9 #get second digit of top mancala in ASCII

		#addi $t7, $t7, 1

		sb $t5, 0($t7)
		addi $t7, $t7, 1 #store second digit in gameboard
		j count_pieces
	write_to_struct:
		#compare newline counter ($t0) to 1,2,3 and 4,5 (for double chars)
		# move $sp, $fp
		# addi $sp, $sp, 4
		move $s0, $t1
		addi $t0, $t0, 1 #linenumber++
		move $t8, $s1

		top_mancala: #when newline_counter = 1
			li $t7, 1
			bne $t0, $t7, bot_mancala
			sb $t1, 1($s5)
			move $s1, $t1 #$s1 holds top mancala
			j sp_reset
		bot_mancala: #when newline_counter = 2
			li $t7, 2
			bne $t0, $t7, num_pockets
			sb $t1, 0($s5)
			move $s2, $t1 #$s2 holds bottom mancala
			j sp_reset
		num_pockets: #when newline_counter = 3
			li $t7, 3
			bne $t0, $t7, default #\n count = 4
			sb $t1, 2($s5)
			sb $t1, 3($s5)
			move $s3, $t1
			li $t7, 0
			sb $t7, 4($s5)
			li $t7, 'B'
			sb $t7, 5($s5)
			#account for eror with too many pocket
			#li $t7, 
			j sp_reset
		default:
		
	j sp_reset
 


file_read:

	# #store \0 at $sp + 1
	# sb $0, 1($sp)
	# li $v0, 16
	# syscall


	bounds_check:
	li $t0, 100
	li $t1, -1
	#li $v0, 1
	slt $v0, $s4, $t0
	addi $v0, $v0, -1
	nor $v0, $v0, $v0
	mul $v0, $v0, $t1 #check if stones <= 99

	li $t0, 99
	li $t2, 2
	mul $t2, $s3, $t2

	slt $v1, $t2, $t0
	addi $v1, $v1, -1
	nor $v1, $v1, $v1
	mul $v1, $v1, $t1 #check if pockets <= 98
	mul $v1, $v1, $t2

	move $sp, $fp
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $fp, 20($sp)
	lw $s4, 24($sp)
	lw $s5, 28($sp)
	addi $sp $sp, 32
	jr $ra


	file_error:
		li $v1, -1
		lw $ra, 0($sp)
		lw $s0, 4($sp)
		lw $s1, 8($sp)
		addi $sp $sp, 12
		jr $ra

print_struct: #print_struct(struct)
	move $t0, $a0
	li $t3, 4
	li $t1, 5 #byte counter
	lbu $t2, 3($t0)
	mul $t2, $t2, $t3

	print_first_five:
		beqz $t1, print_remaining
		lbu $a0, 0($t0)
		li $v0, 1
		syscall

		li $a0, 10
		li $v0, 11
		syscall

		addi $t0, $t0, 1
		addi $t1, $t1, -1
	j print_first_five

	print_remaining:
	lbu $a0, 0($t0)
	li $v0, 11
	syscall

	li $a0, 10
	li $v0, 11
	syscall

	addi $t0, $t0, 1
	addi $t2, $t2, 4
	#print mancalas as well
	print_game_board:
		beqz $t2, done_printing
		
		lbu $a0, 0($t0)
		syscall

		addi $t2, $t2, -1
		addi $t0, $t0, 1
	j print_game_board
	done_printing:
	li $a0, 10
	li $v0, 11
	syscall

	jr $ra

get_pocket: #$a0: GameStruct, $a1: player, $a2: distance

	li $t2, 48
	li $t4, 10
	li $t5, 2
	top_player_pockets: #start from begining mancala
		li $t0, 'T'
		bltz $a2, get_pocket_error
		bne $a1, $t0, bot_player_pockets
		lbu $t0, 3($a0) #number of top pockets
		blt $t0, $a2, get_pocket_error
		
		mul $t1, $t5, $a2 #multiply distance by 2

		addi $t1, $t1, 2 #distance + top mancala (+2) #comment for turbo
		#add $t0, $t0, $t1 # distance + top mancala + number of pockets

		add $t0, $t1, $a0 #total distance + struct address
		lbu $t3, 0($t0)
		sub $t3, $t3, $t2
		mul $t5, $t3, $t4 #get left integer value

		addi $t0, $t0, 1
		
		lbu $t3, 0($t0)
		sub $t3, $t3, $t2
		add $t5, $t5, $t3 #get right integer value

		move $v0, $t5
		jr $ra
	bot_player_pockets: #start from end mancala
		li $t0, 'B'
		bne $a1, $t0, get_pocket_error
		lbu $t0, 2($a0) #number of bot pockets
		blt $t0, $a2, get_pocket_error

		li $t3, 4
		
		mul $t1, $t0, $t3 #2x number of digits for pockets
		add $t1, $t1, $a0  # struct address + 2x pockets  
		addi $t1, $t1, 7 #gameboard offset + second digit (used to be 5)

		mul $t0, $a2, $t5 #distance
		sub $t0, $0, $t0  #total distance negated

		add $t0, $t0, $t1 # total gameboard - distance

		lbu $t3, 0($t0)
		sub $t3, $t3, $t2
		add $t5, $0, $t3 #get right intger value

		addi $t0, $t0, -1
		
		lbu $t3, 0($t0)
		sub $t3, $t3, $t2
		mul $t3, $t3, $t4
		add $t5, $t5, $t3

		move $v0, $t5
		jr $ra
	get_pocket_error:
		li $v0, -1
	jr $ra
	
set_pocket: #$a0: GameState, $a1: player, $a2: distance, $a3: size
	li $t2, 48
	li $t4, 10
	li $t5, 2
	li $t9, 99
	

	bgt $a3, $t9, set_pocket_error
	bltz $a3, set_pocket_error
	set_top: #start from begining mancala
		li $t0, 'T'
		bltz $a2, get_pocket_error
		bne $a1, $t0, set_bot
		lbu $t0, 3($a0) #number of top pockets
		blt $t0, $a2, get_pocket_error
		
		mul $t1, $t5, $a2 #multiply distance by 2
		
		addi $t1, $t1, 8 #distance + top mancala (+2) #comment for turbo
		#add $t0, $t0, $t1 # distance + top mancala + number of pockets

		move $t0, $t1

		div $a3, $t4
		add $t0, $t0, $a0 #total distance + struct address
		mflo $t6
		add $t6, $t6, $t2
		sb $t6, 0($t0) #store left integer value

		addi $t0, $t0, 1
		
		mfhi $t6
		add $t6, $t6, $t2
		sb $t6, 0($t0)

		move $v0, $a3
		jr $ra
	set_bot: #start from end mancala
		li $t0, 'B'
		bne $a1, $t0, get_pocket_error
		lbu $t0, 3($a0) #number of bot pockets
		blt $t0, $a2, get_pocket_error

		li $t3, 4
		
		# mul $t1, $t0, $t3 #2x number of digits for pockets
		# add $t1, $t1, $a0  # struct address + 2x pockets  
		# addi $t1, $t1, 7 #gameboard offset + second digit (was 5)

		# mul $t0, $a2, $t5 #distance
		# sub $t0, $0, $t0  #total distance negated

		# add $t0, $t0, $t1 # total gameboard - distance

		sll $t0, $t0, 2 #4x num pockets
		addi $t0, $t0, 6 # 4xpockets + 8
		add $t0, $t0, $a0 # #addr + 4xpockets + 8
		sll $t1, $a2, 1 #distance *2

		sub $t0, $t0, $t1 # addr + 4xpockets + 8 -distance *2
		
		div $a3, $t4
		mflo $t6
		add $t6, $t6, $t2
		sb $t6, 0($t0) #store left digit

		addi $t0, $t0, 1
		
		mfhi $t6
		add $t6, $t6, $t2
		sb $t6, 0($t0) #store right digit

		move $v0, $a3
		jr $ra
	set_pocket_error:
		li $v0, -2
	jr $ra
collect_stones: # $a0: GameState, $a1: player, $a2: stones
	
	bltz $a2, set_pocket_error
	li $t4, 10
	li $t5, 48
	
	top_player_mancala:
		li $t0, 'T'
		bne $a1, $t0, bot_player_mancala
		addi $t0, $a0, 6 #top mancala address

		lbu $t1, 1($a0) #load top mancala int
		add $t1, $t1, $a2
		sb $t1, 1($a0) #set top mancala byte

		div $t1, $t4
		mflo $t2 #get left digit of new mancala
		add $t2, $t2, $t5

		sb $t2, 0($t0)

		addi $t0, $t0, 1

		mfhi $t2 #get right digit of new mancala
		add $t2, $t2, $t5

		sb $t2, 0($t0)

		move $v0, $a2
		jr $ra
	bot_player_mancala:
		li $t0, 'B'
		bne $a1, $t0, player_error

		lbu $t1, 2($a0)
		li $t0, 8
		add $t1, $t1, $t1
		add $t1, $t1, $t1

		add $t0, $t1, $t0
		add $t0, $a0, $t0 #bot mancala address

		lbu $t1, 0($a0) #load bot mancala int
		add $t1, $t1, $a2
		sb $t1, 0($a0) #set bot mancala byte

		div $t1, $t4
		mflo $t2 #get left digit of new mancala
		add $t2, $t2, $t5

		sb $t2, 0($t0)

		addi $t0, $t0, 1

		mfhi $t2 #get right digit of new mancala
		add $t2, $t2, $t5

		sb $t2, 0($t0)

		move $v0, $a2
		jr $ra
	player_error:
		li $v0, -1
	jr $ra
verify_move: #$a0: GameState, $a1: origin_pocket, $a2: distance
	addi $sp, $sp, -16
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)

	move $s0, $a0 #GameState
	move $s1, $a1 #origin pocket
	move $s2, $a2 #distance

	li $t9, 99
	lbu $t8, 5($a0) #player turn character
	beq $a2, $t9, ignore_origin


	#$a0: GameState, $a1: player char, $a2: distance (origin pocket)

	move $a1, $t8
	move $a2, $s1
	jal get_pocket
	bltz $v0, end_verify_move #-1 if distance not valid/player not valid
	beqz $v0, end_verify_move #0 if no stones in pocket

	beqz $s2, zero_distance_neq_stones
	bne $s2, $v0, zero_distance_neq_stones #distance not equal to stones in pocket
	li $v0, 1
	j end_verify_move
	zero_distance_neq_stones:
		li $v0, -2
		j end_verify_move
	ignore_origin:

		#$t8 = player turn character
		lbu $t7, 4($a0) #moves executed
		
		li $v0, 2

		addi $t7, $t7, 1 
		sb $t7, 4($a0) #increment moves executed by 1
		
		top_move:
			li $t7, 'T'
			bne $t7, $t8, bot_move
			li $t8, 'B'
			sb $t8, 5($s0)
			j end_verify_move
		bot_move:
			li $t8, 'T'
			sb $t8, 5($s0)
			j end_verify_move

	end_verify_move:
	lw $ra 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	addi $sp, $sp 16
	jr  $ra

execute_move: #$a0: GameState, $a1: origin_pocket
	addi $sp, $sp, -36
	sw $ra, 0($sp)
	sw $s0, 4($sp) #origin pocket (distance)
	sw $s1, 8($sp) #origin pocket stones (hand)
	sw $s2, 12($sp)	#player CHAR
	sw $s3, 16($sp) #GameState
	sw $s4, 20($sp) #current player side
	sw $s5, 24($sp) #number of pockets - 1
	sw $s6, 28($sp) 
	sw $s7, 32($sp) #mancala incrementation counter

	lbu $s5, 2($a0)
	addi $s5, $s5, -1 #max distance in a row

	li $s7, 0 #counter starts at 0
	move $s0, $a1 #origin pocket
	lbu $s2, 5($a0) #get current player (CHAR)
	move $s3, $a0 #game_state
	move $s4, $s2 #player side we are on

	# move $a0, $s3
	# jal print_struct

	#get initial pocket
	move $a0, $s3
	move $a1, $s2
	move $a2, $s0
	jal get_pocket #get number of stones in origin pocket

	move $s1, $v0 #origin_pocket stones

	move $a0, $s3
	move $a1, $s2
	move $a2, $s0
	li $a3, 0
	jal set_pocket #remove all stones from origin pocket

	# move $a0, $s3
	# jal print_struct

	move $a0, $s3
	li $s6, 1
	addi $s0, $s0, -1
	# addi $s1, $s1, -1 #decrement hand
	#s0: distance, $s2: player, #s3: struct
	traverse_board:
		# move $a0, $s3
		# jal print_board


		#addi $s0, $s0, -1 #decrement distance
		

		li $t0, 1
		beq $s1, $t0, evaluate_move #check if hand has 1 stone 

		li $t0, 1
		bltz $s0, switch_side#check if distance < 0


		continue_loop:		
		#get stones of next pocket

		
		move $a0, $s3
		move $a1, $s4
		move $a2, $s0
		jal get_pocket

		move $t0, $v0
		addi $t0, $t0, 1 #increment stones in next pocket

		#set stones of next pocket
		move $a0, $s3
		move $a1, $s4
		move $a2, $s0
		move $a3, $t0
		jal set_pocket #set stones in next pocket to prev stones + 1
		

		# move $a0, $s3
		# jal print_board
		
		addi $s1, $s1, -1 #decrement hand
		addi $s0, $s0, -1 #decrement distance
	j traverse_board

	switch_side:
		#add 1 to mancala if player turn == 
		bne $s2, $s4, skip_mancala

		move $a0, $s3
		move $a1, $s4
		li $a2, 1
		jal collect_stones
		addi $s1, $s1, -1 #decrement hand
		addi $s7, $s7, 1 #increment mancala stone counter

		skip_mancala:
			move $s0, $s5
			#addi $s0, $s0, 1 #####
		# switch_to_top:
		# 	li $t0, 'B'
		# 	bne $t0, $s4, switch_to_bot
		# 	li $t0, 'T'
		# 	move $s4, $t0
		# 	j traverse_board
		# switch_to_bot:
		# 	li $t0, 'B'
		# 	move $s4, $t0
		move $a1, $s4
		jal flip_letter
		move $s4, $v0
	j traverse_board
	evaluate_move:
		original_players_mancala:

			li $t1, -1
			bne $s2, $s4, otherwise #branch if not same side as player
			bgt $s0, $t1, empty_pocket #branch if distance >-1
			
			move $a0, $s3
			move $a1, $s4
			li $a2, 1
			jal collect_stones

			addi $s7, $s7, 1 #increment mancala stone counter
			li $v1, 2
			j end_move
		empty_pocket:
			#check if empty pocket

			#addi $s0, $s0, -1 #decrement distance


			move $a0, $s3
			move $a1, $s2
			move $a2, $s0
			jal get_pocket

			move $t0, $v0
			#li $t1, 1
			bne $t0, $0, otherwise

			addi $t0, $t0, 1 #increment stones in next pocket

			# #set stones of next pocket
			move $a0, $s3
			move $a1, $s2
			move $a2, $s0
			move $a3, $t0

			jal set_pocket #set stones in next pocket to prev stones + 1
			
			li $v1, 1
			addi $s1, $s1, -1 #decrement hand

			#j change_turn
			move $a1, $s2
			jal flip_letter
			sb $v0, 5($s3)

			j end_move
		otherwise:

			#if distance < 0 place stone in other side
			bltz $s0, skip_mancala_bruh
			j continue_ow
			# mancala_switch:
			# #add 1 to mancala if player turn == 
			# 	bne $s2, $s4, skip_mancala_bruh

			# 	move $a0, $s3
			# 	move $a1, $s4
			# 	li $a2, 1
			# 	jal collect_stones
			# 	addi $s1, $s1, -1 #decrement hand
			# 	addi $s7, $s7, 1 #increment mancala stone counter

		skip_mancala_bruh:
			move $s0, $s5
			move $a1, $s4
			jal flip_letter
			move $s4, $v0

		j evaluate_move

		continue_ow:
			move $a0, $s3
			move $a1, $s4
			move $a2, $s0
			jal get_pocket

			move $t0, $v0

			addi $t0, $t0, 1 #increment stones in next pocket

			# #set stones of next pocket
			move $a0, $s3
			move $a1, $s4
			move $a2, $s0
			move $a3, $t0
			li $v1, 1
			jal set_pocket #set stones in next pocket to prev stones + 1

			move $a1, $s2
			jal flip_letter
			sb $v0, 5($s3)

			addi $s1, $s1, -1 #decrement hand
			li $v1, 0


	# change_turn:
	# 	change_to_top:
	# 		li $t0, 'B'
	# 		bne $t0, $s4, change_to_bot
	# 		li $t0, 'T'
	# 		sb $t0, 5($s3)
	# 		j end_move
	# 	change_to_bot:
	# 		li $t0, 'B'
	# 		sb $t0, 5($s3)
	end_move:
	#increment moves
	lbu $t7, 4($a0) #moves executed
	addi $t7, $t7, 1 
	sb $t7, 4($a0) #increment moves executed by 1

	move $v0, $s7

	lw $ra, 0($sp)
	lw $s0, 4($sp) #origin pocket (distance)
	lw $s1, 8($sp) #origin pocket stones
	lw $s2, 12($sp)	#current player CHAR
	lw $s3, 16($sp) #GameState
	lw $s4, 20($sp) #current player side
	lw $s5, 24($sp) #number of pockets
	lw $s6, 28($sp) 
	lw $s7, 32($sp) 
	addi $sp, $sp, 36
	jr $ra

steal: #$a0: GameState, $a1: destination_pocket
	addi $sp, $sp, -32
	sw $ra, 0($sp)
	sw $s0, 4($sp) #destination_pocket
	sw $s1, 8($sp) #num pockets
	sw $s2, 12($sp)	#current letter
	sw $s3, 16($sp) #other player's letter
	sw $s4, 20($sp) #corresponding pocket
	sw $s5, 24($sp) #corresponding pocket's stones
	sw $s6, 28($sp) #num stones in destination pocket
	move $s7, $a0
	#flip letter
	#num pockets - 1 - destination pocket,
	#get number
	#set to 0
	#add to destination pocket 

	move $s0, $a1 #destination pocket
	lbu $s1, 2($a0) #num pockets
	lbu $s3, 5($a0) #other player's letter

	move $a1, $s3
	jal flip_letter
	move $s2, $v0 #get original side

	addi $t0, $s1, -1
	sub $s4, $t0, $s0 #corresponding pocket

	move $a1, $s3
	move $a2, $s4  
	jal get_pocket
	move $s5, $v0 #corresponding pocket's stones

	move $a1, $s3
	move $a2, $s4
	li $a3, 0
	jal set_pocket #empty corresponding pocket

	#jal print_struct

	move $a0, $s7
	move $a1, $s2
	move $a2, $s0
	jal get_pocket
	move $s6, $v0 #num stones in destination pocket

	move $a1, $s2
	move $a2, $s0
	li $a3, 0
	jal set_pocket #empty destination pocket

	add $t0, $s6, $s5
	move $a1, $s2
	move $a2, $t0
	jal collect_stones #collect to mancala

	add $v0, $s6, $s5

	lw $ra, 0($sp)
	lw $s0, 4($sp) #destination_pocket
	lw $s1, 8($sp) #num pockets
	lw $s2, 12($sp)	#current letter
	lw $s3, 16($sp) #other player's letter
	lw $s4, 20($sp) #corresponding pocket
	lw $s5, 24($sp) #corresponding pocket's stones
	lw $s6, 28($sp) #num stones in destination pocket
	addi $sp, $sp, 32
	jr $ra

flip_letter: #$a0: GameState, $a1: player char
	switch_to_B:
		li $t0, 'T'
		bne $a1, $t0, switch_to_T
		li $t0, 'B'
		move $v0, $t0
		jr $ra
	switch_to_T:
		li $t0, 'T'
		move $v0, $t0
	jr $ra

check_row: #$a0: GameState
	addi $sp, $sp, -24
	sw $ra, 0($sp)
	sw $s0, 4($sp)	#bot player row sum
	sw $s1, 8($sp)	#current sum
	sw $s2, 12($sp) #player char
	sw $s4, 16($sp) #top player row sum
	sw $s5, 20($sp) #struct
	li $s1, 0
	
	li $s2, 'B'
	move $a1, $s2
	jal sum_row
	move $s0, $v0 #bot row stones

	move $s5, $a0

	li $s2, 'T'
	move $a1, $s2
	jal sum_row
	move $s4, $v0 #top row stones

	beqz $s0, bot_row_zero
	beqz $s4, top_row_zero


	j no_zero_rows
	bot_row_zero:
		move $a0, $s5
		li $a1, 'T'
		li $a2, 0
		jal set_row

		# move $s5, $a0
		# jal print_struct

		# move $a0, $s5
		# li $a1, 'B'
		# move $a2, $s4

		move $a0, $s5
		li $a1, 'T'
		move $a2, $s4
		jal collect_stones #collect top
		lbu $s0, 0($s5)

		li $t0, 'D'
		sb $t0, 5($s5) 

		li $v0, 1
		j top_greater
	top_row_zero:
		li $a1, 'B'
		li $a2, 0
		jal set_row

		# li $a1, 'T'
		# move $a2, $s0

		move $a0, $s5
		li $a1, 'B'
		move $a2, $s0
		jal collect_stones #collect bot
		lbu $s4, 1($s5)

		li $t0, 'D'
		sb $t0, 5($s5) 

		li $v0, 1
		j top_greater
	no_zero_rows:
		li $v0, 0
		lbu $s0, 0($s5)
		lbu $s4, 1($s5)
		top_greater:
			bge $s0, $s4, bot_greater
			li $v1, 2
		j end_row_check
		bot_greater:
			ble $s0, $s4, tie
			li $v1, 1
		j end_row_check
		tie:
			li $v1, 0
	end_row_check:

	lw $ra, 0($sp)
	lw $s0, 4($sp)	#bot player row sum
	lw $s1, 8($sp)	#current sum
	lw $s2, 12($sp) #player char
	lw $s4, 16($sp) #top player row sum
	lw $s5, 20($sp) #struct
	addi $sp, $sp, 24
	jr $ra

	sum_row: #$a0: GameState, $a1: player char
		addi $sp, $sp, -20
		sw $ra, 0($sp)
		sw $s0, 4($sp) #GameStruct
		sw $s1, 8($sp) #player char
		sw $s2, 12($sp) #rolling total
		sw $s3, 16($sp) #num pockets - 1

		li $s2, 0 #rolling total
		move $s0, $a0

		lbu $s3, 2($s0) #get num pockets
		move $s1, $a1 #get player char

	add_pocket:
		addi $s3, $s3, -1
		bltz $s3, end_row_sum
	
		move $a1, $s1
		move $a2, $s3
		jal get_pocket
		
		add $s2, $s2, $v0
	j add_pocket
	end_row_sum:
	move $v0, $s2

	lw $ra, 0($sp)
	lw $s0, 4($sp) #GameStruct
	lw $s1, 8($sp) #player char
	lw $s2, 12($sp) #rolling total
	lw $s3, 16($sp) #num pockets - 1
	addi $sp, $sp, 20
	jr $ra

	set_row: #$a0: GameState, $a1: player char, $a2: size
		addi $sp, $sp, -20
		sw $ra, 0($sp)
		sw $s0, 4($sp) #GameState
		sw $s1, 8($sp) #player char
		sw $s2, 12($sp) #size
		sw $s4, 16($sp) #distance

		move $s0, $a0
		move $s1, $a1
		move $s2, $a2
		lbu $t0, 2($s0) #pockets

		addi $s4, $t0, -1 #max distance

		
		loop_row:

			bltz $s4, end_set_row
			move $a1, $s1 #player
			move $a2, $s4 #distance 
			move $a3, $s2 #size
			jal set_pocket

			addi $s4, $s4, -1

		j loop_row
		end_set_row:
		lw $ra, 0($sp)
		lw $s0, 4($sp) #GameState
		lw $s1, 8($sp) #player char
		lw $s2, 12($sp) #size
		lw $s4, 16($sp) #distance
		addi $sp, $sp, 20
		jr $ra
load_moves:#$a0: moves[], $a1: filename
	
	addi $sp, $sp -36
	sw $ra, 0($sp)
	sw $fp, 4($sp)
	sw $s0, 8($sp) #moves[]
	sw $s1, 12($sp) #filename
	sw $s3, 16($sp) #number of moves
	sw $s4, 20($sp) #line number
	sw $s5, 24($sp) #
	sw $s6, 28($sp) #num columns of moves[]
	sw $s7, 32($sp) #num rows of moves[]

	move $s5, $a0 #moves array
	move $s0, $a0 #moves array
	move $s1, $a1 #filename

	move $a0, $s1
	jal open_file

	bltz $v0, restore_move_stack #return -1 if file error

	move $s1, $v0
	

	#begin reading file
	li $t0, 0
	li $t5, 48
	read_moves_lines:

		li $t1, 0 #value of number
		li $t2, 0 #stack size

		move $a0, $s1
		
		move $fp, $sp
		j moves_newline_loop
		reset_sp:
			move $sp, $fp
		moves_newline_loop:
			addi $sp, $sp, -4 #move $sp to allocate more space
			addi $t2, $t2, 4 #increment stack counter

			move $a1, $sp
			li $a2, 1 
			li $v0, 14
			syscall #read 1 char from file and store onto $sp

			lbu $t3, 0($sp)

			li $t4, 13 #\r ASCII value
			beq $t3, $t4, moves_read_carriage_return
			li $t4, 10 #\n ASCII value
			beq $t3, $t4, moves_skip_carriage_return

		j moves_newline_loop

		moves_read_carriage_return:
			move $a1, $sp
			li $v0, 14
			syscall #skip \n after \r 
		moves_skip_carriage_return:
			addi $sp, $sp, 4
			addi $t2, $t2 -4
			j parse_array_values 

	parse_array_values:
		# addi $sp, $sp, 4 #increment $sp for extra -4
		# addi $t2, $t2, -4 #decrement stack counter for extra +4
		li $t7, 10
		li $t8, 3
		beq $t0, $t8, parse_moves
		li $t1, 0

		moves_reverse_stack:
			move $sp, $fp ######################
			addi $sp, $sp, -4
		moves_char_to_int:
			beqz $t2, set_array #branch if stack size == 0
			mul $t1, $t1, $t7 #multiply number by 10
			mflo $t1
			lb $t6, 0($sp)
			#parse integer and store into register
			sub $t6, $t6, $t5 #get integer value from ASCII
			add $t1, $t1, $t6

			#for each char subtract 4 to $sp
			addi $sp, $sp, -4 #get next value
			addi $t2, $t2, -4 #decrenent stack size
		j moves_char_to_int 
	#load row and column
	set_array:
		#compare newline counter ($t0) to 1,2,3 and 4,5 (for double chars)
		# move $sp, $fp
		# addi $sp, $sp, 4
		#move $s0, $t1
		addi $t0, $t0, 1 #linenumber++
		#move $t8, $s1

		set_columns: #when newline_counter = 1
			li $t7, 1
			bne $t0, $t7, set_rows
			move $s6, $t1 #$s6 holds number of columns
			j reset_sp
		set_rows: #when newline_counter = 2
			li $t7, 2
			bne $t0, $t7, skip_array_set
			# sb $t1, 0($s5)
			move $s7, $t1 #$s7 holds number of rows
			skip_array_set:
			j reset_sp
	parse_moves:		
		move $sp, $fp
		addi $sp, $sp, -4 #get bottom of stack
		li $s3, 0 #number of moves
		li $s4, 0 #value in move
		#li $t6, 0#beads in pocket
		li $t4, 10
		li $t9, 48
		li $t5, 4	

		addi $t0, $t0, 1 #increment newline counter
		move $s7, $s5
		
		bne $t0, $t5, end_load_move #if newline counter != 4 switch branches
		li $t3, 0
	moves_convert_to_int:
			#we want to get bottom 2 and put in string
			#check if first char is \r or \n
			#branch if number of moves == number of columns -> add 99
			beq $t3, $s6, next_move
			lbu $t8, 0($sp) #get first char of pocket
			li $t4, 1 #first char
			li $t5, 13
			beq $t8, $t5, end_load_move #skip \r
			li $t5, 10
			beq $t8, $t5, end_load_move#skip \n


			
				#check if 
			li $t5, 48
			blt $t8, $t5, not_valid_move
			li $t5, 57
			bgt $t8, $t5, not_valid_move


			beqz $t2, sp_reset #branch if stack size == 0

			li $t6, 48
			sub $t8, $t8, $t6
			#sb $t8, 0($t7) #store first ASCII char in gameboard


			move $t5, $t8
			#sub $t5, $t5, $t9
			mul $t5, $t5, $t4  #multiply by 10
			mflo $s4 #get left digit integer value

			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size

			#addi $t7, $t7, 1

			lbu $t8, 0($sp) #get second char of pocket
			li $t4, 2

			li $t5, 48
			blt $t8, $t5, not_valid_move
			li $t5, 57
			bgt $t8, $t5, not_valid_move


			sub $t8, $t8, $t6
			add $s4, $s4, $t8 #get right digit
			
			addi $sp, $sp, -4
			addi $t2, $t2, -4 #decrement stack size

			sb $s4, 0($s7) #store in array 

			addi $s7, $s7, 1
			addi $s3, $s3, 1 #add to number of moves
			addi $t3, $t3, 1
		j moves_convert_to_int
	not_valid_move:
		#load -1 into value
		#incremenet $sp
		addi $t4, $t4, -1
		bgtz $t4, second_char_invalid

		addi $sp, $sp, -4
		addi $t2, $t2, -4 #decrement stack size
		second_char_invalid:

		addi $sp, $sp, -4
		addi $t2, $t2, -4 #decrement stack size

		li $s4, -1
		sb $s4, 0($s7) #store negative if invalid move

		addi $s7, $s7, 1
		addi $s3, $s3, 1 #add to number of moves
		addi $t3, $t3, 1

	j moves_convert_to_int
	next_move:
		beqz $t2, end_load_move ##################
		sub $t3, $t3, $s6

		li $t5, 99
		sb $t5, 0($s7)

		addi $s7, $s7, 1
		addi $s3, $s3, 1
		j moves_convert_to_int

	end_load_move:
	#load from stack
		li $v0, 16
		syscall
		move $v0, $s3

		#addi $s7, $s7, 1 NEED??????????????
		li $t0, -2
		sb $t0, 0($s7)

		move $sp, $fp
		#addi $sp, $sp, 4
		lw $ra, 0($sp)
		lw $fp, 4($sp)
		lw $s0, 8($sp) #moves[]
		lw $s1, 12($sp) #filename
		lw $s3, 16($sp) #number of moves
		lw $s4, 20($sp) #line number
		lw $s5, 24($sp) #
		lw $s6, 28($sp) #num columns of moves[]
		lw $s7, 32($sp) #num rows of moves[]
		addi $sp, $sp, 36
		jr $ra

	restore_move_stack:
		move $t0, $v0
		li $v0, 16
		syscall
		move $v0, $t0

		move $sp, $fp
		lw $ra, 0($sp)
		lw $fp, 4($sp)
		lw $s0, 8($sp) #moves[]
		lw $s1, 12($sp) #filename
		lw $s3, 16($sp) #number of moves
		lw $s4, 20($sp) #line number
		lw $s5, 24($sp) #
		lw $s6, 28($sp) #num columns of moves[]
		lw $s7, 32($sp) #num rows of moves[]
		addi $sp, $sp, 36
		jr $ra

print_moves: #$a0: moves[], $a1: number of moves
	move $t0, $a0
	move $t1, $a1

	print_moves_loop:
		#beqz $t1, end_print_moves
		li $t3, -2

		lb $a0, 0($t0)

		beq $a0, $t3, end_print_moves
		
		li $v0, 1
		syscall

		li $a0, ' '
		li $v0, 11
		syscall

		addi $t0, $t0, 1
		addi $t1, $t1, -1
	j print_moves_loop
	end_print_moves:
	jr $ra

play_game: #$a0: moves_filename, #a1: board_filename, $a2: GameState, $a3: moves[],
	#arg4: num_moves_to_executee
	lw $t0, 0($sp)

	addi $sp, $sp, -40
	sw $ra, 0($sp)
	sw $s0, 4($sp)	
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)
	sw $s5, 24($sp)
	sw $s6, 28($sp)
	sw $s7, 32($sp)
	sw $fp, 36($sp)

	move $s6, $sp
	move $s0, $a0 #moves_filename
	move $s1, $a1 #board_filename
	move $s2, $a2 #GameState
	move $s3, $a3 #moves[]
	move $s4, $t0 #num_moves_to_execute

	move $a0, $s2 #game state
	move $a1, $s1 #board_filename
	jal load_game

	move $a0, $s3

	blez $v0, game_error
	bltz $s4, negative_moves_bruh
	#$v0: stones abide $v1: num pockets
	move $s7, $v1 #num pockets

	move $a0, $s3 #moves[]
	move $a1, $s0 #moves_filename
	jal load_moves

	move $a0, $s3
	li $a1, 18
	jal print_moves

	blez $v0, game_error
	#$v0: number of moves in file

	move $s0, $s3 #$s0 : moves[]
	play_moves:
		lb $s1, 0($s0) #origin pocket
		lbu $t0, 4($s2) #moves executed

		beq $t0, $s4, get_winner
		li $t0, -2
		beq $s1, $t0,  get_winner #end of moves

		#branch if move == 99
		li $t0, 99
		beq $s1, $t0, verify_and_skip

		move $a0, $s2
		lbu $a1, 5($s2) #player char
		move $a2, $s1
		jal get_pocket
		bltz $v0, game_error

		move $s5, $v0 #distance

		move $a0, $s2 #struct
		move $a1, $s1 #origin pocket
		move $a2, $s5 #distance
		jal verify_move
		blez $v0, skip_move #read next move
		li $t2, 2
		beq $v0, $t2, skip_move #move == 99 skip move

		li $a0, 10
		li $v0, 11
		syscall

		move $a0, $s2
		jal print_board ################################
 
		li $a0, 10
		li $v0, 11
		syscall

		# store num stones and pocket position
		move $a0, $s2
		move $a1, $s1 #origin pocket
		jal execute_move

		li $t1, 1
		beq $v1, $t1, steal_move

		move $a0, $s2
		jal print_board #######################################

		li $a0, 10
		li $v0, 11
		syscall ###############################


		addi $s0, $s0, 1
	j play_moves

	skip_move:
		addi $s0, $s0, 1 #get next move
		j play_moves
	j end_play_game

	verify_and_skip:
		move $a0, $s2
		move $a2, $s1
		jal verify_move
		addi $s0, $s0, 1 #get next move
		j play_moves
	steal_move:
		addi $t0, $s1, -1  #origin pocket - 1
		addi $t1, $s5, -1 #num stones (distance) - 1
		bgt $t0, $t1, normal_steal #if num stones <= pocket position
		beq $t0, $t1, normal_steal


		#origin pocket : $s1, num stone: $s5
		#2xppr + origin pocket + 1 - num stones
		lbu $t0, 2($s2) # num pockets
		sll $t0, $t0, 1 # 2xppr
		add $t0, $t0, $s1 # + origin pocket
		addi $t0, $t0, 1
		sub $t0, $t0, $s5
		move $a0, $s2
		move $a1, $t0
		jal steal

		move $a0, $s2
		jal print_board ################################
		
		addi $s0, $s0, 1
		j play_moves
		normal_steal:
			move $a0, $s2
			addi $t0, $t0, 1 #need position - stones
			sub $a1, $t0, $s5
			jal steal
		addi $s0, $s0, 1

		move $a0, $s2
		jal print_board #############################


	j play_moves

	game_error:
	li $v1, -1
	j end_play_game

negative_moves_bruh:
	move $a0, $s2
	jal check_row

	li $v1, 0 

	j end_play_game
	get_winner:
	#check row 
		move $a0, $s2
		jal check_row

		# move $a0, $s2
		# jal print_board
		lbu $v1, 4($s2)
		beqz $v0, end_play_game

		 #if $v0 == 1 bot win $v0 == 2 top win
		li $t9, 1
		beq $v0, $t9, bot_player_win
		li $t9, 2
		beq $v0, $t9, top_player_win
		
		top_player_win:
			li $v0, 2
			lbu $v1, 4($s2)
			j end_play_game
		bot_player_win:
			li $v0, 1
			lbu $v1, 4($s2)
	end_play_game:

	move $sp, $s6

	lw $ra, 0($sp)
	lw $s0, 4($sp)	
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	lw $s5, 24($sp)
	lw $s6, 28($sp)
	lw $s7, 32($sp)
	lw $fp, 36($sp)
	addi $sp, $sp, 40
	jr  $ra

print_board: #$a0: GameState
	move $t7, $a0
	lbu $t8, 2($t7) #num pockets
	sll $t8, $t8, 1
	lbu $t0, 1($t7)
	li $t1, 10

	div $t0, $t1
	
	mflo $t0
	move $a0, $t0
	li $v0, 1
	syscall

	mfhi $t0
	move $a0, $t0
	li $v0, 1
	syscall

	li $a0, 10
	li $v0, 11
	syscall
	############### first line
	lbu $t0, 0($t7)
	li $t1, 10

	div $t0, $t1
	
	mflo $t0
	move $a0, $t0
	li $v0, 1
	syscall

	mfhi $t0
	move $a0, $t0
	li $v0, 1
	syscall

	li $a0, 10
	li $v0, 11
	syscall
	############### second line
	addi $t7, $t7, 8

	li $t1, 0
	li $v0, 11
	printing_loop:
		beq $t8, $t1, next_line

		lbu $a0, 0($t7)
		syscall

		addi $t1, $t1, 1
		addi $t7, $t7, 1
	j printing_loop

	next_line:
	li $a0, 10
	syscall

	sll $t8, $t8, 1

	printing_loop_line_2:
		beq $t8, $t1, end_print_board

		lbu $a0, 0($t7)
		syscall

		addi $t1, $t1, 1
		addi $t7, $t7, 1
	j printing_loop_line_2
	end_print_board:
	li $a0, 10
	syscall
	jr $ra

write_board: #$a0: GameState
	addi $sp, $sp, -24
	sw $ra, 0($sp)
	sw $s0, 4($sp) #file descriptor
	sw $s1, 8($sp) #GameState
	sw $s2, 12($sp)
	sw $s4, 16($sp)
	sw $fp, 20($sp)
	

	addi $sp, $sp, -12
	li $t0, 'o'
	sb $t0, 0($sp)
	li $t0, 'u'
	sb $t0, 1($sp)
	li $t0, 't'
	sb $t0, 2($sp)
	li $t0, 'p'
	sb $t0, 3($sp)
	li $t0, 'u'
	sb $t0, 4($sp)
	li $t0, 't'
	sb $t0, 5($sp)
	li $t0, '.'
	sb $t0, 6($sp)
	li $t0, 't'
	sb $t0, 7($sp)
	li $t0, 'x'
	sb $t0, 8($sp)
	li $t0, 't'
	sb $t0, 9($sp)
	li $t0, 0
	sb $t0, 10($sp)

	move $fp, $sp

	move $s1, $a0

	move $a0, $sp
	li $a1, 1
	li $v0, 13
	syscall

	bltz $v0, end_write_board #return -1 if file error
	move $s0, $v0

	############################ bruh
	lbu $t8, 2($s1) #num pockets
	move $t3, $t8
	sll $t8, $t8, 1
	sll $t3, $t3, 2
	lbu $t0, 1($s1)
	li $t1, 10

	div $t0, $t1
	addi $sp, $sp, -4 #-4
	mflo $t0

	addi $t0, $t0, 48
	sb $t0, 0($sp) #left digit

	addi $sp, $sp, -4 #-4

	mfhi $t0
	addi $t0, $t0, 48
	sb $t0, 0($sp)
	
	addi $sp, $sp, -4 #-4
	
	li $t0, 10
	sb $t0, 0($sp)

	# li $t0, 0
	# sb $t0, 3($sp)

	# move $a0, $s0
	# move $a1, $s1
	# li $a2, 4
	# li $v0, 15
	# syscall

	lbu $t0, 0($s1)

	div $t0, $t1
	addi $sp, $sp, -4 #-4
	mflo $t0

	addi $t0, $t0, 48
	sb $t0, 0($sp) #left digit

	addi $sp, $sp, -4 #-4

	mfhi $t0
	addi $t0, $t0, 48
	sb $t0, 0($sp)
	
	addi $sp, $sp, -4 #-4

	li $t0, 10
	sb $t0, 0($sp)

	# li $t0, 0
	# sb $t0, 3($sp)

	# move $a0, $s0
	# move $a1, $s1
	# li $a2, 4
	# li $v0, 15
	# syscall

	lbu $t6, 8($s1)
	li $t5, 0 #pocket counter

	move $t4, $s1
	addi $t4, $t4, 8
	store_row:
		addi $sp, $sp, -4
		beq $t5, $t8, store_second_row
		lbu $t0, 0($t4)

		sb $t0, 0($sp)
		addi $t4, $t4, 1
		addi $t5, $t5, 1
	j store_row
	store_second_row:
		li $t0, 10
		sb $t0, 0($sp)
		beq $t8, $t3, store_end_of_string
		sll $t8, $t8, 1
	j store_row



	store_end_of_string:
			# addi $sp, $sp, -1
			# li $t0, 10
			# sb $t0, 0($sp)

			addi $sp, $sp, -4
			li $t0, 0
			sb $t0, 0($sp)

		move $sp, $fp
		addi $sp, $sp, -4

	##########################  writing to file
	write_board_loop:
		#write char until next byte
		lbu $t0, 0($sp)
		beqz $t0, end_write_board
		li $v0, 15
		move $a0, $s0
		move $a1, $sp
		#lbu $t9, 0($sp)
		li $a2, 1
		syscall
		addi $sp, $sp, -4
	j write_board_loop

	
	end_write_board:
	li $v0, 16
	move $a0, $s0
	syscall

	li $v0, 1



	move $sp, $fp

	addi $sp, $sp, 12

	lw $ra, 0($sp)
	lw $s0, 4($sp) #file descriptor
	lw $s1, 8($sp) #GameState
	lw $s2, 12($sp)
	lw $s4, 16($sp)
	lw $fp, 20($sp)
	addi $sp, $sp, 24
	jr $ra
	
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################