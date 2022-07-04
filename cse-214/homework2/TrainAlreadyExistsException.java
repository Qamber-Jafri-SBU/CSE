/**
 * An Exception class intended to be used with the Track class.
 *  Thrown when a new Track has the same track number as an
 *  existing Train in the Track.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class TrainAlreadyExistsException extends Exception{

    /**
     * Creates an instance of TrainAlreadyExistsException
     * 
     * @param trainNumber
     *  The train number of the Train attempting to be added
     */
    public TrainAlreadyExistsException(int trainNumber){
        super("Train No. " + trainNumber + " already exists!");
    }
}