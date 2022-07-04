/**
 * The Train class creates Trains that have a train number,
 *  a destination, arrival time, and a transfer time. The 
 *  Trains are implemented to be nodes in a linked list
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class Train{

    private Train next, prev; //The next and previous trains (chronologically) with respect to the current train
    private int trainNumber; //The train number
    private String destination; //The destination of the train
    private int arrivalTime, transferTime; //The arrival and transfer times of the train
    
    /**
     * Creates an instance of Train
     */
    public Train(){
        next = null;
        prev = null;
    }

    /**
     * Creates an instance of Train with specifications
     * 
     * @param trainNumber
     *  The number of the train
     * @param destination
     *  The destination of the train
     * @param arrivalTime
     *  The arrival time of the train
     * @param transferTime
     *  The transfer time of the train
     */
    public Train(int trainNumber, String destination, int arrivalTime, int transferTime){
        this.trainNumber = trainNumber;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
        this.transferTime = transferTime;
        next = null;
        prev = null;
    }
    
    /**
     * @return
     *  Returns the train number
     */
    public int getTrainNumber(){
        return trainNumber;
    }

    /**
     * @return
     *  Returns the destination of the train
     */
    public String getDestination(){
        return destination;
    }

    /**
     * @return
     *  Returns the arrival time of the train
     */
    public int getArrivalTime(){
        return arrivalTime;
    }

    /**
     * @return
     *  Returns the transfer time of the train
     */
    public int getTransferTime(){
        return transferTime;
    }

    /**
     * Returns departure time which is the sum of the arrival time and transfer time
     * 
     * @return
     *  Returns the departure time of the train
     */
    public int getDepartureTime(){
        int departureTime = 0;

        if(transferTime + (arrivalTime % 100) >= 60){
            departureTime = (arrivalTime/100)*100 + (((arrivalTime % 100) + transferTime) % 60) + (transferTime / 60) + 100;
        }else if(transferTime < 60){
            departureTime = arrivalTime + transferTime;
        }else if(transferTime == 60){
            departureTime = arrivalTime + 100;
        }else{
            System.out.println("4");
            departureTime = arrivalTime + (transferTime / 60)*100 + (transferTime % 60);
        }
        return departureTime;
    }

    /**
     * @return
     *  Returns the next train scheduled
     */
    public Train getNext(){
        return next;
    }

    /**
     * Sets the train scheduled after the current one
     * 
     * @param next
     * The train that should be scheduled after the current one
     */
    public void setNext(Train next){
        this.next = next;
    }

    /**
     * @return
     *  Returns the previous train scheduled
     */
    public Train getPrev(){
        return prev;
    }

    /**
     * Sets the train scheduled before the current one
     * 
     * @param prev
     *  The train that should be scheduled before the current one
     */
    public void setPrev(Train prev){
        this.prev = prev;
    }

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 * The object to be compared
	 * 
	 * @return
	 * True if the trains have the same train number and false otherwise
	 */
    public boolean equals(Object obj){
        if(obj instanceof Train){
            if(trainNumber == ((Train)obj).getTrainNumber()){
                return true;
            }
        }
        return false;
    }

	/**
	 * @return
	 * Returns a string representation of the object
	 */
    public String toString(){
        return String.format("%-11s %-15d %-25s %-15s %04d", " ", trainNumber, 
            destination, String.format("%04d",arrivalTime), getDepartureTime()); 
    }
    
}