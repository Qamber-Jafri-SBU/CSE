############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
.text:

str_len:
	#$a0: .ascciz string
	#$v0: string length
	move $t0, $a0
	li $t1, 0 #counter

	read_string:

		lb $t2, 0($t0)
		beqz $t2, end_str_len

		addi $t1, $t1, 1
		addi $t0, $t0, 1
	
	j read_string
	end_str_len:
	move $v0, $t1
	jr $ra

str_equals:
	#$a0: str1, $a1: str2
	#$v0: 1 if str1 = str2, 0 ow
	addi $sp, $sp, -16
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)

	move $s0, $a0 #str1
	move $s1, $a1 #str2

	jal str_len
	move $s2, $v0 #str1.length

	move $a0, $s1
	jal str_len #get str2 length
	bne $s2, $v0, unequal_str

	move $t0, $s0
	move $t1, $s1
	check_char_equality:
		lb $t2, 0($t0) #char 1
		lb $t3, 0($t1) #char 2

		bne $t2, $t3, unequal_str
		beqz $t2, equal_string
		addi $t0, $t0, 1
		addi $t1, $t1, 1
	j check_char_equality
	equal_string:
		bnez $t3, unequal_str

		li $v0, 1
		j end_str_equals
	unequal_str:
		li $v0, 0
	end_str_equals:		

	
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	addi $sp, $sp, 16	
	jr $ra

str_cpy:
	#$a0: src string, $a1: dest string
	#$v0: number of characters copied
	move $t0, $a0 #src
	move $t1, $a1 #dest

	li $t3, 0 #counter	
	copy_string:

		lb $t2, 0($t0)
		sb $t2, 0($t1)
		addi $t3, $t3, 1
		beqz $t2, end_str_cpy

		addi $t0, $t0, 1
		addi $t1, $t1, 1

	j copy_string
	end_str_cpy:
	addi $t3, $t3, -1
	move $v0, $t3
	jr $ra

create_person:
	#$a0: network addr
	#$v0: address of person node in network or -1 if no free nodes

	addi $sp, $sp, -4
	sw $ra, 0($sp)

	move $t0, $a0
	lw $t1, 16($t0) #current number of nodes
	lw $t2, 0($t0) #total number of nodes

	# move $a0, $t0
	# jal is_graph_invalid
	# bltz $v0, full_network

	beq $t1, $t2, full_network

	lw $t2, 8($t0) #size of each node
	#address = curr_nodes*size_of_node + 32

	mul $v0, $t1, $t2
	add $v0, $t0, $v0
	addi $v0, $v0, 36
	addi $t1, $t1, 1
	sw $t1, 16($t0) #add 1 to current number of nodes
	j end_create_person

	full_network:
	li $v0, -1
	
	end_create_person:
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	jr $ra
is_person_exists:
	#$a0: network*, $a1: person node*
	#$v0: 1 if person exists, 0 ow
	addi $sp, $sp, -4
	sw $ra, 0($sp)

	move $t0, $a0	#network
	move $t1, $a1 	#person node

	move $a0, $t0
	jal is_graph_invalid
	bltz $v0, person_not_exist

	lw $t2, 8($t0) #node_size
	lw $t3, 16($t0) #curr num nodes

	addi $t0, $t0, 36 #nodes array
	traverse_nodes:
		beqz $t3, person_not_exist #check if num nodes == 0
		beq $t0, $t1, person_exists #check if person node == curr node
	
		add $t0, $t0, $t2 
		addi $t3, $t3, -1
	j traverse_nodes
	person_exists:
		li $v0, 1
		j end_is_person_exists
	person_not_exist:
		li $v0, 0
	end_is_person_exists:
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	jr $ra

is_person_name_exists:
	#$a0: network*, $a1: name
	addi $sp, $sp, -20
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)


	move $s0, $a0 	#network
	move $s1, $a1	#name char[]*

	# move $a0, $s0
	# jal is_graph_invalid
	# bltz $v0, name_not_exist

	lw $s2, 8($s0) #node_size
	lw $s3, 16($s0) #curr num nodes

	addi $s0, $s0, 36 #nodes array
	find_name:
		beqz $s3, name_not_exist #check if num nodes == 0
		
		move $a0, $s0
		move $a1, $s1
		jal str_equals #1 if strings are equal
		
		li $t0, 1
		beq $v0, $t0, name_exists #branch if nodes[] contains name

		add $s0, $s0, $s2 
		addi $s3, $s3, -1
	j find_name
	name_exists:
		li $v0, 1
		move $v1, $s0
		j end_is_person_name_exists
	name_not_exist:
		li $v0, 0
	end_is_person_name_exists:
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	addi $sp, $sp, 20
	jr $ra

