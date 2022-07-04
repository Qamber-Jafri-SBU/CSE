/**
 * A Stack that contains Equation objects; It allows for keeping track of Equation objects,
 *   and is used in the Calculator
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */

import java.util.EmptyStackException;
import java.util.Stack;

public class HistoryStack extends Stack<Equation>{

    private int size; //size of the Stack
    private Equation undoneEquation; //the equation to be undone

    /**
     * Creates an instance of the object
     */
    public HistoryStack(){
        size = 0;
        undoneEquation = null;
    }

    /**
     * Pushes an Equation to the Stack
     * 
     * @return
     * Returns the pushed Equation
     */
    public Equation push(Equation newEquation){
        size++;
        return super.push(newEquation);
    }

    /**
     * Pops an Equation from the Stack
     * 
     * @return
     * Returns the popped Equation
     */
    public Equation pop(){
        Equation pop = super.pop();
        size--;
        return pop;
    }

    /**
     * Undoes the most recent Equation from the Stack
     * 
     * @throws EmptyStackException
     * when there is nothing to undo
     */
    public void undo() throws EmptyStackException{
        if(size == 0){
            throw new EmptyStackException();
        }
        undoneEquation = pop();
    }

    /**
     * Redoes the undone Equation to the Stack
     * 
     * @throws NoEquationToRedoException
     * when there is nothing to redo
     */
    public void redo() throws NoEquationToRedoException{
        if(undoneEquation == null){
            throw new NoEquationToRedoException();
        }
        push(undoneEquation);
    }

    /**
     * @return
     * Returns size of the Stack
     */
    public int size(){
        return size;
    }

    /**
     * Gets an Equation from the Stack at a given position
     * 
     * @param position
     * The position of an Equation in the Stack
     * 
     * @return
     * Returns the Equation from the given position
     * 
     * @throws InvalidPositionException
     * when position is greater than size or nonpositive
     */
    public Equation getEquation(int position) throws InvalidPositionException{
        Stack<Equation> s = new Stack<Equation>();
        Equation eq;

        if(position > size || position < 1){
            throw new InvalidPositionException(position);
        }
        for(int i = 0; i < position; i++){
            s.push(pop());
        }
        eq = s.peek();
        while(!s.isEmpty()){
            push(s.pop());
        }
        return eq;
    }

    /**
     * @return
     * Returns a String representation of the Object
     */
    public String toString(){
        String toString = "";
        Stack<Equation> s = new Stack<Equation>();
        int k = 1;
        for(int i = 0; i <= size; i++){
            s.push(pop());
        }
        while(!s.isEmpty()){
            push(s.peek());
            toString += k++ + s.pop().toString().substring(1) + "\n";
        }

        return toString;
    }
}