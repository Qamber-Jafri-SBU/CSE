/**
 * A Tree of SceneNodes 
 * 
 * @author Qamber Jafri
 * 		email: qamber.jafri@stonybrook.edu
 * 		Stony Brook ID: 112710107
 * 		Section: R01
 */
public class SceneTree {

    private SceneNode root, cursor;
    private int numberOfChildren;

    /**
     * Creates an instance of this Object
     */
    public SceneTree(){
        root = null;
        cursor = root;
    }

    /**
     * Creates an instance of this Object
     * 
     * @param numberOfChildren
     *  the number of children per each node
     */
    public SceneTree(int numberOfChildren){
        this();
        this.numberOfChildren = numberOfChildren;
    }

    /**
     * Move the cursor backwards
     * @throws NoSuchNodeException
     * when there is no node to move back to
     */
    public void moveCursorBackwards() throws NoSuchNodeException{
        if(cursor == root){
            throw new NoSuchNodeException();
        }
        cursor = cursor.getParent();
        System.out.println("Moved to " + cursor.getTitle());
    }

    /**
     * Move the cursor forwards
     * 
     * @param option
     *  Which cursor to move to
     * 
     * @throws NoSuchNodeException
     *  when there is no node to move to
     */
    public void moveCursorForwards(String option) throws NoSuchNodeException{
        if(cursor.isEnding() || option.charAt(0) > 'C' || option.charAt(0) < 'A'){
            throw new NoSuchNodeException();
        }
        cursor = cursor.getChild(option);
    }

    /**
     * Adds a new node to the current node
     * @param title
     *  title of the scene
     * @param description
     *  description of the scene
     * @throws FullSceneException
     * when the current node has 3 children
     */
    public void addNewNode(String title, String description) throws FullSceneException{
        SceneNode newNode = new SceneNode(title, description, numberOfChildren);
        if(root == null){
            root = newNode;
            cursor = root;
        }else{
            cursor.addSceneNode(newNode);
            newNode.setParent(cursor);
        }
        System.out.println("Scene #" + newNode.getSceneID() + " added");
    }

    /**
     * Removes a scene from the current node
     * @param   option
     *  which Node to remove
     * @throws NoSuchNodeException
     *  when there is an incorrect selection or there are no child nodes
     */
    public void removeScene(String option) throws NoSuchNodeException{
        if(cursor.isEnding() || option.charAt(0) > 'C' || option.charAt(0) < 'A'){
            throw new NoSuchNodeException();
        }
        cursor.setChild(option, null);

        if(cursor.getChild("A") == null){
            cursor.setChild("A", cursor.getChild("B"));
            cursor.setChild("B", null);
        }
        if(cursor.getChild("B") == null){
            cursor.setChild("B", cursor.getChild("C"));
            cursor.setChild("C", null);
        }
    }

    /**
     * Moves a node to be a child of another node
     * 
     * @param sceneIDToMoveTo
     *  Node to move the current node to
     * 
     * @throws NoSuchNodeException
     *  when an incorrect scene Id is provided
     * 
     * @throws FullSceneException
     *  when the node to move to has 3 children
     */
    public void moveScene(int sceneIDToMoveTo) throws NoSuchNodeException, FullSceneException{
        SceneNode nodePtr = root;

        if(cursor.getSceneID() == sceneIDToMoveTo){
            System.out.println("Cannot choose the selected node!");
        }

        nodePtr = nodePtr.inorderHelper(sceneIDToMoveTo);
        if(nodePtr.getSceneID() != sceneIDToMoveTo){
            throw new NoSuchNodeException();
        }

        nodePtr.addSceneNode(cursor);

        if(cursor.getParent().getChild("A") == cursor){
            cursor.getParent().setChild("A", null);
        }else if(cursor.getParent().getChild("B") == cursor){
            cursor.getParent().setChild("B", null);
        }else{
            cursor.getParent().setChild("C", null);
        }
        cursor = nodePtr;
    }


    /**
     * Gets path from Root node
     * 
     * @return
     *  the String representation of the path
     */
    public String getPathFromRoot(){
        String s = root.toString();
        SceneNode arr[] = new SceneNode[root.getNumScene()];
        if(root == null){
            //exception
        }
        if(root == cursor){
            return s;
        }
        else{

            return s;
        }
    }

    /**
     * Preorder traversal of Tree
     */
    public void preorder(){
        root.preorderHelper(root);
    }

    /**
     * Gets root
     * @return root node
     */
    public SceneNode getRoot(){
        return root;
    }
    
    /**
     * Gets cursor
     * @return selected node
     */
    public SceneNode getCursor(){
        return cursor;
    }

    /**
     * String representation of the Object
     */
    public String toString(){
        return root.preorder("", 0);
    }

}