/**
 * Contains information for Scenes as a Node to be used in a tree
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class SceneNode{

    private static int numScenes = 0;

    private String title;
    private String sceneDescription;
    private int sceneID;
    private SceneNode[] childScenes;
    private SceneNode parent;
    private SceneNode left, middle, right;

    /**
     * Creates an instance of the Object
     */
    public SceneNode(){
        numScenes++;
        sceneID = numScenes;
    }

    /**
     * Creates an instance of the Object
     * 
     * @param title
     *  title of the Scene
     * 
     * @param sceneDescription
     *  description of the Scene
     * 
     * @param numberOfChildren
     *  number of children each node will have
     */
    public SceneNode(String title, String sceneDescription, int numberOfChildren){
        this();
        this.title = title;
        this.sceneDescription = sceneDescription;
        childScenes = new SceneNode[numberOfChildren];
    }

    /**
     * Adds a child to this SceneNode
     * 
     * @param scene
     *  the node to add
     * @throws FullSceneException
     *  when the number of children is already equal to 3
     */
    public void addSceneNode(SceneNode scene) throws FullSceneException{
        if(left == null){
            left = scene;
        }
        else if(middle == null){
            middle = scene;
        }
        else if(right == null){
            right = scene;
        }else{
            throw new FullSceneException();
        }
    }

    /**
     * @return
     * If this SceneNode does not have any children
     */
    public boolean isEnding(){
        return left == null;
    }

    /**
     * Prints the description
     */
    public void displayScene(){
        System.out.println(sceneDescription);
    }

    /**
     * Prints the full Scene
     */
    public void displayFullScene(){
        System.out.println("Scene ID #" + sceneID);
        System.out.println("Title: " + title);
        System.out.println("Scene: " + sceneDescription);
        System.out.print("Leads To: ");

        if(left == null){
            System.out.println("NONE");
        }else {
            if(middle == null){
                System.out.print("\"" + left.getSceneDescription() + "\"" + " (#" + left.getSceneID() + ") ,");
            }else if(right == null){
                System.out.print("\"" + left.getSceneDescription() + "\"" + " (#" + left.getSceneID() + ") ,");
                System.out.print("\"" + middle.getSceneDescription() + "\"" + " (#" + middle.getSceneID() + ") ,");
            }else if(right != null){
                System.out.print("\"" + left.getSceneDescription() + "\"" + " (#" + left.getSceneID() + ") ,");
                System.out.print("\"" + middle.getSceneDescription() + "\"" + " (#" + middle.getSceneID() + ") ,");
                System.out.print("\"" + right.getSceneDescription() + "\"" + " (#" + right.getSceneID() + ") ,"); 
            }
        }
        System.out.println();
    }

    /**
     * inorder traversal to search for a node
     * 
     * @param sceneID
     *  The desired Nodes id
     * 
     * @param newNode
     *  A temporary node to return the desired node
     * 
     * @return
     *  Returns the desired Node
     */
    public SceneNode inorder(int sceneID, SceneNode newNode){
        if(this != null){
            if(left != null){
                newNode = left.inorder(sceneID, newNode);
            }
            if(this.sceneID == sceneID){
                return this;
            }
            if(middle != null){
                newNode = middle.inorder(sceneID, newNode);
            }
            if(right != null){
                newNode = right.inorder(sceneID, newNode);
            }
        }

        return newNode;

    }

    /**
     * Helped method for the inorder() method
     * 
     * @param sceneID
     *  The desired scenes id
     * 
     * @return
     *  The desired scene
     */
    public SceneNode inorderHelper(int sceneID){
        SceneNode newNode = null;
        return inorder(sceneID, newNode);
    }

    /**
     * Preorder traversal to find path to node
     * @param path
     *  array to hold nodes
     * 
     * @param node
     *  Starting node
     * 
     * @param index
     *  index in the array
     * 
     * @return
     *  The index of the array
     */
    public int preorder(SceneNode[] path, SceneNode node ,int index){
        path[index] = this;
        if(this == node){
            path[index] = node;
        }
        if(left != null){
            index = left.preorder(path, node, index + 1);
        }
        if(middle != null){
            index = middle.preorder(path, node, index + 1);
        }
        if(right != null){
            index = right.preorder(path, node, index + 1);
        }
        if(isEnding()){
            path[index] = null;
        }
        return index;
    }

    /**
     * Helper method for preorder traversal to find path
     * 
     * @param node
     *  Starting node
     */
    public void preorderHelper(SceneNode node){
        SceneNode[] path = new SceneNode[numScenes];
        preorder(path, node, 0);
        for(int i = 0; i < path.length; i++){
            if(path[i] != null){
                System.out.println(path[i].toString());
            }else{
                return;
            }
        }
    }

    /**
     * Preorder traversal to get String representation of the Tree
     * 
     * @param s
     *  A temporary String to concatenate with
     * 
     * @param depth
     *  The current depth of the tree
     * 
     * @return
     *  A string representation of the tree
     */
    public String preorder(String s, int depth){
        s += toString();
        if(left != null){
            s = left.preorder(s + "\n" + repeat("\t", depth) + "A)", depth + 1);
        }
        if(middle != null){
            s = middle.preorder(s + "\n" + repeat("\t", depth) + "B)", depth + 1);
        }
        if(right != null){
            s = right.preorder(s + "\n" + repeat("\t", depth) + "C)", depth + 1);
        }
        return s;
    }

    /**
     * Iterates a String
     * 
     * @param s
     *  The String to iterate
     * @param n
     *  The number of times to iterate the String
     * 
     * @return
     *  The iterated String
     */
    public static String repeat(String s, int n){
        while(n > 0){
            s += s;
            n--;
        }
        return s;
    }

    /**
     * A string representation of the Object
     * @return
     *  A string representation of the Object
     */
    public String toString(){
        return title + " (#" + sceneID + ")";
    }

    /**
     * Returns a child of the current node
     * @param option
     *  The desired child (A, B, C)
     * 
     * @return
     *  Returns the desired child
     */
    public SceneNode getChild(String option){
        option = option.toUpperCase();
        switch(option){
            case "A":
                return left;
            case "B":
                return middle;
            case "C":
                return right;
            default:
                return null;
        }
    }

    /**
     * Sets a specific child of the current node
     * 
     * @param option
     *  Which child to set
     * 
     * @param newNode
     *  What to set the child to
     */
    public void setChild(String option, SceneNode newNode){
        option = option.toUpperCase();
        switch(option){
            case "A":
                left = newNode;
                break;
            case "B":
                middle = newNode;
                break;
            case "C":
                right = newNode;
                break;
        }
    }

    /**
     * @return the parent
     */
    public SceneNode getParent(){
        return parent;
    }
    
    /**
     * Sets the parent node
     * @param parent
     *  The new parent node
     */
    public void setParent(SceneNode parent){
        this.parent = parent;
    }

    /**
     * 
     * @return the number of scenes created
     */
    public int getNumScene(){
        return numScenes;
    }

    /**
     * 
     * @return the title of the node
     */
    public String getTitle(){
        return title;
    }
    
    /**
     * @return the sceneID
     */
    public int getSceneID() {
        return sceneID;
    }

    /**
     * @return the sceneDescription
     */
    public String getSceneDescription() {
        return sceneDescription;
    }
}