add_person_property:
	#$a0: network*, $a1: person node*, $a2: prop_name char[]*, $a3: prop_val char[]*
	#

	addi $sp, $sp, -28
	sw $ra, 0($sp)
	sw $s0, 4($sp)		
	sw $s1, 8($sp)	
	sw $s2, 12($sp)	
	sw $s3, 16($sp)	
	sw $s4, 20($sp)	
	sw $s5, 24($sp)	

	move $s0, $a0 #network*
	move $s1, $a1 #person node
	move $s2, $a2 #prop_name 
	move $s3, $a3 #prop_val

	# move $a0 , $s0
	# jal is_graph_invalid
	# bltz $v0, add_person_property_person_not_exist

	lw $s4, 8($s0) #size of node
	lw $s5, 16($s0) #current num nodes

	#check if $a2 is equal to NAME\0 -> if not $v0 = 0
	move $t0, $s0
	addi $a0, $t0, 24
	#lw $a0, 24($s0) #NAME PROPERTY
	move $a1, $s2
	jal str_equals
	beqz $v0, end_add_person_property

	#if is_person_exists == 0 -> $v0 = -1
	move $a0, $s0
	move $a1, $s1
	jal is_person_exists
	beqz $v0, add_person_property_person_not_exist

	#if length of prop_val > size of node -> $v0 = -2
	move $a0, $s3
	jal str_len
	bgt $v0, $s4, val_too_large

	#loop through all nodes and if currnode.string == prop_val -> $v0 = -3

	move $a0, $s0
	move $a1, $s3
	jal is_person_name_exists

	li $t0, 1
	beq $v0, $t0, non_unique_val

	# addi $s6, $s0, 36

	# get_names:
	# 	beqz $s5, unique_val
		
	# 	move $a0, $s6
	# 	move $a1, $s3
	# 	jal str_equals
	# 	bnez $v0, non_unique_val

	# 	add $s6, $s6, $s4
	# 	addi $s5, $s5, -1
	# j get_names

	move $a0, $s3
	move $a1, $s1
	jal str_cpy

	li $v0, 1
	j end_add_person_property
	non_unique_val:
	li $v0, -3
	j end_add_person_property
	val_too_large:
	li $v0, -2
	j end_add_person_property
	add_person_property_person_not_exist:
	li $v0, -1
	end_add_person_property:
	lw $ra, 0($sp)
	lw $s0, 4($sp)		
	lw $s1, 8($sp)	
	lw $s2, 12($sp)	
	lw $s3, 16($sp)	
	lw $s4, 20($sp)	
	lw $s5, 24($sp)	
	addi $sp, $sp, 28
	jr $ra

get_person:
	#$a0: network*, $a1: name char[]*
	addi $sp, $sp, -4
	sw $ra, 0($sp)

	move $t0, $a0
	jal is_graph_invalid
	bltz $v0, full_network

	move $a0, $t0
	jal is_person_name_exists
	beqz $v0, end_get_person
	move $v0, $v1

	end_get_person:
	lw $ra, 0($sp) 
	addi $sp, $sp, 4
	jr $ra
is_relation_exists:
	#$a0: network*, $a1: person1 node*, $a2: person2 node*
	#$v0: 1 if there is an edge between nodes, 0 ow

	addi $sp, $sp, -24
	sw $ra, 0($sp)
	sw $s0, 4($sp)
	sw $s1, 8($sp)
	sw $s2, 12($sp)
	sw $s3, 16($sp)
	sw $s4, 20($sp)


	move $s0, $a0 #network
	move $s1, $a1 #person 1
	move $s2, $a2 #person 2

	# move $a0, $s0
	# jal is_graph_invalid
	# bltz $v0, full_network

	#loop through edges array 
	#need edge size, edge count

	lw $s3, 12($s0) #edge size
	lw $s4, 20($s0) #curr num edges
	lw $t1, 8($s0) #size of nodes

	lw $t0, 0($s0) #total nodes


	addi $s0, $s0, 36
	mul $t0, $t0, $t1
	add $s0, $s0, $t0 #number of bytes until edges array

	find_relation:
		beqz $s4, relation_not_exists
		lw $t0, 0($s0) #get first person addr
		lw $t1, 4($s0) #get second person addr
	
		beq $s1, $t0, is_person_one_equal #p1 = first addr
		beq $s2, $t0, is_person_two_equal #p2 = first addr

		j continue_relation_find

		is_person_one_equal:
			beq $s2, $t1, relation_exists
		j continue_relation_find		
		is_person_two_equal:
			beq $s1, $t1, relation_exists
		j continue_relation_find	
		continue_relation_find:
		addi $s0, $s0, 12
		addi $s4, $s4, -1
	j find_relation
	relation_not_exists:
		li $v0, 0
		j end_is_relation_exists
	relation_exists:
		li $v0, 1
		move $v1, $s0

	end_is_relation_exists:
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)
	lw $s2, 12($sp)
	lw $s3, 16($sp)
	lw $s4, 20($sp)
	addi $sp, $sp, 24
	jr $ra

