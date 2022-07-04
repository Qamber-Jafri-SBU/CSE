/**
 * The Track class creates Tracks that hold Train objects, and 
 *  that also have a utilization rate, track number, and size.
 *  The Tracks are implemented to be linked lists of Train nodes,
 *  and to be nodes themselves to be used in other lists.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class Track{

    private Train head, tail, cursor; //the initial, final, and selected Trains on the Track
    private Track next, prev; //The next and previous Tracks in the linked list
    private double utilizationRate; //The utilization rate of the track
    private int trackNumber; //The track number of the track
    private int size; //The number of Trains on the Track
    private static final String HEADER = String.format("%-11s %-15s %-25s %-15s %-15s\n"
     + "-------------------------------------------------------------------------------------\n", "Selected", "Train Number",
        "Train Destination", "Arrival Time", "Departure Time"); //The header for the table

    /**
     * Creates an instance of Track
     */
    public Track(){
        head = null;
        tail = null;
        cursor = null;
        next = null;
        prev = null;
        size = 0;
    }

    /**
     * Creates an instance of Track with a specified track number
     * 
     * @param trackNumber
     *  The track number for the track
     */
    public Track(int trackNumber){
        this.trackNumber = trackNumber;
        utilizationRate = 0;
        head = null;
        tail = null;
        cursor = null;
        next = null;
        prev = null;
        size = 0;
    }

    /**
     * Returns the utilization rate (The percentage that the track is being used throughout the day)
     * 
     * @return
     *  Returns the utilization rate
     */
    public double getUtilizationRate(){
        Train nodePtr = head;
        while(nodePtr != null){
            utilizationRate = 100*(double)nodePtr.getTransferTime()/1440;
            nodePtr = nodePtr.getNext();
        }
        return Double.parseDouble(String.format("%.2f", utilizationRate));
    }

    /**
     * @return
     *  Returns the track number
     */
    public int getTrackNumber(){
        return trackNumber;
    }

    /**
     * @return
     *  Returns the currently selected Train
     */
    public Train getCursor(){
        return cursor;
    }

    /**
     * Sets the currently selected Train
     * 
     * @param cursor
     *  The new Train to select 
     */
    public void setCursor(Train cursor){
        this.cursor = cursor;
    }

    /**
     * @return
     *  Returns the next Track
     */    
    public Track getNext(){
        return next;
    }

    /**
     * Sets the next Track to a different Track
     * 
     * @param prev
     *  The Track to set the next Track to
     */
    public void setNext(Track next){
        this.next = next;
    }

    /**
     * @return
     *  Returns the previous Track
     */
    public Track getPrev(){
        return prev;
    }

    /**
     * Sets the previous Track to a different Track
     * 
     * @param prev
     *  The Track to set the previous Track to
     */
    public void setPrev(Track prev){
        this.prev = prev;
    }

    /**
     * @return
     *  Returns the number of trains on the track
     */
    public int getSize(){
        return size;
    }

    /**
     * Adds a Train to the Track
     * 
     * @param newTrain
     *  The Train to be added to the Track
     * 
     * @throws InvalidTimeException
     *  when the arrival time of the Train is not valid
     * 
     * @throws TrainAlreadyExistsException
     *  when a Train with the same number is attempted to be added to the Track
     */
    public void addTrain(Train newTrain) throws InvalidTimeException, TrainAlreadyExistsException{
        Train nodePtr = head;

        if(!timeIsValid(newTrain.getArrivalTime())){
            throw new InvalidTimeException();
        }
        if(cursor == null){
            head = newTrain;
            tail = head;
            cursor = head;
            size++;
            return;
        }else if(nodePtr.getPrev() == null && nodePtr.getNext() == null){ 
            if(nodePtr.getTrainNumber() == newTrain.getTrainNumber()){
                throw new TrainAlreadyExistsException(nodePtr.getTrainNumber());
            }
            if(newTrain.getArrivalTime() > nodePtr.getDepartureTime()){
                nodePtr.setNext(newTrain);
                nodePtr.getNext().setPrev(nodePtr);
                tail = nodePtr.getNext();
                cursor = tail;
                size++;

            }
            else if(newTrain.getDepartureTime() < nodePtr.getArrivalTime()){
                nodePtr.setPrev(newTrain);
                nodePtr.getPrev().setNext(nodePtr);
                head = nodePtr.getPrev();
                cursor = head;
                size++;
            }   
            else{
                System.out.println("Train not added: There is a Train already scheduled on Track " + getTrackNumber() +  " at that time!");
            }
            return;
        }
        else{
        while(nodePtr != null && newTrain.getArrivalTime() > nodePtr.getDepartureTime()){
            if(nodePtr.getArrivalTime() <= newTrain.getArrivalTime() && newTrain.getArrivalTime() <= nodePtr.getDepartureTime() ){
                System.out.println("Train not added: There is a Train already scheduled on Track " + getTrackNumber() +  " at that time!");
                return;
            }
            if(nodePtr.getTrainNumber() == newTrain.getTrainNumber()){
                throw new TrainAlreadyExistsException(nodePtr.getTrainNumber());
            }
            if(nodePtr.getNext() == null){
                break;
            }
            nodePtr = nodePtr.getNext();
        }
        if(nodePtr.getPrev() == null){
            nodePtr.setPrev(newTrain);
            nodePtr.getPrev().setNext(nodePtr);
            nodePtr = nodePtr.getPrev();

            head = nodePtr;
            cursor = head;
        }
        else if(nodePtr.getNext() == null){
            nodePtr.setNext(newTrain);
            nodePtr.getNext().setPrev(nodePtr);
            nodePtr = nodePtr.getNext();

            tail = nodePtr;
            cursor = tail;
        }
        else if(nodePtr.getPrev() != null){
            newTrain.setNext(nodePtr);
            newTrain.setPrev(nodePtr.getPrev());
            nodePtr.getPrev().setNext(newTrain);
            nodePtr.setPrev(newTrain);
            cursor = nodePtr;
        }
        System.out.println("Train No. " + newTrain.getTrainNumber() + " to " + newTrain.getDestination() + " added to Track " + trackNumber);
        size++;
        }
    }


    /**
     * Prints the selected Train
     */
    public void printSelectedTrain(){
        System.out.println("Selected Train: \n");
        System.out.print(cursor.toString());
    }

    /**
     * Removes the selected Train
     * 
     * @return
     *  Returns a reference to the removed Train
     */
    public Train removeSelectedTrain(){
        Train nodePtr = cursor;
        if(cursor == null){
            return null;
        }
        else if((cursor.getNext() == null) && (cursor.getPrev() == null)){
            cursor = null;
            head = cursor;
            tail = cursor;
        }
        else if(cursor.getNext() == null){
            System.out.println("at end");
            cursor.getPrev().setNext(null);
            cursor = cursor.getPrev();
            tail = cursor;
        }
        else if(cursor.getPrev() == null){
            System.out.println("at beginning");
            cursor.getNext().setPrev(null);
            cursor = cursor.getNext();
            head = cursor;
        }else{
            System.out.println("in middle");
            cursor.getPrev().setNext(cursor.getNext());
            cursor.getNext().setPrev(cursor.getPrev());
            cursor = cursor.getNext();
        }

        size--;
        return nodePtr;
    }

    /**
     * Moves the cursor to the next Train scheduled
     * 
     * @return
     *  True if the cursor was moved and false otherwise
     * 
     * @throws NoTrainSelectedException
     *  when no Train is selected
     */    
    public boolean selectNextTrain() throws NoTrainSelectedException{
        if(cursor == null){
            throw new NoTrainSelectedException();
        }
        if(cursor.getNext() != null){
            cursor = cursor.getNext();
            return true;
        }
        return false;
    }

    /**
     * Moves the cursor to the previous Train scheduled
     * 
     * @return
     *  True if the cursor was moved and false otherwise
     * 
     * @throws NoTrainSelectedException
     *  when no Train is selected
     */
    public boolean selectPrevTrain() throws NoTrainSelectedException{
        if(cursor == null){
            throw new NoTrainSelectedException();
        }
        if(cursor.getPrev() != null){
            cursor = cursor.getPrev();
            return true;
        }
        return false;
    }

	/**
	 * @return
	 * Returns a string representation of the object
	 */
    public String toString(){
        Train nodePtr = head;
        String toString = "";
        String line;

        System.out.println("Track " + getTrackNumber() + " (" + getUtilizationRate() + "% Utilization Rate): ");
        System.out.print(HEADER);
        while(nodePtr != null){
            line = nodePtr.toString() + "\n";
            if(nodePtr == cursor){
                line = "*" + line.substring(1); 
            }
            toString += line;
            nodePtr = nodePtr.getNext();
        }
        return toString;
    }

    /**
     * Checks if the time is in standard 24-hour format e.g. HH:MM
     * 
     * @param time
     *  The time checked for legality
     * 
     * @return
     *  True when the time is of the form HH:MM and false otherwise
     */
    public boolean timeIsValid(int time){
        if((time / 10) % 10 > 5){
            return false;
        }
        if(time / 100 > 23){
            return false;
        }

        return true;
    }
}