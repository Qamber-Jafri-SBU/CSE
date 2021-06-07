############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################
############################ DO NOT CREATE A .data SECTION ############################

############################## Do not .include any files! #############################

.text
eval:
  #$a0 the starting address of the expression
  #print BadToken if it contains invalid ops
  #print ParseError if not well formed i.e. (2+-3)
  addi $sp, $sp, -24
  sw $ra, 0($sp)
  sw $s0, 4($sp)
  sw $s1, 8($sp)
  sw $s2, 12($sp)
  sw $s3, 16($sp)
  sw $s4, 20($sp)

  
  la $s0, val_stack
  li $s1, 0 #val_stack tp
  la $s2, op_stack
  li $s3, 0 #op_stack tp
  move $s4, $a0 #The AExp to parse
  
  addi $s2, $s2, 2000
  move $s5, $a0  #move AExp to $s5
  move $s6, $a0
  
  
  traverse_expression:
    lb $s6, 0($s5) #first char of the expression
    move $a0, $s6
    
    # li $v0, 1
    # syscall

    beqz $s6, op_stack_not_empty #branch if char is null char
    move $a0, $s6 #move char to $a0
    jal is_digit #returns 1 if isdigit, 0 ow
    addi $s7, $v0, -1
    beqz $s7, is_number #if char is number branch
    li $s7, '('
    beq $s7, $s6, is_open_parens #if char is left parens branch
    li $s7, ')'
    li $t9, '(' #bruh
    beq $s7, $s6, is_closed_parens #if char is right parens branch
    jal valid_ops #returns 1 if isop, 0 ow
    move $s7, $v0
    beqz $s7, bad_token #branch if char is not valid op
    j is_op

    bad_token:
      la $a0, BadToken
      li $v0, 4
      syscall
      li $v0, 10
      syscall
    increment_char:
      addi $s5, $s5, 1
    j traverse_expression

    is_number:
      #push to valstack
      move $a0, $s4 #move AExp to  $a0
      move $a1, $s5
      jal get_number #returns int ($v0) and address of next char after number ($v1)
      move $a0, $v0 #move int to $a0
      move $s5, $v1

      # li $v0, 1
      # syscall #print int

      move $a1, $s1 #tp for val_stack
      move $a2, $s0 #addr for val_stack
      jal stack_push #element, tp, addr
      move $s1, $v0 #increment tp by 4
      move $s6, $v1 #move counter to end of number
      addi $s6, $s6, -1 #maybe have to decrement because traverse increase pos by 1
      j increment_char

    is_open_parens:
      move $a0, $s6 
      move $a1, $s3 #tp for op stack
      move $a2, $s2 #addr for op stack
      jal stack_push
    j increment_char

      #else if right parens
        #while not left parens pop op, pop val twice, apply bop, push val stack
        #if left parens pop

    is_closed_parens:
      move $a0, $s3 #tp of op stack
      move $a1, $s2 #addr of op stack
      jal stack_peek
      move $t8, $v0
      beq $t8, $t9, pop_open_parens

      addi $a0, $s3, -4
      move $a1, $s2
      jal stack_pop
      move $s3, $v0 #decrement op tp
      move $t6, $v1 #operator

      addi $a0, $s1, -4
      move $a1, $s0
      jal stack_pop
      move $s1, $v0 #decrement val tp
      move $t7, $v1 #second operand

      addi $a0, $s1, -4
      move $a1, $s0
      jal stack_pop
      move $s1, $v0 #decrement val tp
      move $t8, $v1 #first operand

      move $a0, $t8
      move $a1, $t6
      move $a2, $t7
      jal apply_bop #perform operation

      move $a0, $v0
      move $a1, $s1
      move $a2, $s0
      jal stack_push #push result onto val stack
      move $s1, $v0

    ##please work
    pop_open_parens:
      move $a0, $s3
      move $a1, $s2
      jal stack_pop
    j increment_char
    is_op:
      op_stack_not_empty_precendence_check:
      #while opstack not empty & tp of opstack has geq precedence as thischar
        #pop op, pop val twice, apply bop, push val stack
      #check if op stack empty
      addi $a0, $s3, -4 #op stack tp -4
      jal is_stack_empty
      addi $t8, $v0, -1 # $t8 0 if op stack is empty
      beqz $t8, op_stack_empty_lt_precedence #branch if op stack is empty

      move $a0, $s6 #char
      jal op_precedence
      move $t7, $v0 #$t7 -> current precedence

      addi $a0, $s3, -4 #op stack tp-4
      move $a1, $s2
      jal stack_peek #get val at tp
      
      move $a0, $v0 #val at op stack tp
      jal op_precedence
      move $t6, $v0 #$t6 -> tp precedence
      
      blt $t6, $t7, op_stack_empty_lt_precedence #branch if tp precedence < current precedence

      #### evaluating
      addi $a0, $s3, -4
      move $a1, $s2
      jal stack_pop
      move $s3, $v0 #decrement op tp
      move $t6, $v1 #operator

      # addi $a0, $s1, -4
      # jal is_stack_empty 
      # move $t8, 
      addi $a0, $s1, -4
      move $a1, $s0
      jal stack_pop
      move $s1, $v0 #decrement val tp
      move $t7, $v1 #second operand

      addi $a0, $s1, -4
      move $a1, $s0
      jal stack_pop
      move $s1, $v0 #decrement val tp
      move $t8, $v1 #first operand

      move $a0, $t8
      move $a1, $t6
      move $a2, $t7
      jal apply_bop #perform operation

      move $a0, $v0
      move $a1, $s1
      move $a2, $s0
      jal stack_push #push result onto val stack
      move $s1, $v0

      #####

      j op_stack_not_empty_precendence_check

      op_stack_empty_lt_precedence:
      move $a0, $s6 #move op to $a0
      move $a1, $s3 #tp for op_stack
      move $a2, $s2 #addr for op_stack
      jal stack_push #element, tp, addr
      move $s3, $v0 #updated op stack tp
      j increment_char

