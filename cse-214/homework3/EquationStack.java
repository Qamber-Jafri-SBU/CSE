/**
 * A Stack of String Objects used with the Calculator
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.Stack;
public class EquationStack extends Stack<String>{

    private int size; //the size of the Stack

    /**
     * Creates an instance of the Object
     */
    public EquationStack(){
        size = 0;
    }

    /**
     * Pushes a String to the Stack
     * 
     * @param String
     * The String to be pushed
     * 
     * @return
     * Returns the pushed String
     */
    public String push(String s){
        size++;
        return super.push(s);
    }

    /**
     * Pops a String from the Stack
     * 
     * @return
     * Returns the popped String
     */
    public String pop(){
        String pop = super.pop();
        size--;
        return pop;
    }

    /**
     * Checks whether the Stack is empty
     * 
     * @return
     * True when the Stack is empty, false otherwise
     */
    public boolean isEmpty(){
        return super.isEmpty();
    }

    /**
     * @return
     * Returns the size of the Stack
     */
    public int size(){
        return size;
    }
}