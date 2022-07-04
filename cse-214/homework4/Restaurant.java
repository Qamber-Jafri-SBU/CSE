/**
 * The Restaurant class is a Queue of Customers
 *  implemented as a Linked List
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class Restaurant{

    private static int numberOfRestaurants = 0;
    private int restaurantNumber;
    private Customer tail;
    private Customer head;
    private Customer cursor;
    private int size;

    /**
     * Creates an instance of this Object
     */
    public Restaurant(){
        ++numberOfRestaurants;
        restaurantNumber = numberOfRestaurants;
        tail = null;
        head = null;
        cursor = null;
        size = 0;
    }

    /**
     * Enqueues a customer into the end of the Queue
     * 
     * @param c the Customer to enqueue into the Queue
     */
    public void enqueue(Customer c){
        Customer nodePtr = head;

        if(nodePtr == null){
            head = c;
            tail = c;
            cursor = c;
        }
        else if(head.getNext() == null && tail.getPrev() == null){
            if(head.getTimeLeftToServe() <= c.getTimeLeftToServe()){
                tail = c;
                head.setNext(tail);
                tail.setPrev(head);
                cursor = c;
            }else{
                head = c;
                head.setNext(tail);
                tail.setPrev(head);

                cursor = c;
            }
        }
        else{
            while(nodePtr.hasNext() &&  nodePtr.getTimeLeftToServe() <= c.getTimeLeftToServe()){
                nodePtr = nodePtr.getNext();
            }
            if(!nodePtr.hasNext()){
                if(nodePtr.getTimeLeftToServe() <= c.getTimeLeftToServe()){
                    nodePtr.setNext(c);
                    c.setPrev(nodePtr);
                    tail = c;
                    cursor = c;
                }else{
                    c.setNext(nodePtr);
                    c.setPrev(nodePtr.getPrev());
                    nodePtr.getPrev().setNext(c);
                    nodePtr.setPrev(c);
                    cursor = c;
                }
            }else if(nodePtr == head){
                if(nodePtr.getTimeLeftToServe() <= c.getTimeLeftToServe()){
                    c.setNext(head.getNext());
                    head.getNext().setPrev(c);
                    c.setPrev(head);
                    head.setNext(c);
                    cursor = c;
                }else{
                c.setNext(head);
                head.setPrev(c);
                head = c;
                cursor = c;   
                }
            }else if(nodePtr.getTimeLeftToServe() <= c.getTimeLeftToServe()){
                nodePtr.getNext().setPrev(c);
                c.setPrev(nodePtr);
                c.setNext(nodePtr.getNext());
                nodePtr.setNext(c);
                cursor = c;
            }else{
                c.setNext(nodePtr);
                c.setPrev(nodePtr.getPrev());
                nodePtr.getPrev().setNext(c);
                nodePtr.setPrev(c);
                cursor = c;
            }
        }
        size++;
    }

    /**
     * Dequeues an element from the front of the Queue
     * 
     * @return the Customer at the front of the Queue
     */
    public Customer dequeue(){
        Customer nodePtr = head;
        head = head.getNext();
        size--;
            return nodePtr;
    }

    /**
     * Gets the Customer at a specific index in the Queue
     * @param index the position of the Customer to get
     * 
     * @return the Customer at the specific index
     */
    public Customer get(int index){
        Customer nodePtr = head;
        while((index > 0) && nodePtr.hasNext()){
            nodePtr = nodePtr.getNext();
            index--;
        }
        return nodePtr;
    }

    /**
     * Returns the number of Customers in the Restaurant
     * @return the size of the Restaurant
     */
    public int size(){
        return size;
    }

    /**
     * @return true if the Restaurant has no Customer and false otherwise
     */
    public boolean isEmpty(){
        if(size() > 0){
            return false;
        }
        return true;
    }

    /**
     * @return the front Customer in the Restaurant
     */
    public Customer peek(){
        return head;
    }

    /**
     * A String representation of this Object
     */
    public String toString(){
        String toString = "R" + restaurantNumber + ": {";
        if(size() == 1){
            return (toString += get(0).toString() + "}");
        }
        for(int i = 0; i < size(); i++){
            toString += get(i).toString() + ", ";
        }
        return (toString + "}");
    }
}