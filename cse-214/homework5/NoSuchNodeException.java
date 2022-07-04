/**
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class NoSuchNodeException extends Exception{

    /**
     * Creates an instance of this Object
     */
    public NoSuchNodeException(){
        super("That option does not exist");
    }
}