/**
 * Text Adventure creator using a Tree of SceneNodes
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
import java.util.Scanner;

public class AdventureDesigner {

    /**
     * Driver method
     */
    public static final String MENU = "\nA) Add Scene" 
    + "\nR) Remove Scene" 
    + "\nS) Show Current Scene"
    + "\nP) Print Adventure Tree"
    + "\nB) Go Back A Scene"
    + "\nF) Go Forward A Scene"
    + "\nG) Play Game"
    + "\nN) Print Path To Cursor"
    + "\nM) Move scene"
    + "\nQ) Quit\n";
    static SceneTree st = new SceneTree();
    static Scanner stdin = new Scanner(System.in);
    public static void main(String[] args) {
        boolean isRunning = true;
        int id;
        String response, title, description, option;

        do{
            System.out.println(MENU);
            System.out.print("Please enter a selection: ");
            response = stdin.nextLine().toUpperCase();
            switch(response){
                case "A":
                    System.out.print("Please enter a title: ");
                    title = stdin.nextLine();
                    System.out.print("Please enter a scene: ");
                    description = stdin.nextLine();
                    try{
                        st.addNewNode(title, description);
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    
                    break;
                case "R":
                    System.out.print("Please enter an option: ");
                    option = stdin.nextLine().toUpperCase();
                    try{
                        st.removeScene(option);
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    System.out.println("Path " + option + " removed");
                    break;
                case "S":
                    st.getCursor().displayFullScene();
                    break;
                case "P":
                    System.out.println(st.toString());
                    break;
                case "B":
                    try{
                        st.moveCursorBackwards();
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    break;
                case "F":
                    System.out.print("Please enter a selection: ");
                    option = stdin.nextLine().toUpperCase();
                    try{
                        st.moveCursorForwards(option);
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    
                    break;
                case "G":
                    playGame();
                    break;
                case "N":

                case "M":
                    System.out.print("Move currrent scene to: ");
                    id = stdin.nextInt();
                    try{
                        st.moveScene(id);
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    break;
                case "Q":
                    isRunning = false;
                    break;
                default:
                    System.out.println("Please choose one of the options");
            }
        }while(isRunning);

        stdin.close();
    }

    /**
     * Plays the created game
     */
    public static void playGame(){
        String response = "";
        System.out.println("Now beginning game...");
        System.out.println();
        SceneNode nodePtr = st.getRoot();
        if(st.getRoot() == null){
            System.out.println("That action cannot be completed");
            return;
        }
        while(!nodePtr.isEnding()){
            System.out.println(nodePtr.getTitle());
            System.out.println(nodePtr.getSceneDescription());
            System.out.println();

            if(nodePtr.getChild("A") != null){
                System.out.println("A) " + nodePtr.getChild("A").getTitle());
            }
            if(nodePtr.getChild("B") != null){
                System.out.println("B) " + nodePtr.getChild("B").getTitle());
            }
            if(nodePtr.getChild("C") != null){
                System.out.println("C) " + nodePtr.getChild("C").getTitle());
            }

            if(nodePtr.isEnding()){
                break;
            }
            System.out.println();
            System.out.println("Please enter an operation");
            response = stdin.nextLine().toUpperCase();
            try{
                st.moveCursorForwards(response);
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
            nodePtr = st.getCursor();

        }
        System.out.println();
        System.out.println("The end");
        System.out.println("Returning back to creation mode...");
    }
}