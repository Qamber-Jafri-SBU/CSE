/**
 * Exception for use with GeneralLedger. Thrown when a Transaction is attempted to be added to a full GeneralLedger
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */
public class FullGeneralLedgerException extends Exception{
    
    public FullGeneralLedgerException(){
        super("Ledger is full!!");
    }
}