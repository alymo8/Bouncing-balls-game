/**
 * Implements a B-Tree class using a NON-RECURSIVE algorithm.
 * @author ferrie
 * @author alymo
 */

public class bTree {
	private final double Delta = 0.1;
	public static double X=0;
	public static double Y=0;
	private double lastSize=0;
	boolean runs;
	// Instance variables
	
	bNode root=null;
	
/**
 * addNode method - adds a new node by descending to the leaf node
 *                  using a while loop in place of recursion.  Ugly,
 *                  yet easy to understand.
 */
	

	public void addNode(aBall iBall) {
		
		bNode current;

// Empty tree
		
		if (root == null) {
			root = makeNode(iBall);
		}
		
// If not empty, descend to the leaf node according to
// the input data.  
		
		else {
			current = root;
			while (true) {
				if (iBall.getbSize() < current.iBall.getbSize()) {
					
// New data < data at node, branch left
					
					if (current.left == null) {				// leaf node
						current.left = makeNode(iBall);		// attach new node here
						break;
					}

					else {									// otherwise
						current = current.left;				// keep traversing
					}
				}
				else {
// New data >= data at node, branch right
					
					if (current.right == null) {			// leaf node	
						current.right = makeNode(iBall);		// attach
						break;
					}
					else {									// otherwise 
						current = current.right;			// keep traversing
					}
				}
			}
		}
		
	}
	
/**
 * makeNode
 * 
 * Creates a single instance of a bNode
 * 
 * @param	int data   Data to be added
 * @return  bNode node Node created
 */
	
	bNode makeNode(aBall iBall) {
		bNode node = new bNode();							// create new object
		node.iBall = iBall;									// initialize data field
		node.left = null;									// set both successors
		node.right = null;									// to null
		return node;										// return handle to new object
	}
	
	
	
	/**
	 * isRunning method - keeps updated on the state of the balls and returns false when all the balls stop 
	 * @return
	 */
	public boolean isRunning() {
		runs = false;
		inorder(root);
		return runs;		
	}
	
	/**
	 * inorder method - inorder traversal via call to recursive method
	 */
	public void inorder(bNode root) {
		if(root.left != null) inorder(root.left);
		if (root.iBall.getbState()) runs = true ;
		if(root.right!=null) inorder(root.right);		
	}
	
/**
 * traverse_inorder method - recursively traverses tree in order (LEFT-Root-RIGHT) and prints each node.
 */
	

	private void traverse_inorder(bNode root) {			//finds the size of the balls and places them in order
		
		if (root.left != null) traverse_inorder(root.left);
		
		double Size = root.iBall.getbSize();
		
		if (Size - lastSize < Delta) {
			Y += lastSize + Size;
			
		}
		else {
			X+=Size +lastSize;
			Y=Size;
			
		}
		lastSize = Size;
		root.iBall.moveTo(X,Y);
		if (root.right != null) traverse_inorder(root.right);
	}
	/**
	 * This method hides recursion from user.
	 */
 void stackBalls() {		// hides recursion from user
		lastSize = 0;	
		traverse_inorder(root);
		
}

/**
 * This method hides recursion from user.
 */
 public void stopBalls() {		// hides recursion from user
		
		stop(root);
		
	}

/**
 * This method freezes all the ball by stopping the threads
 * @param root
 */

	private void stop(bNode root) {			//stops the balls
		 if (root.left != null) stop(root.left);
		root.iBall.isRunning = false ;
		 if (root.right != null) stop(root.right);
	}

}


/**
 * A simple bNode class for use by bTree.  The "payload" can be
 * modified accordingly to support any object type.
 * 
 * @author ferrie
 *
 */

class bNode {
	aBall iBall;
	bNode left;
	bNode right;
}




