/**
 * The GeneralLedger class creates GeneralLedgers that are an array of 
 *  Transactions and allows the organization of these Transactions
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */
public class GeneralLedger{
    
    public static int MAX_TRANSACTIONS = 50; //The maximum number of transactions a GeneralLedger can contain
    public static final String HEADER = String.format("\n%-7s %-14s%-7s%-7s%-7s\n" 
    + "---------------------------------------------------------------------------------------------------\n",
    "No.", "Date", "Debit", "Credit", "Description"); //The header for the table containing the transactions of the ledger
    private Transaction[] ledger; //The array which will hold Transactions inserted into this GeneralLedger object
    private double totalDebitAmount; //The total debit amount for all transactions in this GeneralLedger object
    private double totalCreditAmount; //The total credit amount for all transactions in this GeneralLedger object
    private int numberOfTransactions; //The number of transactions in the ledger

    /**
     * Postconditions:
     *  This GeneralLedger has been initialized to an empty list of Transactions.
     * 
     * Creates an instance of GeneralLedger
     */
    public GeneralLedger(){
        ledger = new Transaction[MAX_TRANSACTIONS];
        totalDebitAmount = 0;
        totalCreditAmount = 0;
        numberOfTransactions = 0;
    }

    /**
     * Adds a Transaction to the ledger
     * 
     * Preconditions:
     *  The Transaction object has been instantiated and the number of Transaction
     *      objects in this GeneralLedger is less than MAX_TRANSACTIONS.
     * 
     * Postconditions:
     *  The new transaction is now listed in the correct order in the list. All Transactions whose date is newer than or equal 
     *      to newTransaction are moved back one position (e.g. If there are 5 transactions in a generalLedger, positioned 1-5, 
     *      and the transaction you insert has a date that's newer than the transaction in position 3 but older than the 
     *      transaction in position 4, the new transaction would be placed in position 4, the transaction that was in position 4 
     *      will be moved to position 5, and the transaction that was in position 5 will be moved to position 6.)
     * 
     * @param newTransaction
     * The Transaction to be added
     * 
     * @throws TransactionAlreadyExistsException
     * when the Transaction already exists in the ledger
     * @throws InvalidTransactionException
     * when the Transaction has a value of 0 or the date is not of legal format
     * @throws FullGeneralLedgerException
     * when the ledger is full
     */
    public void addTransaction(Transaction newTransaction) throws TransactionAlreadyExistsException, InvalidTransactionException, FullGeneralLedgerException{
        boolean doesExist = false;
        
        if(numberOfTransactions < 50){
            if(newTransaction.getAmount() != 0 && newTransaction.hasLegalDate()){
                for(int i = 0; i < numberOfTransactions; i++){
                    if(newTransaction.equals(ledger[i])){
                        doesExist = true;
                        break;
                    }
                }

                if(!doesExist){
                    if(numberOfTransactions == 0)
                        ledger[0] = newTransaction;
                    else
                        sortLedger(newTransaction);   
                    numberOfTransactions++;
                }else{
                    throw new TransactionAlreadyExistsException();
                }
            }else{
                throw new InvalidTransactionException();
            }
        }else{
            throw new FullGeneralLedgerException();
        }

        if(newTransaction.getAmount() > 0){
            totalDebitAmount += newTransaction.getAmount();
        }else{
            totalCreditAmount += Math.abs(newTransaction.getAmount());
        }

    }

    /**
     * Places a new Transaction into the ledger sorted by date
     * 
     * Preconditions:
     *  This generalLedger has been instantiated and 1 <= position <= items_currently_in_list. 
     * 
     * Postconditions:
     *  The Transaction at the desired position has been removed. All transactions that were originally greater than 
     *  or equal to position are moved backward one position (e.g. If there are 5 Transactions in the generalLedger, 
     *  positioned 1-5, and you remove the Transaction in position 4, the item that was in position 5 will be moved to position 4).
     * 
     * @param newTransaction
     * The new Transaction that needs to be sorted
     */
    private void sortLedger(Transaction newTransaction){
        int newTransactionYear = newTransaction.getYear(), newTransactionMonth = newTransaction.getMonth(),
            newTransactionDay = newTransaction.getDay(), k = 0;

        while(k < numberOfTransactions && newTransactionYear > ledger[k].getYear()){
            k++;
        }
        while(k < numberOfTransactions && newTransactionYear == ledger[k].getYear() &&
            newTransactionMonth > ledger[k].getMonth()){
            k++;
        }
        while(k < numberOfTransactions && newTransactionYear == ledger[k].getYear()
            && newTransactionMonth == ledger[k].getMonth() && newTransactionDay > ledger[k].getDay()){
            k++;
        }

        shiftLedger(k, 1);
        ledger[k] = newTransaction;
    }

