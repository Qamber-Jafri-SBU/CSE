/**
 * Exception for use with GeneralLedger. Thrown when the position given is not in the GeneralLedger
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */
public class InvalidLedgerPositionException extends Exception{

    public InvalidLedgerPositionException(){
        super("Invalid Ledger Position!");
    }
}