op_stack_not_empty:
  #while opstack not empty, pop op, pop val twice, apply bop, push val stack
  addi $a0, $s3, -4
  jal is_stack_empty
  addi $s7, $v0, -1 # -1 -> not empty 0 -> empty
  beqz $s7, get_result #break if $s7 is 0

  addi $a0, $s3, -4
  move $a1, $s2
  jal stack_pop
  move $s3, $v0 #decrement op tp
  move $t6, $v1 #operator

  addi $a0, $s1, -4
  move $a1, $s0
  jal stack_pop
  move $s1, $v0 #decrement val tp
  move $t7, $v1 #second operand

  addi $a0, $s1, -4
  move $a1, $s0
  jal stack_pop
  move $s1, $v0 #decrement val tp
  move $t8, $v1 #first operand

  move $a0, $t8
  move $a1, $t6
  move $a2, $t7
  jal apply_bop #perform operation

  move $a0, $v0
  move $a1, $s1
  move $a2, $s0
  jal stack_push #push result onto val stack
  move $s1, $v0
  j op_stack_not_empty

get_result:
  #if op stack not empty -> parse error
  addi $a0, $s3, -4
  jal is_stack_empty
  beqz $v0, parse_error

  addi $a0, $s1, -4
  move $a1, $s0
  jal stack_pop
  move $s1, $v0

  addi $a0, $s1, -4
  jal is_stack_empty
  beqz $v0, parse_error
  #if val stack not empty -> parse error
  
  move $a0, $v1
  li $v0, 1
  syscall

  lw $ra, 0($sp)
  lw $s0, 4($sp)
  lw $s1, 8($sp)
  lw $s2, 12($sp)
  lw $s3, 16($sp)
  lw $s4, 20($sp)
  addi $sp, $sp, 24
  jr $ra

parse_error:
  la $a0, ParseError
  li $v0, 4
  syscall
  li $v0, 10
  syscall

is_digit:
  li $v0, 0
  li $t0, 48
  blt $a0, $t0, skip_above_ASCII_of_9
  li $t0, 58
  slt $v0, $a0, $t0
  skip_above_ASCII_of_9:
  jr $ra

#$a0, element to push to stack, $a1 element on top of the stack, 
#$a2 base address of the stack
stack_push:

  addi $t0, $a2, 2000
  bge $a1, $t0, stack_error
  add $a2, $a2, $a1
  sw $a0, 0($a2)
  addi $a1, $a1, 4
  move $v0, $a1
  jr $ra

  stack_error:
    la $a0, ParseError
    li $v0 4
    syscall
    li $v0 10
    syscall

stack_peek:
  li $t0, -4
  beq $t0, $a0, stack_error #empty stack exception
  move $v0, $a0
  li $t0, 0
  add $t0, $a0, $a1
  lw $v0, 0($t0)
  jr $ra

stack_pop:
  li $t0, -4
  beq $t0, $a0, stack_error #empty stack exception
  move $v0, $a0
  add $t0, $a1, $a0
  lw $v1, 0($t0)
  jr $ra