add_relation:
	#$a0: network, $a1: person1 node*, $a2: person2 node*
	addi $sp, $sp, -28
	sw $ra, 0($sp)
	sw $s0, 4($sp)		
	sw $s1, 8($sp)	
	sw $s2, 12($sp)	
	sw $s3, 16($sp)	
	sw $s4, 20($sp)	
	sw $s5, 24($sp)	

	move $s0, $a0 #network
	move $s1, $a1 #person 1 node
	move $s2, $a2 #person 2 node

	# move $a0, $s0
	# jal is_graph_invalid
	# bltz $v0, full_edge_list

	#if is_person_exists = 0 for p1 or p2 -> $v0 = 0
	move $a0, $s0
	move $a1, $s1
	jal is_person_exists #does p1 exist
	beqz $v0, person_does_not_exist

	move $a0, $s0
	move $a1, $s2
	jal is_person_exists #does p2 exist
	beqz $v0, person_does_not_exist

	#if curr num edges == total num edges -> $v0 = -1
	lw $s3, 20($s0) #curr number of edges
	lw $t1, 4($s0) #total number of edges
	beq $s3, $t1, full_edge_list

	#if is_relation_exists -> $v0 = -2
	move $a0, $s0
	move $a1, $s1
	move $a2, $s2
	jal is_relation_exists

	li $t0, 1
	beq $v0, $t0, relation_already_exists

	#if p1 = p2 -> $v0 = -3
	beq $s1, $s2, relation_with_same_person

	lw $t0, 12($s0) #size of edge
	mul $s4, $s3, $t0 #get first available edge
	add $s5, $s4, $s0

	sw $s1, 0($s5)
	sw $s2, 4($s5)
	sw $0,  8($s5)
	
	addi $t0, $t0, 1
	sw $t0, 20($s0)

	li $v0, 1
	j end_add_relation
	relation_with_same_person:
		li $v0, -3
		j end_add_relation
	relation_already_exists:
		li $v0, -2
		j end_add_relation
	full_edge_list:
		li $v0, -1
	person_does_not_exist:
	#$v0 already has 0
	end_add_relation:
	lw $ra, 0($sp)
	lw $s0, 4($sp)		
	lw $s1, 8($sp)	
	lw $s2, 12($sp)	
	lw $s3, 16($sp)	
	lw $s4, 20($sp)	
	lw $s5, 24($sp)	
	addi $sp, $sp, 28
	jr $ra
add_relation_property:
	#$a0: network, $a1: person1, $a2: person2, $a3: prop_name, $a4: prop_value

	lw $t0, 0($sp) #prop value

	addi $sp, $sp, -28
	sw $ra, 0($sp)
	sw $s0, 4($sp)		
	sw $s1, 8($sp)	
	sw $s2, 12($sp)	
	sw $s3, 16($sp)	
	sw $s4, 20($sp)	
	sw $s5, 24($sp)	

	
	move $s0, $a0 #network
	move $s1, $a1 #person1 node
	move $s2, $a2 #person 2 node
	move $s3, $a3 #prop_name
	move $s4, $t0 #prop_value

	# move $a0 , $s0
	# jal is_graph_invalid
	# bltz $v0, property_not_friend

	#if is_relation_exists = 0 -> $v0 = 0
	move $a0, $s0
	move $a1, $s1
	move $a2, $s2
	jal is_relation_exists
	beqz $v0, add_relation_rel_not_exist
	move $s5, $v1 #relation address

	#if str equals (prop name) ("FRIEND") = 0 -> $v0 = -1
	move $a0, $s3
	addi $a1, $s0, 29
	jal str_equals
	beqz $v0, property_not_friend

	#if prop_val < 0 -> -2
	bltz $s4, property_value_ltz

	sw $s4, 8($s5) #store prop_val in relation
	li $v0, 1
	j end_add_person_property

	property_value_ltz:
	li $v0, -2
	j end_add_relation_property
	property_not_friend:
	li $v0, -1
	add_relation_rel_not_exist:
	#$v0 already = 0
	end_add_relation_property:
	lw $ra, 0($sp)
	lw $s0, 4($sp)		
	lw $s1, 8($sp)	
	lw $s2, 12($sp)	
	lw $s3, 16($sp)	
	lw $s4, 20($sp)	
	lw $s5, 24($sp)	
	addi $sp, $sp, 28	
	jr $ra