    /**
     * Removes a Transaction from the ledger
     * 
     * Preconditions:
     *  The GeneralLedger has been instantiated and 1 <= position <= items_currently_in_list. 
     * 
     * @param position
     * The position of the Transaction to be removed
     * 
     * @throws InvalidLedgerPositionException
     * when the position is nonpositive or greater than the number of transactions
     */
    public void removeTransaction(int position) throws InvalidLedgerPositionException{
        if(position <= 0 || position > numberOfTransactions){
            throw new InvalidLedgerPositionException();
        }

        if(ledger[position - 1].getAmount() > 0){
            totalDebitAmount -= ledger[position - 1].getAmount();
        }else{
            totalCreditAmount -= Math.abs(ledger[position - 1].getAmount());
        }

        shiftLedger(position - 1, -1);
        numberOfTransactions--;
    }

    /**
     * Filter the ledger by date
     * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated. 
     * 
     * Postconditions:
     *  Displays a neatly formatted table of all Transactions that have taken place on the specified date. 
     * 
     * @param date
     * The date to be searched for
     */
    public static void filter(GeneralLedger generalLedger, String date){
        System.out.print(HEADER);
        for(int i = 0; i < generalLedger.getNumberOfTransactions(); i++){
            if(date.equals(generalLedger.getLedger()[i].getDate())){
                System.out.print("\n" + (i + 1) + "" + generalLedger.getLedger()[i].toString());
            }
        }
    }

    /**
     * Search for a specific transaction
     * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param date
     * The date of the Transaction to be searched for
     * @param amount
     * The amount of the Transaction to be searched for
     * @param description
     * The description of the Transaction to be searched for
     */
    public void searchForTransaction(String date, double amount, String description){
        String transactionString = "";
        boolean exists = false;
            for(int i = 0; i < numberOfTransactions; i++){
                if(ledger[i].getDate().equals(date) && ledger[i].getAmount() == amount
                    && ledger[i].getDescription().equals(description)){
                        transactionString = HEADER + "\n";
                        exists = true;
                        transactionString += (i + 1) + ledger[i].toString();
                        break;
                    }
            }
            if(!exists)
                transactionString = "No such transaction.";
            System.out.println(transactionString);
    }

