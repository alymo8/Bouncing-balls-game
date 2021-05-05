import acm.graphics.*;
import acm.program.*;
import java.awt.Color;

/**
 * 
 * @author alymo
 *
 */

public class aBall extends Thread {
	
	private static final double g = 9.8; 				// MKS gravitational constant 9.8 m/s^2
	private static final double Pi = 3.141592654;		// To convert degrees to radians
	private static final double TICK = 0.1;				// Clock tick duration (sec)
	private static final double ETHR = 0.01; 			// If either Vx or Vy < ETHR STOP
	private static final double k = 0.0001;
	private static final int HEIGHT = 500;
	private static final double SCALE = HEIGHT/100;		// Pixels per meter
	
	
	private double Xi;			// initial values used by the constructor
	private double Yi;
	private double Vo;
	private double theta;
	private double bSize;
	public Color bColor;
	private double bLoss;
	private GOval myBall;
	public volatile boolean isRunning;							// Returns the state of the balls
	private bSim link;
	private double ScrX;
	private double ScrY;
	public void trace(double X, double Y)  {
		double scrx=X*SCALE;
		double scry=HEIGHT-Y*SCALE;
		 GOval tracePt = new GOval(scrx, scry, 2, 2);
			tracePt.setFilled(true);
			tracePt.setColor(bColor);
			link.add(tracePt);
			
	 }
	
	/**
	* The constructor specifies the parameters for simulation. They are
	*
	* @param Xi double The initial X position of the center of the ball
	* @param Yi double The initial Y position of the center of the ball
	* @param Vo double The initial velocity of the ball at launch
	* @param theta double Launch angle (with the horizontal plane)
	* @param bSize double The radius of the ball in simulation units
	* @param bColor Color The initial color of the ball
	* @param bLoss double Fraction [0,1] of the energy lost on each bounce
	*/
	
	public aBall (double Xi, double Yi, double Vo, double theta,	//create the constructor
			double bSize, Color bColor, double bLoss, bSim link) {
		this.Xi = Xi; // Get simulation parameters
		this.Yi = Yi;
		this.Vo = Vo;
		this.theta = theta;
		this.bSize = bSize;
		this.bColor = bColor;
		this.bLoss = bLoss;
		this.link = link;
		
		this.myBall = new GOval(Xi,Yi,2*bSize*SCALE,2*bSize*SCALE); // create the ball
		myBall.setFilled(true);
		myBall.setFillColor(bColor);
		
	}
	
	/**
	 * The getBall method makes getting the ball outside of this class possible.
	 * @return myBall
	 */
	
	public GOval getBall() {		//get the ball so it can work in aBall
		 return myBall;
		}
	
	/**
	 * The getbSize method makes getting the ball size outside of this class possible.
	 * @return bSize
	 */
	
	public double getbSize() {
		return bSize;
	}

	/**
	 * The getState method returns the the state of the balls.
	 * @return isRunning
	 */
	
	public boolean getbState() {
		return isRunning;
	}
	
	/**
	 * The moveTo method moves the ball to its corresponding coordinates.
	 * @param x x coordinate, to be converted to screen coordinates.
	 * @param y y coordinate, to be converted to screen coordinates.
	 */
	
	public void moveTo (double x,double y) {
		x= (int) ((x-bSize)*SCALE); // x position in pixels
		y= (int)(HEIGHT - (y+bSize)*SCALE); // y position in pixels
		myBall.setLocation(x,y);//moveTo(X,Y) to place the ball there
	}
	
	/**
	 * The Trace method creates the tracePoints when the JToggleButton is on.
	 */
	
	public void Trace () {
		GOval tracePt = new GOval(ScrX-bSize*SCALE, Math.min(ScrY, HEIGHT-bSize*SCALE), 2, 2);
		tracePt.setFilled(true);
		tracePt.setColor(bColor);
		link.add(tracePt);
		
	}
	
	/**
	* The run method implements the simulation loop from Assignment 1.
	* Once the start method is called on the aBall instance, the
	* code in the run method is executed concurrently with the main
	* program.
	* @param void
	* @return void
	*/
	double X,Xo,Xlast,Vx,Y,Vy,Ylast; // Position and velocity variables as defined above
	public void run() {
		 
		double Vt = g / (4*Pi*bSize*bSize*k); // Terminal velocity
		double time = 0; // time (reset at each interval)
		double Vox=Vo*Math.cos(theta*Pi/180); // Initial velocity components in X and Y
		double Voy=Vo*Math.sin(theta*Pi/180);
		double KEx=.5*Vox*Vox, KEy=.5*Voy*Voy;// Kinetic energy in X and Y directions
		double TotalE= KEx+KEy;
		double TotalElast = TotalE;		
		
		Xo=Xi; // Initial X position
		Y=bSize; // Initial Y position
		Ylast=Y; // Y position at end of previous iteration	 			
		Xlast=Xo; // Same for X.
		isRunning = true; 				// The ball moves then the program is running
		while (isRunning) {
		 
		 X = Vox*Vt/g*(1-Math.exp(-g*time/Vt)); // Update position
		 Y = bSize + Vt/g*(Voy+Vt)*(1-Math.exp(-g*time/Vt))-Vt*time;

		 Vx = (X-Xlast)/TICK; // Estimate X and Y velocities
		 Vy = (Y-Ylast)/TICK;

		 Xlast = X; // For next iteration
		 Ylast = Y;

		// Check to see if we've hit the ground. If yes, inject energy loss,
		// force current value of Y to ball radius, restart Yi for next
		// iteration. 

		TotalElast = TotalE;
		
		 if ((Vy<0) && (Y<=bSize)) {
		 
		 KEx = 0.5*Vx*Vx*(1-bLoss); // Kinetic energy in X direction after collision
		 KEy = 0.5*Vy*Vy*(1-bLoss); // Kinetic energy in Y direction after collision		
		 TotalE= KEx+KEy;
		 Vox = Math.sqrt(2*KEx); // Resulting horizontal velocity
		 if (theta>=90) Vox = (-1)*Vox; //to make the ball move in the opposite direction
		 //otherwise it will be launched again with the opposite angle
		 Voy = Math.sqrt(2*KEy); // Resulting vertical velocity
		 time=0; // Reset current interval time
		 Y=bSize; // Physically limit ball penetration
		 Xo+=X; // Add to the cumulative X displacement
		 X=0; // Reset X to zero for start of next interval
		 Xlast=X; // Reset last X and Y positions
		 Ylast=Y; 
		 if(bSim.Traceable == true) {

				Trace();
		 }
		// Termination condition - energy depletes to 0
		 
		 if ((KEy+KEx <= ETHR) || (TotalE >= TotalElast)) isRunning = false;
		 }
			
		 moveTo(X+Xo,Y);
		 // Update ball position on screen
							// current position in simulation coordinates is
							// (Xo+X,Y)
		 
if (link.getTraceable()) {
	trace (X+Xo,Y);
}
		 try { 									// pause for 50 milliseconds
				Thread.sleep(50);
				} catch (InterruptedException e) {
				e.printStackTrace();
				};; // Let display catch up
		 time+=TICK; // Update time
		}	
		    

	}
	
}


	
	

	
		
		 
