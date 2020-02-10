/**
 * Exception for use with GeneralLedger. Thrown when a Transactions is attempted to be added to the GeneralLedger, but it already exists
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */
public class TransactionAlreadyExistsException extends Exception{

    public TransactionAlreadyExistsException(){
        super("Transaction already exists!");
    }
}