is_stack_empty:
  li $t0, -3
  slt $v0, $a0, $t0
  jr $ra

valid_ops:
  case_Plus:
    li $t0, '+'
    bne $a0, $t0, case_Minus
    li $v0, 1
    j skip_operator_cases
  case_Minus:
    li $t0, '-'
    bne $a0, $t0, case_Times
    li $v0, 1
    j skip_operator_cases
  case_Times:
    li $t0, '*'
    bne $a0, $t0, case_Slash
    li $v0, 1
    j skip_operator_cases
  case_Slash:
    li $t0, '/'
    bne $a0, $t0, default_op
    li $v0, 1
    j skip_operator_cases
  default_op:
    li $v0, 0
    jr $ra
  skip_operator_cases:
  jr $ra

op_precedence:
  # case_Open_Parentheses:
  #   li $t0, '('
  #   bne $a0, $t0, case_Close_Parentheses
  #   li $v0, 3
  #   j skip_operator_precedence_cases
  # case_Close_Parentheses:
  #   li $t0, ')'
  #   bne $a0, $t0, case_Multiplication
  #   li $v0, 3
  #   j skip_operator_precedence_cases
  case_Multiplication:
    li $t0, '*'
    bne $a0, $t0, case_Division
    li $v0, 2
    j skip_operator_precedence_cases
  case_Division:
    li $t0, '/'
    bne $a0, $t0, case_Addition
    li $v0, 2
    j skip_operator_precedence_cases
  case_Addition:
    li $t0, '+'
    bne $a0, $t0, case_Subtraction
    li $v0, 1
    j skip_operator_precedence_cases
  case_Subtraction:
    li $t0, '-'
    bne $a0, $t0, not_a_valid_op
    li $v0, 1
    j skip_operator_precedence_cases
  not_a_valid_op:
    la $a0, BadToken
    li $v0, 4
    syscall
    li $v0, 10
    syscall
  skip_operator_precedence_cases:
  jr $ra

apply_bop:
  #$a0 op1, $a1 operator, $a2 op2
  move $t0, $a0,
  move $a0, $a1,
  move $a1, $t0
  #a0 operator, $a1 op1, $a2 op2
  addi $sp, $sp, -4
  sw $ra, 0($sp) #store calling fxns address
  jal op_precedence #returns 1 if +,- returns 2 if *,/
  lw $ra, 0($sp) #load calling fxns address
  addi $sp, $sp, 4
  li $t0, 2
  bne $v0, $t0, add_operation

  li $t0 '*'
  bne $a0, $t0, divide_operation
  mult $a1, $a2
  mflo $v0
  j skip_add_operation
  divide_operation:
    beq $a2, $0, div_by_zero
    bltz $a1, first_neg
    bltz $a2, second_neg
    div $a1, $a2
    mflo $v0
    j skip_add_operation
    first_neg:
      li $t0, 1
      bltz $a2, both_neg
      div $a1, $a2
      mfhi $t1
      beqz $t1, both_neg
      mflo $t0
      addi $v0, $t0, -1
      j skip_add_operation
    second_neg:
      div $a1, $a2
      mflo $t0
      addi $v0, $t0, -1
      j skip_add_operation
    both_neg:
      div $a1, $a2
      mflo $v0
      j skip_add_operation
    div_by_zero:
      la $a0, ApplyOpError
      li $v0, 4
      syscall
      li $v0, 10
      syscall
  add_operation:
    li $t0, '-'
    move $v0, $a1
    bne $a0, $t0, sum
    sub $a2, $0, $a2
    sum:
    add $v0, $v0, $a2
  skip_add_operation:
  jr $ra

#takes expression as string ($a0) and address of char in string ($a1)
#returns int within a string and the address of next char
get_number:
  move $t1, $a1 #address of first char to be read
  li $t2, 0
  li $t3, 10
  add $t5, $0, $a1
  addi $sp, $sp, -8
  sw $ra, 0($sp)
  sw $a0, 4($sp)
  traverse_string: #traverse string and read each digit
    lb $a0, 0($t1)
    jal is_digit
    move $t4, $v0
    beqz $t4, return_int
    mult $t2, $t3
    mflo $t2
    andi $a0, $a0, 0x0F #convert ASCII to int
    add $t2, $t2, $a0
    addi $t1, $t1, 1
    addi $t5, $t5, 1
  j traverse_string
  return_int:
  move $v0, $t2
  addi $v1, $t1, -1 
  lw $a0, 4($sp)
  lw $ra, 0($sp)
  addi $sp, $sp, 8
  jr $ra