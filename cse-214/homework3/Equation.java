/**
 * The Equation Class creates Equation objects that hold information,
 *  infix, prefix, and postfix representations, the answer in base-10,
 *  base-2, and base-16.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.Stack;
public class Equation{

    private String equation, prefix, postfix; //infix, prefix, and postfix representations of equation
    private double answer;  //solution to equation
    private String binary, hex; //binary and hex representations of the answer
    private boolean balanced; //whether or not the equation has even number of zeroes, no double operators, no division by zero

    /**
     * Creates an instance of Equation
     */
    public Equation(){
    }

    /**
     * Creates an instance of Equation
     */
    public Equation(String equation){
        this.equation = equation;
        balanced = isValid();
        postfix = calculatePostfix();
        prefix = calculatePrefix();
        answer = computeAnswer();
        binary = getBinary();
        hex = getHex();
    }

    /**
     * 
     * @return 
     * Solution to equation in base-10
     */
    public double getAnswer(){
        return answer; 
    }

    /**
     * 
     * @return
     * Solution to equation in base-2 
     */
    public String getBinary(){
        return binary = decToBin((int)Math.round(answer));
    }

    /**
     * 
     * @return
     * Solution to equation in base-16
     */
    public String getHex(){
        return hex = decToHex((int)Math.round(answer));
    }

    /**
     * 
     * @return
     * Returns prefix representation of equation
     */
    public String getPrefix(){
        return prefix;
    }

    /**
     * 
     * @return
     * Returns postfix representation of equation
     */
    public String getPostfix(){
        return postfix;
    }

    /**
     * Returns an assigned integer value for a character
     * 
     * @param c
     * The character to be checked
     * 
     * @return
     * 0 when c= $, 1 when c= (, 2 when c= +, -, 3 when c= *, /, %, 4 when c= ^, -1 otherwise
     */
    public int getOperatorPrecedence(char c){
        if(c == '$')
            return 0;
        if(c == '(')
            return 1;
        if(c == '+' || c == '-')
            return 2;
        if(c == '*' || c == '/' || c == '%')
            return 3;
        if(c == '^')
            return 4;

        return -1;            
    }

    /**
     * Calculates answer to equation
     * 
     * @return
     * Returns the answer in base-10
     */
    public double computeAnswer(){
        EquationStack numStack = new EquationStack();
        double operand1, operand2, result = 0;
        String temp;
        if(!isValid()){
            return result;
        }
        for(int i = 0; i < postfix.length(); i++){
            temp = "";
            while(i < postfix.length() && Character.isDigit(postfix.charAt(i))){
                temp += postfix.charAt(i);
                i++;
            }
            if(temp != ""){
                numStack.push(temp);
            }
            if(i == postfix.length()){
                i--;
            }
            if(getOperatorPrecedence(postfix.charAt(i)) > 1){
                operand2 = Double.parseDouble(numStack.pop());
                operand1 = Double.parseDouble(numStack.pop());
                if(postfix.charAt(i) == '+'){
                    result = operand1 + operand2;
                }else if(postfix.charAt(i) == '-'){
                    result = operand1 - operand2;
                }else if(postfix.charAt(i) == '*'){
                    result = operand1 * operand2;
                }else if(postfix.charAt(i) == '/'){
                    result = operand1 / operand2;
                }else if(postfix.charAt(i) == '%'){
                    result = operand1 % operand2;
                }else if(postfix.charAt(i) == '^'){
                    result = Math.pow(operand1, operand2);
                }
                numStack.push(Double.toString(result));
            }
        }
        result = Double.parseDouble(numStack.pop());
        return result;
    }

    /**
     * Calculate prefix representation of equation
     * 
     * @return
     * Returns prefix representation
     */
    public String calculatePrefix(){
        Stack<Character> operators = new Stack<Character>();
        EquationStack operands = new EquationStack();
        String temp, operand1, operand2;
        prefix = "";

        operators.push('$');
        if(isValid()){
            for(int i = 0; i < equation.length(); i++){
                temp = "";
                if(equation.charAt(i) == ' '){
                    continue;
                }
                while(Character.isDigit(equation.charAt(i))){

                    temp += equation.charAt(i);
                    if(i == equation.length() - 1 || equation.charAt(i + 1) == ' '){
                        break;
                    }
                    i++;

                }
                if(temp != ""){
                    operands.push(temp);
                }

                if(equation.charAt(i) == '(' || equation.charAt(i) == '[' || equation.charAt(i) == '{'){
                    operators.push(equation.charAt(i));
                }else if(equation.charAt(i) == ')' || equation.charAt(i) == ']' || equation.charAt(i) == '}'){
                    while(operators.peek() != '(' && operators.peek() != '[' && operators.peek() != '{'){
                        operand2 = operands.pop();
                        operand1 = operands.pop();
                        operands.push(operators.pop() + " " + operand1 + " " + operand2);
                    }
                    operators.pop();
                }
                else if(!Character.isDigit(equation.charAt(i)) && getOperatorPrecedence(equation.charAt(i)) > getOperatorPrecedence(operators.peek())){
                    operators.push(equation.charAt(i));
                }else if(!Character.isDigit(equation.charAt(i)) && getOperatorPrecedence(equation.charAt(i)) <= getOperatorPrecedence(operators.peek())){
                    while(!Character.isDigit(equation.charAt(i)) && getOperatorPrecedence(equation.charAt(i)) <= getOperatorPrecedence(operators.peek())){
                        operand2 = operands.pop();
                        operand1 = operands.pop();
                        operands.push(operators.pop() + " " + operand1 + " " + operand2);
                    }
                    operators.push(equation.charAt(i));
                }else if(i == equation.length() - 1){
                    while(operators.size() > 1){
                        operand2 = operands.pop();
                        operand1 = operands.pop();
                        operands.push(operators.pop() + " " + operand1 + " " + operand2);
                    }
                }
            }
            prefix = operands.pop();
        }else{
            prefix = "N/A";
        }

        return prefix;
    }

    /**
     * Calculates postfix representation
     * 
     * @return
     * Returns the postfix representation
     */
    public String calculatePostfix(){
        Stack<Character> s = new Stack<Character>();
        s.push('$');
        char topOperator;
        postfix = "";

        if(isValid()){
            for(int i = 0; i < equation.length(); i++){
                if(Character.isDigit(equation.charAt(i))){
                    postfix += equation.charAt(i);
                }else if(equation.charAt(i) == '(' || equation.charAt(i) == '[' || equation.charAt(i) == '{'){
                    s.push(equation.charAt(i));
                }else if(equation.charAt(i) == ')' || equation.charAt(i) == ']' || equation.charAt(i) == '}'){
                    topOperator = s.pop();
                    while(topOperator != '(' && topOperator != '[' && topOperator != '{'){
                        postfix += " " + topOperator;
                        topOperator = s.pop();
                    }
                }else if(getOperatorPrecedence(equation.charAt(i)) > 1 ){
                    topOperator = s.peek();
                    while(getOperatorPrecedence(topOperator) >= getOperatorPrecedence(equation.charAt(i))){
                        postfix += " " + s.pop();
                        topOperator = s.peek();
                    }
                    postfix += " ";
                    s.push(equation.charAt(i));
                }
            }
            topOperator = s.pop();
            while(topOperator != '$'){
                postfix += topOperator;
                topOperator = s.pop();
            }
        }else{
            postfix = "N/A";
        }

        return postfix;
    }

    /**
     * Checks if the equation is valid
     * 
     * @return
     * True if equation is balanced, doesnt divide by zero, and does not include double operators
     */
    public boolean isValid(){
        boolean repeatedOperators = false, dividesByZero = false;

        for(int i = 0; i < equation.length() - 1; i++){
            if(equation.charAt(i) == ' ' || equation.charAt(i) == '(' || equation.charAt(i) == '[' || 
                equation.charAt(i) == '{' || equation.charAt(i) == ')' || equation.charAt(i) == ']' || 
                equation.charAt(i) == '}'){
                continue;
            }
            if(!Character.isDigit(equation.charAt(i)) && !Character.isDigit(equation.charAt(i + 1)) && equation.charAt(i + 1) != ' '){
                repeatedOperators = true;
            }
            if(!Character.isDigit(equation.charAt(i)) && equation.charAt(i + 1) == '0'){
                dividesByZero = true;
            }
        }

        return (!repeatedOperators && !dividesByZero && isBalanced());
    }

    /**
     * Checks if the equation is balanced
     * 
     * @return
     * True when the equation has the same number of opening and closing parentheses, brackets, or braces
     */
    public boolean isBalanced(){
        String temp = "";

        for(int i = 0; i < equation.length(); i++){
            
            if(equation.charAt(i) == '(' || equation.charAt(i) == '[' || equation.charAt(i) == '{'){
                temp += equation.charAt(i);
            }
            if(equation.charAt(i) == ']' && temp.charAt(temp.length() - 1) == '[' 
                || equation.charAt(i) == '}' && temp.charAt(temp.length() - 1) == '{'
                || equation.charAt(i) == ')' && temp.charAt(temp.length() - 1) == '('){
                    temp = temp.substring(0, temp.length() - 1);
            }
        }

        return balanced = (temp.equals(""));
    }
    
    /**
     * Calculates binary representation of a number
     * 
     * @param number
     * Integer in base-10
     * 
     * @return
     * Returns binary representation of the number 
     */
    public String decToBin(int number){
        return baseChange(number, 2);
    }

    /**
     * Calculates hexadecimal representation of a number
     * 
     * @param number
     * Integer in base-10
     * 
     * @return
     * Returns hexadecimal representation of the number
     */
    public String decToHex(int number){
        return baseChange(number, 16);
    }

    /**
     * Changes the base of a given base-10 integer
     * 
     * @param number
     * A base-10 integer
     * 
     * @param base
     * The base to change to
     * 
     * @return
     * Returns the number in the given base
     */
    public String baseChange(int number, int base){
        String[] digits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String result = "";
        if(number == 0){
            result = "0";
        }
        while(number > 0){
            result = digits[(number % base)] + result;
            number /= base;
        }
        return result;
    }

    /**
	 * @return
	 * Returns a string representation of the object
	 */
    public String toString(){
        return String.format("%-5s %-30s %-30s %-30s %-20s %-15s %s", "", equation, prefix, postfix, 
        String.format("%.3f", answer), binary, hex);
    }
}