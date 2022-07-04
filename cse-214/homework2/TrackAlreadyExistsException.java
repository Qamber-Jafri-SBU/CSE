/**
 * An Exception class intended to be used with the Station class.
 *  Thrown when a new Track has the same track number as an
 *  existing Track in the Station.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class TrackAlreadyExistsException extends Exception{

    /**
     * Creates an instance of TrackAlreadyExistsException
     * 
     * @param trackNumber
     *  The track number of the Track attempting to be added
     */
    public TrackAlreadyExistsException(int trackNumber){
        super("Track not added: Track " + trackNumber + " already exists.");
    }
}