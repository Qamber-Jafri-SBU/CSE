/**
 * The DiningSimulator class is a simulator 
 *  of multiple Restaurants and prints results based on
 *  user input
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.ArrayList;
import java.util.Scanner;
public class DiningSimulator{

    private ArrayList<Restaurant> restaurants;
    private final int CHEF_COOKING_SPEED;
    private int chefs;
    private int duration;
    private double arrivalProb;
    private double averageTime;
    private int maxCustomerSize;
    private int numRestaurants;
    private int customersLost;
    private int totalServiceTime;
    private int customersServed;
    private int profit;
    
    public static void main(String[] args){
        String response = "";
        boolean isRunning = true;
        DiningSimulator diningSimulator;
        int numRestaurants, maxCustomerSize, chefs, duration;
        double arrivalProb;
        Scanner stdin = new Scanner(System.in);

        do{
            System.out.println("Starting simulator...\n");
            System.out.print("Enter the number of restaurants: ");
            numRestaurants = stdin.nextInt();
            System.out.print("Enter the maximum number of customers a restaurant can serve: ");
            maxCustomerSize = stdin.nextInt();
            System.out.print("Enter the arrival probability of a customer: ");
            arrivalProb = stdin.nextDouble();
            System.out.print("Enter the number of chefs: ");
            chefs = stdin.nextInt();
            System.out.print("Enter the number of simulation units: ");
            duration = stdin.nextInt();
            diningSimulator = new DiningSimulator(numRestaurants, maxCustomerSize, arrivalProb, chefs, duration);
            diningSimulator.simulate();
            System.out.println("Simulation ending...");
            diningSimulator.results();
            System.out.print("Do you want to try another simulation? (y/n): ");
            response = stdin.nextLine();
            if(response.equals("n")){
                isRunning = false;
            }else if(!response.equals("y")){
                while(!response.equals("y")){
                    System.out.print("Please enter either y/n");
                    response = stdin.nextLine();
                }
                if(response.equals("n")){
                    isRunning = false;
                }
            }

        }while(isRunning);
        stdin.close();
    }

    /**
     * Creates an instance of this class
     */
    public DiningSimulator(){
        customersLost = 0;
        totalServiceTime = 0;
        customersServed = 0;
        profit = 0;
        CHEF_COOKING_SPEED = 0;
        averageTime = 0;
    }

    /**
     * Creates an instance of this class
     * 
     * @param numRestaurants 
     *  The number of Restaurants in the simulation
     * 
     * @param maxCustomerSize
     *  The max number of Customers in each Restaurant
     * 
     * @param arrivalProb
     *  The arrival probabilty of a Customer entering the Restaurants
     * 
     * @param chefs
     *  The number of chefs in each Restaurant
     * 
     * @param duration
     *  The number of simulation units to run (each unit is 5 minutes)
     */
    public DiningSimulator(int numRestaurants, int maxCustomerSize, double arrivalProb, int chefs, int duration){
        this.numRestaurants = numRestaurants;
        this.maxCustomerSize = maxCustomerSize;
        this.arrivalProb = arrivalProb;
        this.chefs = chefs;
        this.duration = duration;
        averageTime = 0;
        customersLost = 0;
        totalServiceTime = 0;
        customersServed = 0;
        profit = 0;
        restaurants = new ArrayList<Restaurant>(numRestaurants);
        if(chefs < 1 || numRestaurants < 1 || maxCustomerSize < 1 || arrivalProb < 0 || arrivalProb > 1 || duration < 1){
            System.out.println("NO SIMULATION!!!");
            CHEF_COOKING_SPEED = 0;
            return;
        }
        if(chefs == 3){
            CHEF_COOKING_SPEED = 0;
        }else if(chefs <= 2){
            CHEF_COOKING_SPEED = 10/chefs;
        }else if(chefs == 4 ){
            CHEF_COOKING_SPEED = -5;
        }else{
            CHEF_COOKING_SPEED = -10;
        }

        
        for(int i = 0; i < numRestaurants; i++){
            restaurants.add(new Restaurant());
        }

    }

    /**
     * Runs the simulation
     * 
     * @param arrivalProb
     *  The arrival probabilty of the Customers entering the Restaurant
     * 
     * @param duration
     *  The number of simulation units in the simulation
     * 
     * @return
     *  The average wait time for Customers
     */
    public double simulate(double arrivalProb, int duration){
        int currentTime = 1;

        while(currentTime <= duration){
            System.out.println("Time: " + currentTime);
            customerDeparture();
            customerArrival(currentTime);
            for(int i = 0; i < numRestaurants; i++){
                System.out.println(restaurants.get(i));
            }
            decrementTimeLeftToServe(currentTime);
            currentTime++;
            System.out.println();
        }


        return (averageTime = totalServiceTime/customersServed);
    }

    /**
     * Runs the simulation
     * 
     * @return
     *  The average wait time for Customers
     */
    public double simulate(){
        int currentTime = 1;

        while(currentTime <= duration){
            System.out.println("Time: " + currentTime);
            customerDeparture();
            customerArrival(currentTime);
            for(int i = 0; i < numRestaurants; i++){
                System.out.println(restaurants.get(i));
            }
            decrementTimeLeftToServe(currentTime);
            currentTime++;
            System.out.println();
        }
        

        return (averageTime = totalServiceTime/customersServed);
    }

    /**
     * Models the arrival of Customers
     * 
     * @param currentTime
     *  The current time in the simulation
     */
    private void customerArrival(int currentTime){
        ArrayList<Customer> newCustomers = new ArrayList<Customer>(9);
        ArrayList<Customer> lostCustomers = new ArrayList<Customer>(9);
        boolean customerLeft = false;
        Customer newCustomer;
        for(int i = 0; i < numRestaurants; i++){
            for(int j = 0; j < 3; j++){
                if(Math.random() < arrivalProb){
                    newCustomer = customerOrdering(currentTime);
                    newCustomer.setTimeToCook(newCustomer.getTimeToCook() + CHEF_COOKING_SPEED);
                    newCustomer.setTimeToServe(newCustomer.getTimeToCook() + 15);
                    newCustomer.setTimeLeftToServe(newCustomer.getTimeToServe());
                    System.out.println("Customer #" + newCustomer.getOrderNumber() + " has entered Restaurant " + (i + 1));
                    if(restaurants.get(i).size() >= maxCustomerSize){
                        lostCustomers.add(newCustomer);
                        customerLeft = true;
                        continue;
                    }
                    
                    newCustomers.add(newCustomer);
                    restaurants.get(i).enqueue(newCustomer);
                }
            }
        }
        customerSeating(newCustomers);
        if(customerLeft)
            leavingCustomers(lostCustomers);
    }

    /**
     * Models the ordering done by a Customer
     * 
     * @param currentTime
     *  The current time in the simulation
     * @return
     *  The Customer after they've ordered 
     */
    private Customer customerOrdering(int currentTime){
        Customer customer = new Customer();
        int arrivalTime = currentTime*5;
        switch(randInt(1, 5)){
            case 1: //Cheeseburger
                customer = new Customer("Cheeseburger", 15, arrivalTime, 25, "C");
                break;
            case 2: //Steak
                customer = new Customer("Steak", 25, arrivalTime, 30, "S");
                break;
            case 3: //Grilled Cheese
                customer = new Customer("Grilled Cheese", 10, arrivalTime, 15, "GC");
                break;
            case 4: //Chicken Tenders
                customer = new Customer("Chicken Tenders", 10, arrivalTime, 25, "CT");
                break;
            case 5: //Chicken Wings
                customer = new Customer("Chicken Wings", 20, arrivalTime, 30, "CW");
                break;
        }
        return customer;
    }

    /**
     * Models the seating done by a Customer
     * 
     * @param newCustomers
     *  An ArrayList of Customers to be seated
     */
    private void customerSeating(ArrayList<Customer> newCustomers){
        for(int i = 0; i < newCustomers.size(); i++){
            System.out.println("Customer #" + newCustomers.get(i).getOrderNumber() +
                 " has been seated with order \"" + newCustomers.get(i).getFood() + "\".");
        }    
    }

    /**
     * Models Customers who leave without ordering
     * 
     * @param leavingCustomers
     *  An ArrayList of Customers intending to leave
     */
    private void leavingCustomers(ArrayList<Customer> leavingCustomers){
        for(int i = 0; i < leavingCustomers.size(); i++){
            System.out.println("Customer #" + (leavingCustomers.get(i).getOrderNumber() +
                 " cannot be seated! They have left the restaurant."));
            customersLost++;
        }
    }

    /**
     * Decrements the serve time of each Customer with respect to the simulation unit (5 minutes)
     */
    private void decrementTimeLeftToServe(int currentTime){
        for(int i = 0; i < numRestaurants; i++){
            for(int j = 0; j < restaurants.get(i).size(); j++){
                restaurants.get(i).get(j).decrementTimeLeftToServe();
             }
        }
    }

    /**
     * Models Customer departure after they've ordered
     */
    private void customerDeparture(){
        Customer departingCustomer;
        for(int i = 0; i < numRestaurants; i++){
            while(!(restaurants.get(i).isEmpty()) && restaurants.get(i).peek().getTimeLeftToServe() <= 0){
                departingCustomer = restaurants.get(i).dequeue();
                System.out.println("Customer #" + departingCustomer.getOrderNumber() + " has enjoyed their food! $" + departingCustomer.getPriceOfFood() +  " profit.");
                profit += departingCustomer.getPriceOfFood();
                totalServiceTime += departingCustomer.getTimeToServe();
                customersServed++;
            }
        }
    }

    /**
     * Displays the results of the simulation
     */
    private void results(){
        System.out.println("Total customer time: " + totalServiceTime + " minutes");
        System.out.println("Total customers served: " + customersServed);
        System.out.println("Average customer time lapse: " + averageTime);
        System.out.println("Total Profit: " + profit);
        System.out.println("Customers that left: " + customersLost);
    }

    /**
     * Generates a random integer between minVal and maxVal inclusive
     * 
     * @param minVal the minimum value to be generated
     * 
     * @param maxVal the maximum value to be generated
     * 
     * @return
     *  A random integer between minVal and maxVal 
     */
    private int randInt(int minVal, int maxVal){
        return (int)(Math.random()*maxVal + minVal);
    }
}