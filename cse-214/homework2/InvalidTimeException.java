/**
 * An Exception class intended to be used with the Track class.
 *  Thrown when the time of the Train added to the Track is invalid,
 *  i.e. not of the form HH:MM
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class InvalidTimeException extends Exception{

    /**
     * Creates an instance of InvalidTimeException
     */
    public InvalidTimeException(){
        super("Train not added: Invalid arrival time.");
    }
}