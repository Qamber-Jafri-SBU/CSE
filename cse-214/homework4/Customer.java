/**
 * The Customer class allows for the creation
 *  of Customer object that are to be used in the
 *  Restaurant class which acts as a Queue
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class Customer{

    private static int totalCustomers = 0;

    private int orderNumber;
    private String food, foodAbbreviation;
    private int priceOfFood;
    private int timeArrived, timeToCook, timeToServe, timeLeftToServe;
    private Customer prev;
    private Customer next;

    /**
     * Creates an instance of the object
     */
    public Customer(){
        ++totalCustomers;
        orderNumber = totalCustomers;
        prev = null;
        next = null;
    }

    /**
     * Creates an instance of the object
     * 
     * @param food
     *  The name of the food
     * 
     * @param priceOfFood
     *  The price of the food
     * 
     * @param timeArrived
     *  The time the customer arrives at
     * 
     * @param timeToCook
     *  The time to cook the food
     * 
     * @param foodAbbreviation
     *  The abbreviation of the food
     */
    public Customer(String food, int priceOfFood, int timeArrived, int timeToCook, String foodAbbreviation){
        this.food = food;
        this.priceOfFood = priceOfFood;
        this.timeArrived = timeArrived;
        this.timeToCook = timeToCook;
        this.foodAbbreviation = foodAbbreviation;
        timeLeftToServe = timeToServe;
        orderNumber = totalCustomers;
        prev = null;
        next = null;
    }

    /**
     * @return the orderNumber
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * @return the food
     */
    public String getFood() {
        return food;
    }

    /**
     * @return the priceOfFood
     */
    public int getPriceOfFood() {
        return priceOfFood;
    }

    /**
     * @return the timeArrived
     */
    public int getTimeArrived() {
        return timeArrived;
    }

    /**
     * @return the timeToCook
     */
    public int getTimeToCook() {
        return timeToCook;
    }

    /**
     * @param timeToCook the timeToCook to set
     */
    public void setTimeToCook(int timeToCook) {
        this.timeToCook = timeToCook;
    }

    /**
     * @return the timeToServe
     */
    public int getTimeToServe() {
        return timeToServe;
    }

    /**
     * @param timeToServe the timeToServe to set
     */
    public void setTimeToServe(int timeToServe) {
        this.timeToServe = timeToServe;
    }

    /**
     * @return the timeLeftToServe
     */
    public int getTimeLeftToServe() {
        return timeLeftToServe;
    }

    /**
     * @param timeLeftToServe the timeLeftToServe to set
     */
    public void setTimeLeftToServe(int timeLeftToServe) {
        this.timeLeftToServe = timeLeftToServe;
    }

    public void decrementTimeLeftToServe(){
        timeLeftToServe -= 5;
    }
    /**
     * @return the totalCustomers
     */
    public static int getTotalCustomers() {
        return totalCustomers;
    }

    /**
     * @return the next Customer in queue
     */
    public Customer getNext(){
        return next;
    }

    /**
     * @param c the next customer to set
     */
    public void setNext(Customer c){
        next = c;
    }

    /**
     * @return the previous customer in queue
     */
    public Customer getPrev(){
        return prev;
    }

    /**
     * @param c the previous customer to set
     */
    public void setPrev(Customer c){
        prev = c;
    }
    
    /**
     * @return true if there is a next customer in line false otherwise
     */
    public boolean hasNext(){
        if(getNext() != null){
            return true;
        }
        return false;
    }

    /**
     * A String representation of this Object
     */
    public String toString(){
        return ("[#" + orderNumber + ", " + foodAbbreviation + ", " + timeLeftToServe + " min.]");
    }
}