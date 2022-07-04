/**
 * An Exception used with HistoryStack, thrown when there is no equation to redo in the Stack
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class NoEquationToRedoException extends Exception{

    /**
     * Creates an instance of this Object
     */
    public NoEquationToRedoException(){
        super("No equation to redo!");
    }
}