/**
 * Exception for use with GeneralLedger. Thrown when the Transaction is not legal 
 *  i.e. the amount is nonpositive or the date is not of the format YYYY/MM/DD
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */
public class InvalidTransactionException extends Exception{

    public InvalidTransactionException(){
        super("Invalid transaction!!\nPlease ensure the date is of the form YYYY/MM/DD and that the transaction amount is not 0!");
    }
}