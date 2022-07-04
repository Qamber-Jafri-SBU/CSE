/**
 * An Exception class intended to be used with the Track class.
 *  Thrown when no Train is selected on the Track.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class NoTrainSelectedException extends Exception{

    /**
     * Creates an instance of NoTrainSelectedException
     */
    public NoTrainSelectedException(){
        super("No train selected!");
    }
}