    /**
	 * Creates and returns a copy of an object
     * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated. 
     * 
     * Postconditions:
     *  A copy (backup) of this GeneralLedger object.
     * 
	 * @return
	 * Creates and returns a copy of this object
	 */
    public Object clone(){
        GeneralLedger backupGeneralLedger = new GeneralLedger();
        backupGeneralLedger.setNumberOfTransactions(numberOfTransactions);
        backupGeneralLedger.setLedger(ledger);
        backupGeneralLedger.setTotalDebitAmount(totalDebitAmount);
        backupGeneralLedger.setTotalCreditAmount(totalCreditAmount);
        return backupGeneralLedger;
    }

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
	 * @param obj
	 * The object to be compared
	 * 
	 * @return
	 * True if the general ledgers are equal to each other and false otherwise
	 */
    public boolean equals(Object obj){
        if(obj instanceof GeneralLedger){
            for(int i = 0; i < numberOfTransactions; i++){
                if(ledger[i].equals(((GeneralLedger)obj).getLedger()[i])){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a Transaction exists in the ledger
     * 
     * Preconditions:
     *  This GeneralLedger and transaction have been instantiated. 
     * 
     * @param transaction
     * The Transaction to be checked for in the ledger
     * 
     * @return
     * Returns true if the Transaction exists in the ledger and false otherwise
     * 
     * @throws IllegalArgumentException
     * when the Transaction is an invalid Transaction object
     */
    public boolean exists(Transaction transaction) throws IllegalArgumentException{
        if(transaction.getAmount() == 0 || !transaction.hasLegalDate()){
            throw new IllegalArgumentException();
        }
        for(int i = 0; i < numberOfTransactions; i++){
            if(transaction.equals(ledger[i]))
                return true;
        }
        return false;
    }

    /**
     * Returns the number of transactions of the ledger
     * 
     * @return
     * Returns the number of transactions of the ledger
     */
    public int size(){
        return numberOfTransactions;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * Prints all transactions in the ledger
     */
    public void printAllTransactions(){
        if(numberOfTransactions == 0){
            System.out.println("\nNo transactions currently in the general ledger.");
        }else{
            System.out.println(toString());
        }

    }

    /**
     * Shifts the ledger in a direction from a starting position
     * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param startingPosition
     * The starting position to shift from
     * 
     * @param direction
     * If -1 the ledger moves one position towards the beginning of the ledger
     *  if 1 the ledger move one position towards the end of the ledger
     */
    public void shiftLedger(int startingPosition, int direction){
        if(direction > 0){
            for(int i = numberOfTransactions; i > startingPosition; i--){
                ledger[i] = ledger[i - 1];
            }
        }
        if(direction < 0){
            for(int i = startingPosition; i < numberOfTransactions - 1; i++){
                ledger[i] = ledger[i + 1]; 
            }
        }
        else
            return;
    }

    /**
     * Returns a String representation of this GeneralLedger object, which is a neatly formatted table of each 
     *  Transaction contained in this ledger on its own line with its position number (as shown in the sample output). 
     * 
     * @return
     * Returns a String representation of the object
     */
    public String toString(){
        String generalLedgerString = HEADER;

        for(int i = 0; i < numberOfTransactions; i++){
            generalLedgerString += (i + 1) + "" + ledger[i].toString() + "\n";
        }
        return generalLedgerString;
    }
    
    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param position
     * The position of the Transaction in the ledger
     * 
     * @param header
     * If the String should contain a header 
     * 
     * @return
     * Returns a String representation of a Transaction in the ledger
     * 
     * @throws InvalidLedgerPositionException
     * when the position is a nonpositive number or greater than the number of transactions
     */
    public String transactionToString(int position, boolean header) throws InvalidLedgerPositionException{
        String transactionString = "";
        if(position <= 0 || position > numberOfTransactions)
            throw new InvalidLedgerPositionException();
        
        if(header){
            transactionString = HEADER;
        }
        transactionString += position + ledger[position - 1].toString();
        return transactionString;
    }

    /**
     * 
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param position
     * The position of the Transaction in the ledger
     * 
     * @return
     * Returns a Transaction from the ledger
     * 
     * @throws InvalidLedgerPositionException
     * when the position is a nonpositive number or greater than the number of transactions
     */
    public Transaction getTransaction(int position) throws InvalidLedgerPositionException{
        if(position <= 0 || position > numberOfTransactions)
            throw new InvalidLedgerPositionException();
        return ledger[position - 1];
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * @return
     * Returns the ledger
     */
    public Transaction[] getLedger(){
        return ledger;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * Sets the ledger of the account to another ledger
     * @param ledger
     */
    private void setLedger(Transaction[] ledger){
        for(int i = 0; i < numberOfTransactions; i++){
            this.ledger[i] = (Transaction)ledger[i].clone();
        }
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @return
     * Returns the number of transactions
     */
    public int getNumberOfTransactions(){
        return numberOfTransactions;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param numberOfTransactions
     * Sets the number of transactions
     */
    private void setNumberOfTransactions(int numberOfTransactions){
        this.numberOfTransactions = numberOfTransactions;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @return
     * Returns the total debit amount of the account in dollars
     */
    public double getTotalDebitAmount(){
        return totalDebitAmount;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param totalDebitAmount
     * Sets the total debit amount in dollars
     */
    private void setTotalDebitAmount(double totalDebitAmount){
        this.totalDebitAmount = totalDebitAmount;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @return
     * Returns the total credit amount of the account in dollars
     */
    public double getTotalCreditAmount(){
        return totalCreditAmount;
    }

    /**
     * Preconditions:
     *  This GeneralLedger object has been instantiated.
     * 
     * @param totalCreditAmount
     * Sets the total credit amount in dollars
     */
    private void setTotalCreditAmount(double totalCreditAmount){
        this.totalCreditAmount = totalCreditAmount;
    }


}