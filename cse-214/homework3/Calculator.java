/**
 * Driver program for Calculator, uses Equation, HistoryStack,
 *  EquationStack, InvalidPositionException, and NoEquationToRedoException.
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.Scanner;
public class Calculator{

    public static final String MENU = "[A] Add new equation\n"
    +   "[F] Change equation from history\n"
    +   "[B] Print previous equation\n"
    +   "[P] Print full history\n"
    +   "[U] Undo\n"
    +   "[R] Redo\n"
    +   "[C] Clear history\n"
    +   "[Q] Quit\n"; //The String representation of the menu which shows possible operations
    public static final String HEADER =  String.format("%-5s %-30s %-30s %-30s %-20s %-15s %s\n", "#", "Equation",
        "Pre-fix", "Post-fix", "Answer", "Binary", "Hexadecimal")+ "--------------------------------------------"
    +   "---------------------------------------------------------------------------------------------------------"; //Header for Table of Equations
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in);
        boolean isRunning = true;
        String response;

        HistoryStack equationHistory = new HistoryStack();

        do{
            System.out.println(MENU);
            System.out.println("Select an option");
            response = stdin.nextLine().toUpperCase();

            switch(response){
                case "A":
                    System.out.print("Please enter an equation (in-fix notation): ");
                    Equation newEquation = new Equation(stdin.nextLine());
                    equationHistory.push(newEquation);
                    try{
                        if(newEquation.isValid()){
                            System.out.println("The equation is balanced and the answer is "
                                + newEquation.getAnswer());
                        }else{
                            System.out.println("The equation is not balanced");
                        }
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "F":
                    System.out.print("Which equation would you like to change? ");
                    int position = stdin.nextInt();
                    System.out.println("Equation at position " + position + ": " + equationHistory.get(position - 1));
                    System.out.print("What would you like to do to the equation (Replace / remove / add)?");
                    String choice = stdin.nextLine();
                    break;
                case "B":
                    System.out.println(HEADER);
                    System.out.println(equationHistory.peek().toString());
                    break;
                case "P":
                    System.out.println(HEADER);
                    System.out.println(equationHistory.toString());
                    break;
                case "U":
                    try{
                        equationHistory.undo();
                    }catch(Exception e){
                        System.out.println("Nothing to undo!");
                    }
                    break;
                case "R":
                    try{
                        equationHistory.redo();
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "C":
                    System.out.println("Resetting calculator");
                    while(!equationHistory.isEmpty()){
                        equationHistory.pop();
                    }
                    break;
                case "Q":
                    System.out.println("Program terminating normally...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Please pick one of the choices!");
            }
        }while(isRunning);
        stdin.close();
    }
}