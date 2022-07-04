/**
 * An exception thrown when a given position goes out of bounds
 *  or is less than zero. Used with HistoryStack. 
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class InvalidPositionException extends Exception{

    /**
     * Creates an instance of this object
     * 
     * @param position
     * The invalid position
     */
    public InvalidPositionException(int position){
        super(position + " is not a valid position!");
    }
}