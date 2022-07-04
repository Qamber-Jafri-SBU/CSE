/**
 * The Station class creates Stations that hold Track objects.
 *  This class is implemented to be used as a linked list of 
 *  nodes of type Track.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.Scanner;

public class Station{
    
    private Track head, tail, cursor; //The first, last, and selected Track (based on track number)
    private int size; //The number of Tracks in the Station
    private static final String HEADER = String.format("%-11s %-15s %-25s %-15s %-15s\n"
    + "-------------------------------------------------------------------------------------\n", "Selected", "Train Number",
       "Train Destination", "Arrival Time", "Departure Time"); //The header to display the table
    private static final String MENU = "|-----------------------------------------------------------------------------|\n"
        + "| Train Options                       | Track Options                         |\n"
        + "|    A. Add new Train                 |    TA. Add Track                      |\n"
        + "|    N. Select next Train             |    TR. Remove selected Track          |\n"
        + "|    V. Select previous Train         |    TS. Switch Track                   |\n"
        + "|    R. Remove selected Train         |   TPS. Print selected Track           |\n"
        + "|    P. Print selected Train          |   TPA. Print all Tracks               |\n"
        + "|-----------------------------------------------------------------------------|\n"
        + "| Station Options                                                             |\n"
        + "|   SI. Print Station Information                                             |\n"
        + "|    Q. Quit                                                                  |\n"
        + "|-----------------------------------------------------------------------------|\n"; //The menu for the program

    private static Scanner stdin = new Scanner(System.in);

    public static void main(String[] args) {
        boolean isRunning = true;
        String choice;
        Station station = new Station();
        do{
            System.out.println(MENU);
            System.out.print("Choose an operation: ");
            choice = stdin.next().toUpperCase();

            switch(choice){
                case "A":
                    addTrain(station);
                    break;
                case "N":
                    selectNextTrain(station);
                    break;
                case "V":
                    selectPrevTrain(station);
                    break;
                case "R":
                    removeSelectedTrain(station);
                    break;
                case "P":
                    printSelectedTrain(station);
                    break;
                case "TA":
                    addTrack(station);
                    break;
                case "TR":
                    station.removeSelectedTrack();
                    break;
                case "TS":
                    switchTrack(station);
                    break;
                case "TPS":
                    station.printSelectedTrack();
                    break;
                case "TPA":
                    station.printAllTracks();
                    break;
                case "SI":
                    if(station.getCursor() != null){
                        System.out.println(station.toString());
                    }else{
                        System.out.println("Station is empty! Nothing to print!");
                    }
                    break;
                case "Q":
                    System.out.println("Program terminating successfully...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Please pick one of the options!\n");
                    break;
            }
        }
        while(isRunning);

        stdin.close();
    }

    /**
     * Creates an instance of Station
     */
    public Station(){
        head = null;
        cursor = null;
        tail = null;
        size = 0;
    }

    /**
     * Adds a Track to the Station
     * 
     * @param newTrack
     *  The Track to be added to the Station
     * 
     * @throws TrackAlreadyExistsException
     *  when a Track with the same track number already exists in the Station
     */
    public void addTrack(Track newTrack) throws TrackAlreadyExistsException{
        Track nodePtr = cursor;

        if(cursor == null){
            head = newTrack;
            tail = head;
            cursor = head;
            size++;
            return;
        }else if(nodePtr.getPrev() == null && nodePtr.getNext() == null){ 
            
            if(newTrack.getTrackNumber() > nodePtr.getTrackNumber()){
                nodePtr.setNext(newTrack);
                nodePtr.getNext().setPrev(nodePtr);
                tail = nodePtr.getNext();
                cursor = tail;
                size++;

            }
            else if(newTrack.getTrackNumber() < nodePtr.getTrackNumber()){
                nodePtr.setPrev(newTrack);
                nodePtr.getPrev().setNext(nodePtr);
                head = nodePtr.getPrev();
                cursor = head;
                size++;
            }   
            else{ 
                throw new TrackAlreadyExistsException(nodePtr.getTrackNumber());
            }
            return;
        }
        
        else{
            while(nodePtr.getNext() != null && newTrack.getTrackNumber() > nodePtr.getTrackNumber()){
                nodePtr = nodePtr.getNext();
                if(nodePtr.getTrackNumber() == newTrack.getTrackNumber()){
                    System.out.println("Track not added: Track" + nodePtr.getTrackNumber() + "already exists.");  //Throw Exception
                }
            }
            if(nodePtr.getNext() == null){
                nodePtr.setNext(newTrack);
                nodePtr.getNext().setPrev(nodePtr);
                nodePtr = nodePtr.getNext();
                tail = nodePtr;
                cursor = tail;
            }else if(nodePtr.getPrev() == null){
                nodePtr.setPrev(newTrack);
                nodePtr.getPrev().setNext(nodePtr);
                head = nodePtr;
                cursor = head;
            }else{
                newTrack.setNext(nodePtr);
                newTrack.setPrev(nodePtr.getPrev());
                nodePtr.getPrev().setNext(newTrack);
                nodePtr.setPrev(newTrack);
            }
        }
        size++;
    }

    /**
     * Removes the selected Track from the Station
     * 
     * @return
     *  Returns a reference to the removed Track
     */
    public Track removeSelectedTrack(){
        Track nodePtr = cursor;

        if (cursor == null){
            return null;
        }
        else if((cursor.getNext() == null) && (cursor.getPrev() == null)){
            cursor = null;
            head = cursor;
            tail = cursor;
        }
        else if(cursor.getNext() == null){
            cursor.getPrev().setNext(null);
            cursor = cursor.getPrev();
            tail = cursor;
        }
        else if(cursor.getPrev() == null){
            cursor.getNext().setPrev(null);
            cursor = cursor.getNext();
            head = cursor;
        }
        else{
            cursor.getPrev().setNext(cursor.getNext());
            cursor.getNext().setPrev(cursor.getPrev());
            cursor = cursor.getNext();
        }

        size--;
        return nodePtr;
    }
    
    /**
     * Prints the selected Track
     */
    public void printSelectedTrack(){
        System.out.println(cursor.toString());
    }

    /**
     * Prints all Tracks
     */
    public void printAllTracks(){
        Track nodePtr = head;

        while(nodePtr != null){
            System.out.println(nodePtr.toString());
            nodePtr = nodePtr.getNext();
        }
    }

    /**
     * Selects a different Track in the Station
     * 
     * @param trackToSelect
     *  The Track to select
     * 
     * @return
     *  True when Track number is found in the Station and false otherwise
     */
    public boolean selectTrack(int trackToSelect){
        Track nodePtr = head;

        while(nodePtr.getNext() != null && nodePtr.getTrackNumber() != trackToSelect){
            nodePtr = nodePtr.getNext();
        }
        if(nodePtr.getTrackNumber() == trackToSelect){
            cursor = nodePtr;
            return true;
        }

        return false;
    }

	/**
	 * @return
	 * Returns a string representation of the object
	 */
    public String toString(){
        String toString = "Nothing to Print!";
        Track nodePtr = head;

        if(nodePtr != null){
            toString = "Station (" + size + " track(s)):\n";
        }
        while(nodePtr != null){
            toString += "\tTrack " + nodePtr.getTrackNumber() + ": " + nodePtr.getSize() +
                 " train(s) arriving (" + nodePtr.getUtilizationRate() + "% Utilization Rate)";
            nodePtr = nodePtr.getNext();
        }

        return toString;
    }

    /**
     * @return
     *  Returns the selected Track
     */
    public Track getCursor(){
        return cursor;
    }

    /**
     * Adds a Track to a given Station
     * 
     * @param station
     *  The Station to be added to
     */
    public static void addTrack(Station station){
        int trackNumber;

        System.out.print("\nEnter track number: ");
        trackNumber = stdin.nextInt();
        stdin.nextLine();

        try{
            station.addTrack(new Track(trackNumber));
            System.out.println("Track " + trackNumber + " added to the Station.");
        }catch(TrackAlreadyExistsException ex){
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Adds a Train to a selected Track of a given Station
     * 
     * @param station
     *  The Station to be added to
     */
    public static void addTrain(Station station){
        int trainNumber, arrivalTime, transferTime;
        String destination;

        if(station.getCursor() == null){
            System.out.println("No track is selected!");
            return;
        }

        System.out.print("Enter train number: ");
        trainNumber = stdin.nextInt();
        stdin.nextLine();
        System.out.print("Enter train destination: ");
        destination = stdin.nextLine();
        System.out.print("Enter train arrival time: ");
        arrivalTime = stdin.nextInt();
        System.out.print("Enter train transfer time: ");
        transferTime = stdin.nextInt();

        try{
            station.getCursor().addTrain(new Train(trainNumber, destination, arrivalTime, transferTime));
            
        }catch(InvalidTimeException ex){
            System.out.println(ex.getMessage());
        }catch(TrainAlreadyExistsException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Selects the next Train in a given Station
     * @param station
     *  The Station to be altered
     */
    public static void selectNextTrain(Station station){
        boolean selectNext;
        try{
            selectNext = station.getCursor().selectNextTrain();
            if(selectNext){
                System.out.println("Cursor has been moved to next train.");
            }else{
                System.out.println("Selected train not updated: Already at end of Track list.");
            }
        }catch(NoTrainSelectedException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Selects the previous Train in a given Station
     * @param station
     *  The Station to be altered
     */
    public static void selectPrevTrain(Station station){
        boolean selectPrev;
        try{
            selectPrev = station.getCursor().selectPrevTrain();
            if(selectPrev){
                System.out.println("Cursor has been moved to previous train.");
            }else{
                System.out.println("Selected train not updated: Already at beginning of Track list.");
            }

        }catch(NoTrainSelectedException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Prints the selected Train in the selected Track of a given Station
     * 
     * @param station
     * The Station to be altered
     */
    public static void printSelectedTrain(Station station){
        System.out.print(HEADER);
        System.out.println("*" + (station.getCursor().getCursor().toString().substring(1)));
    }

    /**
     * Selects a different Track in a given Station
     * 
     * @param station
     * The Station to be altered
     */
    public static void switchTrack(Station station){
        int trackNumber;
        
        System.out.print("Enter the track number: ");
        trackNumber = stdin.nextInt();
        if(station.selectTrack(trackNumber)){
            System.out.println("Switched to Track " + trackNumber);
        }else{
            System.out.println("Could not switch to Track " + trackNumber + ": Track " + trackNumber + " does not exist.");
        }

    }

    /**
     * Removes the selected Train in the selected Track in a given Station
     * 
     * @param station
     * The Station to be altered
     */
    public static void removeSelectedTrain(Station station){
        Train removedTrain = station.getCursor().removeSelectedTrain();
        if(removedTrain == null){
            System.out.println("No train selected!");
            return;
        }
        System.out.println("Train No. " + removedTrain.getTrainNumber() + " to " + removedTrain.getDestination() +
            " has been removed from " + station.getCursor().getTrackNumber());
    }
}