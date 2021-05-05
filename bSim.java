import acm.graphics.*;
import acm.gui.DoubleField;
import acm.gui.IntField;
import acm.gui.TablePanel;
import acm.program.*;
import acm.util.RandomGenerator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
/**
 * 
 * @author alymo
 *
 */

public class bSim extends GraphicsProgram implements ActionListener{
	
private static final int WIDTH = 1200; // n.b. screen coordinates
private static final int HEIGHT = 500;
private static final int OFFSET = 100;
private static final double SCALE = HEIGHT/100;		// pixels per meter

private boolean Cleared;
volatile static boolean Traceable;
volatile static boolean PauseEnable;
volatile static boolean SimEnable ;
private boolean StackEnable;

private RandomGenerator rgen = RandomGenerator.getInstance();


bTree myTree = new bTree();

private IntField numBalls;
private DoubleField minSize;
private DoubleField maxSize;
private DoubleField minLoss;
private DoubleField maxLoss;
private DoubleField minVel;
private DoubleField maxVel;
private DoubleField ThMin;
private DoubleField ThMax;
private JButton b1;
private JButton b2;
private JButton b3;
private JButton b4;
private JButton b5;
private JToggleButton Trace;
private TablePanel P;

private boolean ClearEnable;

/**
 * The init method is responsible of all the display making the user interface
 */

public void init() {
	rgen.setSeed((long) 424242);  //initialize the seed so we obtain a precise sequence of results
	addActionListeners();
	this.resize(WIDTH, HEIGHT+OFFSET);
	
	// create the ground	
	GRect rect = new GRect(0,HEIGHT,WIDTH,3);
	rect.setFilled(true);
	add(rect);
	
	b1 = new JButton("Run");	
	b2 = new JButton("Stack");
	b3 = new JButton("Clear");	
	b4 = new JButton("Quit");	
	b5 = new JButton("Stop");
	
	Trace = new JToggleButton("Trace");

	numBalls = new IntField(60);
	minSize = new DoubleField(1.0);
	maxSize = new DoubleField(7.0);
	minLoss = new DoubleField(0.2);
	maxLoss = new DoubleField(0.6);
	minVel = new DoubleField(40.0);
	maxVel = new DoubleField(50.0);
	ThMin = new DoubleField(80.0);
	ThMax = new DoubleField(100.0);


	P = new TablePanel(10,2);						//construct the table with general simulation parametets
	add(P,EAST);
	P.add(new JLabel(" "));
	P.add(new JLabel("General simulation parameters"));
	
	P.add(new JLabel("NUMBALLS:"));
	P.add(numBalls);
	
	P.add(new JLabel("MIN SIZE:"));
	P.add(minSize);
	
	P.add(new JLabel("MAX SIZE:"));
	P.add(maxSize);
	
	P.add(new JLabel("LOSS MIN:"));
	P.add(minLoss);
	
	P.add(new JLabel("LOSS MAX:"));
	P.add(maxLoss);
	
	P.add(new JLabel("MIN VEL:"));		
	P.add(minVel);
	
	P.add(new JLabel("MAX VEL:"));
	P.add(maxVel);
	
	P.add(new JLabel("TH MIN:"));
	P.add(ThMin);
	
	P.add(new JLabel("TH MAX:"));
	P.add(ThMax);
	
	add(b1, NORTH);			//add the JButtons
	add(b2, NORTH);
	add(b3, NORTH);
	add(b5, NORTH);
	add(b4, NORTH);
	
	add(Trace, SOUTH);		// add the TracePoints
	Trace.addItemListener(itemlistener);
	addActionListeners();


	}
/**
 * The run method put conditions for the booleans.
 * This will help us make the buttons do their proper fuctions
 */
public void run() {
	
	SimEnable = false;
	StackEnable = false;

	
	while (true) {
		pause (200);		
		
		if(SimEnable) {
			doSim();
			SimEnable = false;
			
			while (myTree.isRunning());  				// Block until termination
			
			GLabel done = new GLabel("Simulation complete");	// Prompt user
			
	        done.setLocation(800, 100);
	        done.setFont("Arial-plain-12");
			done.setColor(Color.RED);
			add(done);
			
		}
		
		else if (StackEnable) {

			doStack();
			StackEnable=false;

		}
	}
}

/**
 * The actionPerformed method makes the buttons do their functions
 */

public void actionPerformed(ActionEvent e) {
	
	if (e.getActionCommand().equals("Run")) SimEnable = true;		//when the button is on start the simulation
	else if (e.getActionCommand().equals("Stack")) StackEnable = true;
	else if (e.getActionCommand().equals("Clear")) {
		Clear() ;
		Traceable = false;
	}
	else if (e.getActionCommand().equals("Quit")) System.exit(0);
	else if (e.getActionCommand().equals("Stop")) { 
		myTree.stopBalls();
	}
	
	}

/**
 * The itemListener method activates the Trace JToggleButton when it's pressed
 */

ItemListener itemlistener = new ItemListener() {		//when Trace is on add the trace points
	public void itemStateChanged(ItemEvent e) {
		int state = e.getStateChange();
		if (state == e.SELECTED) {			
			Traceable = true;
		}
		else Traceable = false;
		
	}
};

/**
* The doSim method implements the simulation loop instead of run.
* Once it's called it starts a new simulation with the parameters entered by the user
* @param void
* @return void
*/
public void doSim() {
	
	for(int i=1; i <= numBalls.getValue(); i++) {						
		
		double iSize = rgen.nextDouble(minSize.getValue(), maxSize.getValue());	//enter parameters values using the random generator
        Color iColor = rgen.nextColor();
        double iLoss = rgen.nextDouble(minLoss.getValue(), maxLoss.getValue());
        double iVel = rgen.nextDouble(minVel.getValue(), maxVel.getValue());  
        double iTheta = rgen.nextDouble(ThMin.getValue(), ThMax.getValue());
        double Xi = (WIDTH-300)/2/SCALE;
        double Yi=iSize;
  
		aBall iBall = new aBall(Xi,Yi,iVel,iTheta,iSize,iColor,iLoss,this);	//create an instance of a ball
		add(iBall.getBall());
		iBall.start();									//start makes all the balls go at the same time
		myTree.addNode(iBall);
		
	}
}

/**
* The doStack stacks the balls by size using bTree
* It works directly after the balls stop
*/

public void doStack(){

	while (myTree.isRunning()) ; // Block until termination 
	
	String str = "All stacked!";
	GLabel text = new GLabel(str, 800, 100 );// Prompt user
	text.setColor(Color.GREEN);
	text.setLabel("");
	add(text);
	bTree.X=0;
	bTree.Y= 0;
	myTree.stackBalls(); // Lay out balls in order

	
	}
	
/**
 * The Clear method removes everything in the display and adds a new ground
 * It also creates a new bTree to remove the cleared balls from the old bTree
 * 
 */
	public void Clear() {		
	
		
		removeAll();		//remove the balls from the screen
		Cleared = true;
		myTree = new bTree();
		GRect nrect = new GRect(0,HEIGHT,WIDTH,3);
		nrect.setFilled(true);
		add(nrect);
		
	

	}
public boolean getTraceable() {
	
	return Traceable;
}
	}