is_friend_of_friend:
	#$a0: network, $a1: name1 char[]*, $a2: name2 char[]*

	#if name1 and name2 are mutual friends -> $v0 = 1
	#if name1 and name2 are not mutual friends -> $v0 = 0

	addi $sp, $sp, -28
	sw $ra, 0($sp)
	sw $s0, 4($sp)		
	sw $s1, 8($sp)	
	sw $s2, 12($sp)	
	sw $s3, 16($sp)	
	sw $s4, 20($sp)	
	sw $s5, 24($sp)	

	move $s0, $a0 #network
	move $s1, $a1 #name1
	move $s2, $a2 #name2

	# move $a0 , $s0
	# jal is_graph_invalid
	# bltz $v0, full_network


	move $a0, $s0
	move $a1, $s1
	move $a2, $s2
	jal is_relation_exists
	bnez $v0, no_mutuals

	#if name1 or name2 are not in the network -> $v0 = -1
	move $a0, $s0
	move $a1, $s1
	jal is_person_name_exists
	beqz $v0, name_not_in_network
	move $s3, $v1 #person 1

	move $a0, $s0
	move $a1, $s2
	jal is_person_name_exists
	beqz $v0, name_not_in_network
	move $s4, $v1 #person 2

	#get edge array starting addr
	lw $t0, 0($s0)
	lw $t1, 8($s0)
	mul $t0, $t0, $t1
	addi $t0, $t0, 36
	add $t0, $t0, $s0

	#get person for each name
	move $s5, $t0
	lw $t0, 4($s0)
	lw $t1, 12($s0)
	mul $t0, $t0, $t1

	add $s7, $s5, $t0 #end of edges[]
	#$s3 = person 1 addr, $
	find_friends:
		beq $s5, $s7, no_mutuals #check if end of edges is reached

		lw $s6, 0($s5) #first p in relation addr
		beq $s6, $s3, find_mutuals_one
		
		lw $s6, 4($s5) #second p in relation addr
		beq $s6, $s3, find_mutuals_two

		addi $s5, $s5, 12

	j find_friends
		find_mutuals_one:
			move $a0, $s0
			lw $a1, 4($s5)
			move $a2, $s4
			jal is_relation_exists
			
			li $t0, 1
			beq $v0, $t0, mutuals_friends
			addi $s5, $s5, 12
	j find_friends
		find_mutuals_two:
			move $a0, $s0
			lw $a1, 0($s5)
			move $a2, $s4
			jal is_relation_exists
			
			li $t0, 1
			beq $v0, $t0, mutuals_friends
			addi $s5, $s5, 12
	j find_friends

	no_mutuals:
		li $v0, 0
		j end_is_friend_of_friend
	mutuals_friends:
		li $v0, 1
		j end_is_friend_of_friend
	name_not_in_network:
	li $v0, -1
	end_is_friend_of_friend:

	lw $ra, 0($sp)
	lw $s0, 4($sp)
	lw $s1, 8($sp)	
	lw $s2, 12($sp)	
	lw $s3, 16($sp)	
	lw $s4, 20($sp)	
	lw $s5, 24($sp)	
	addi $sp, $sp, 28
	jr $ra

is_graph_invalid:
	#check if total/curr/size/ are negative
	#edge size must equal 12
	#check if nodes array matches curr nodes and total nodes / size
	#check if edges array matches curr edges and total edges / size

	#$a0: network

	lw $t9, 0($a0)
	blez $t9, invalid_graph

	lw $t9, 4($a0)
	blez $t9, invalid_graph

	lw $t9, 8($a0)
	blez $t9, invalid_graph

	lw $t9, 12($a0)
	li $t8, 12
	bne $t9, $t8, invalid_graph

	lw $t9, 16($a0)
	bltz $t9, invalid_graph

	lw $t9, 20($a0)
	bltz $t9, invalid_graph

	li $v0, 0
	j end_is_graph_invalid
	invalid_graph:
	li $v0, -1
	end_is_graph_invalid:
	jr $ra