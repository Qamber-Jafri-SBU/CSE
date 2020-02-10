/**
 * The main method runs a menu driven application which first creates an empty GeneralLedger object. 
 *  The program prompts the user for a command to execute an operation. Once a command has been chosen, 
 *  the program may ask the user for additional information if necessary and performs the operation. 
 * 
 * @author Qamber Jafri
 *      email: qamber.jafri@stonybrook.edu
 *      Stony Brook ID: 112710107
 *      Section: R01
 */

import java.util.Scanner;

public class GeneralLedgerManager{



    static Scanner stdin = new Scanner(System.in);
        public static void main(String[] args){
        
        GeneralLedger generalLedger = new GeneralLedger();
        GeneralLedger backupGeneralLedger = new GeneralLedger();
        boolean isRunning = true;

        while(isRunning){
            System.out.print(
                "\n(A) Add Transaction\n"  
                + "(G) Get Transaction\n"
                + "(R) Remove Transaction\n"
                + "(P) Print Transactions in General Ledger\n"
                + "(F) Filter by Date\n"
                + "(L) Look for Transaction\n"
                + "(S) Size\n"
                + "(B) Backup\n"
                + "(PB) Print Transactions in Backup\n"
                + "(RB) Revert to Backup\n"
                + "(CB) Compare Backup with Current\n"
                + "(PF) Print Financial Information\n"
                + "(Q) Quit\n\n"
                + "Enter a selection: " 
                 );
            String response = stdin.next().toUpperCase();
            switch(response){
                case "A":
                    addTransaction(generalLedger);
                    break;
                case "G":
                    getTransaction(generalLedger);
                    break;
                case "R":
                    removeTransaction(generalLedger);
                    break;
                case "P":
                    printTransactions(generalLedger);
                    break;
                case "F":
                    filterByDate(generalLedger);
                    break;
                case "L":
                    searchForTransaction(generalLedger);
                    break;
                case "S":
                    size(generalLedger);
                    break;
                case "B":
                    backupGeneralLedger = backup(generalLedger, backupGeneralLedger);
                    break;
                case "PB":
                    printTransactions(backupGeneralLedger);
                    break;
                case "RB":
                    generalLedger = revertToBackup(generalLedger, backupGeneralLedger);
                    break;
                case "CB":
                    compareToBackup(generalLedger, backupGeneralLedger);
                    break;
                case "PF":
                    printFinancialData(generalLedger);
                    break;
                case "Q":
                    System.out.println("Program terminating successfully...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Please pick one of the choices!\n");
                    break;
            }
        }
        stdin.close();

    }

    /**
     * Prompts user for date, amount (in dollars), and a description in order to create a Transaction in the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void addTransaction(GeneralLedger generalLedger){
        String date, description;
        double amount;
        System.out.print("Enter date: ");
        date = stdin.next();
        System.out.print("Enter amount ($): ");
        amount = stdin.nextDouble();
        stdin.nextLine();
        System.out.print("Enter description: ");
        description = stdin.nextLine();
        
        try{   
            generalLedger.addTransaction(new Transaction(date, amount, description));
            System.out.println("\nTransaction successfully added to the general ledger.");
        }catch(Exception ex){
            System.out.println("\n" + ex.getMessage());
        }
    }

    /**
     * Prompts the user for a position in order to get a Transaction from the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void getTransaction(GeneralLedger generalLedger){
        int position;
        
        System.out.print("Enter position: ");
        position = stdin.nextInt();
        try{
            System.out.print(generalLedger.transactionToString(position, true));
        }catch(Exception ex){
            System.out.println("\n" + "No such transaction.");
        }
    }

    /**
     * Prompts the user for a position in order to remove a Transaction from the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void removeTransaction(GeneralLedger generalLedger){
        int position;
        
        System.out.print("Enter position: ");
        position = stdin.nextInt();
        
        try{
            generalLedger.removeTransaction(position);
            System.out.println("Transaction has been successfully removed from the general ledger.");
        }catch(Exception ex){
            System.out.println("\n" + ex.getMessage());
        }
    }

    /**
     * Prints the Transactions in the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void printTransactions(GeneralLedger generalLedger){
        generalLedger.printAllTransactions();
    }

    /**
     * Prompts the user for a date in order to search for Transactions conducted on a specific date from the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void filterByDate(GeneralLedger generalLedger){
        String date;
        System.out.print("Enter date: ");
        date = stdin.next();
        GeneralLedger.filter(generalLedger, date);
    }

    /**
     * Prompts the user for a date, amount (in dollars), and a description in order to search for a specific Transaction from the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void searchForTransaction(GeneralLedger generalLedger){
        String date, description;
        double amount;
        
        System.out.print("Enter date: ");
        date = stdin.next();
        System.out.print("Enter amount ($): ");
        amount = stdin.nextDouble();
        stdin.nextLine();
        System.out.print("Enter description: ");
        description = stdin.nextLine();
        generalLedger.searchForTransaction(date, amount, description);
    }

    /**
     * Prints the number of Transactions (size) in the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void size(GeneralLedger generalLedger){
        System.out.println("\nThere are " + generalLedger.size() + " transactions currently in the general ledger.");
    }

    /**
     * Backups the GeneralLedger to another GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     * @param backupGeneralLedger
     * The backup GeneralLedger to be operated on
     */
    public static GeneralLedger backup(GeneralLedger generalLedger, GeneralLedger backupGeneralLedger){
        backupGeneralLedger = (GeneralLedger)generalLedger.clone();
        System.out.println("\nCreated a backup of the current general ledger.");
        return backupGeneralLedger;
    }

    /**
     * Reverts the current GeneralLedger to the saved backup GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     * @param backupGeneralLedger
     * The backup GeneralLedger to be operated on
     */
    public static GeneralLedger revertToBackup(GeneralLedger generalLedger, GeneralLedger backupGeneralLedger){
        generalLedger = backupGeneralLedger;
        System.out.println("\nGeneral ledger successfully reverted to the backup copy.");
        return generalLedger;
    }

    /**
     * Compares the GeneralLedger to the backup GeneralLedger and prints whether or not they are equal
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void compareToBackup(GeneralLedger generalLedger, GeneralLedger backupGeneralLedger){
        if(generalLedger.equals(backupGeneralLedger)){
            System.out.println("\nThe current general ledger is the same as the backup copy.");
        }else{
            System.out.println("\nThe current general ledger is not the same as the backup copy.");
        }
    }

    /**
     * Prints the financial data of the GeneralLedger
     * 
     * @param generalLedger
     * The GeneralLedger to be operated on
     */
    public static void printFinancialData(GeneralLedger generalLedger){
        System.out.println("Financial Data for Your Account\n"
            + "---------------------------------------------------------------------------------------------------");
        System.out.println("Assets: $" + generalLedger.getTotalDebitAmount());
        System.out.println("Liabilities: $" + generalLedger.getTotalCreditAmount());
        System.out.println("Net Worth: $" + (generalLedger.getTotalDebitAmount() - generalLedger.getTotalCreditAmount()));
    }
}