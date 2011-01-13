/**
 * Copyright Sebastian Rockel 2010
 * Basic Pioneer 2DX class
 */
package robot;

import core.Logger;
import data.Position;
import data.Trackable;
import device.Position2d;
import device.Ranger;
import device.RobotClient;

/**
 * This class represents a minimal or standard coniguration
 * of a Pioneer 2DX robot at TAMS laboratory at University Hamburg
 * informatics center
 * It can be instantiated or inherited to add other devices.
 * @author sebastian
 *
 */
class Pioneer implements Runnable, Trackable
{
	protected RobotClient roboClient = null;
	protected Position2d posi = null;
	
	// To be implemented in subclass when needed
	protected Ranger  laser = null;
	protected Ranger  sonar = null;
//	protected Gripper grip  = null;
	
	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );
	
	protected int id = -1;
	protected double speed = -1.0;
	protected double turnrate = -1.0;
	protected enum StateType { // TODO static ?
		LWALL_FOLLOWING,
		RWALL_FOLLOWING,
	    COLLISION_AVOIDANCE,
	    WALL_SEARCHING
	}
	protected StateType currentState;
	protected enum viewDirectType { // TODO static?
	   LEFT,
	   RIGHT,
	   FRONT,
	   BACK,
	   LEFTFRONT,
	   RIGHTFRONT,
	   LEFTREAR,
	   RIGHTREAR,
	   ALL
	}
	// Parameters TODO shall be in own config file or superclass
	protected final double VEL       = 0.3;///< Normal_advance_speed in meters per sec.
	protected final double TURN_RATE = 40; ///< Max wall following turnrate in deg per sec.
	                             /// Low values: Smoother trajectory but more
	                             /// restricted
	protected final double STOP_ROT  = 30; ///< Stop rotation speed.
	                             /// Low values increase maneuverability in narrow
	                             /// edges, high values let the robot sometimes be
	                             /// stuck.
	protected final double TRACK_ROT =  40; /// Goal tracking rotation speed in degrees per sec.
	protected final double YAW_TOLERANCE = 20;///< Yaw tolerance for ball tracking in deg
	protected final double DIST_TOLERANCE = 0.5;///< Distance tolerance before stopping in meters
	protected final double WALLFOLLOWDIST = 0.5; ///< Preferred wall following distance in meters.
	protected final double STOP_WALLFOLLOWDIST = 0.2; ///< Stop distance in meters.
	protected final double WALLLOSTDIST  = 1.5; ///< Wall attractor in meters before loosing walls.
	protected final double SHAPE_DIST = 0.3; ///< Min Radius from sensor for robot shape.
	// Laser ranger
	protected final double LMAXANGLE = 240; ///< Laser max angle in degree
	protected final int BEAMCOUNT = 2; ///< Number of laser beams taken for one average distance measurement
	protected final double DEGPROBEAM   = 0.3515625; ///< 360./1024. in degree per laser beam
	protected final double LPMAX     = 5.0;  ///< max laser range in meters
	protected final double COS45     = 0.83867056795; ///< Cos(33);
	protected final double INV_COS45 = 1.19236329284; ///< 1/COS45
	protected final double DIAGOFFSET  = 0.1;  ///< Laser to sonar diagonal offset in meters.
	protected final double HORZOFFSET  = 0.15; ///< Laser to sonar horizontal offset in meters.
	protected final double MOUNTOFFSET = 0.1;  ///< Sonar vertical offset at back for laptop mount.
	protected final int LMIN  = 175;/**< LEFT min angle.       */ protected final int LMAX  = 239; ///< LEFT max angle.
	protected final int LFMIN = 140;/**< LEFTFRONT min angle.  */ protected final int LFMAX = 175; ///< LEFTFRONT max angle.
	protected final int FMIN  = 100;/**< FRONT min angle.      */ protected final int FMAX  = 140; ///< FRONT max angle.
	protected final int RFMIN = 65; /**< RIGHTFRONT min angle. */ protected final int RFMAX = 100; ///< RIGHTFRONT max angle.
	protected final int RMIN  = 0;  /**< RIGHT min angle.      */ protected final int RMAX  = 65;  ///< RIGHT max angle.
	
	//Debugging
	protected static boolean isDebugLaser = false;
	protected static boolean isDebugState = false;
	protected static boolean isDebugSonar = false;
	protected static boolean isDebugDistance = false;
	protected static boolean isDebugPosition = false;

	/**
	 * Default constructor
	 */
	public Pioneer(){}
	/**
	 * This constructor has to be overwritten in any subclasses!
	 */
	public Pioneer (String name, int port, int id) throws IllegalStateException {

		this.id = id;

		// Added standard devices
		roboClient = new RobotClient (name, port, id);
		posi = new Position2d(roboClient.getClient(), id);
	}

	// Define thread behavior
	public void run() {
		
		roboClient.runThreaded();
		
		while ( ! thread.isInterrupted()) {
			// Should not be called more than @ 10Hz
			this.update();
			try { Thread.sleep (100); }
			catch (InterruptedException e) { thread.interrupt(); }
		}
	}
	protected void update() {
		// Sensor read is done asynchronously
		plan();
		execute();
	}
	public void runThreaded() {
		thread.start();
		Logger.logActivity(false, "Running", this.toString(), this.id, thread.getName());
	}
	
	// Shutdown robot and clean up
	public void shutdown () {
		// Cleaning up
		shutdownDevices();
		posi.thread.interrupt();
		while (posi.thread.isAlive());
		roboClient.shutdown();
		this.thread.interrupt();
		while(this.thread.isAlive());
		Logger.logActivity(false, "Shutdown", this.toString(), this.id, thread.getName());
	}
		
	/**
	 * To be implemented by subclasses.
	 * When they have additional devices.
	 */
	protected void shutdownDevices(){};
		
	protected void plan () {
		double tmp_turnrate = 0.;

		if (isDebugSonar && this.sonar != null){
			double[] sonarValues = this.sonar.getRanges();	
			int 	sonarCount  = this.sonar.getCount();

			System.out.println();
			for(int i=0; i< sonarCount; i++)
				System.out.println("Sonar " + i + ": " + sonarValues[i]);
		}

		// (Left) Wall following
		this.turnrate = wallfollow();
		// Collision avoidance overrides other turnrate if neccessary!
		// May change this.turnrate or this.currentState
		this.turnrate = collisionAvoid();

		// Set speed dependend on the wall distance
		this.speed = calcspeed();

		// Check if rotating is safe
		// tune turnrate controlling here
		tmp_turnrate = checkrotate();

		// Fusion of the vectors makes a smoother trajectory
		//		this.turnrate = (tmp_turnrate + this.turnrate) / 2;
		double weight = 0.5;
		this.turnrate = weight*tmp_turnrate + (1-weight)*this.turnrate;
		if (isDebugState) {
			System.out.printf("turnrate/speed/state:\t%5.2f\t%5.2f\t%s\n", this.turnrate, this.speed, this.currentState.toString());
		}
		if (isDebugDistance) {
			if (this.laser != null) {
				System.out.print("Laser (l/lf/f/rf/r/rb/b/lb):\t");
				System.out.printf("%5.2f", getDistanceLas(LMIN,  LMAX)-HORZOFFSET);	System.out.print("\t");
				System.out.printf("%5.2f", getDistanceLas(LFMIN, LFMAX)-DIAGOFFSET);	System.out.print("\t");
				System.out.printf("%5.2f", getDistanceLas(FMIN,  FMAX));				System.out.print("\t");
				System.out.printf("%5.2f", getDistanceLas(RFMIN, RFMAX)-DIAGOFFSET);	System.out.print("\t");
				System.out.printf("%5.2f", getDistanceLas(RMIN,  RMAX) -HORZOFFSET);
				System.out.println("\t" + " XXXX" + "\t" + " XXXX" + "\t" + " XXXX");
			} else {
				System.out.println("No laser available!");
			}

			if (this.sonar != null) {
				double[] sonarValues = this.sonar.getRanges();		
				System.out.print("Sonar (l/lf/f/rf/r/rb/b/lb):\t");
				System.out.printf("%5.2f", Math.min(sonarValues[15],sonarValues[0]));	System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[1], sonarValues[2]));   System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[3], sonarValues[4]));   System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[5], sonarValues[6]));   System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[7], sonarValues[8]));   System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[9], sonarValues[10])-MOUNTOFFSET); System.out.print("\t");
				System.out.printf("%5.2f", Math.min(sonarValues[11],sonarValues[12])-MOUNTOFFSET); System.out.print("\t");
				System.out.printf("%5.2f\n", Math.min(sonarValues[13],sonarValues[14])-MOUNTOFFSET);
			} else {
				System.out.println("No sonar available!");
			}

			System.out.print("Shape (l/lf/f/rf/r/rb/b/lb):\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.LEFT)); System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.LEFTFRONT));  System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.FRONT));      System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.RIGHTFRONT)); System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.RIGHT));      System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.RIGHTREAR));  System.out.print("\t");
			System.out.printf("%5.2f", getDistance(viewDirectType.BACK));       System.out.print("\t");
			System.out.printf("%5.2f\n", getDistance(viewDirectType.LEFTREAR));
		}
		if (isDebugPosition) {
			System.out.printf("%5.2f", posi.getPosition().getX());	System.out.print("\t");
			System.out.printf("%5.2f", posi.getPosition().getY());	System.out.print("\t");
			System.out.printf("%5.2f\n", java.lang.Math.toDegrees(posi.getPosition().getYaw()));
		}
	}

	/**
	 * Command the motors
	 */
	protected final void execute() {
		posi.setSpeed(speed);
		posi.setTurnrate(turnrate);
	}
	/**
	 * Return robot position
	 */
	public Position getPosition() {
		return posi.getPosition();
	}

	/**
	 * Returns the minimum distance of the given arc.
	 * Algorithm calculates the average of BEAMCOUNT beams
	 * to define a minimum value per degree.
	 * @param minAngle Minimum angle to check (degree).
	 * @param maxAngle Maximum angle to check (degree).
	 * @return Minimum distance in range.
	 */
	protected final double getDistanceLas ( int minAngle, int maxAngle ) {
		double minDist         = LPMAX; ///< Min distance in the arc.
	    double distCurr        = LPMAX; ///< Current distance of a laser beam

	    if (this.laser != null) {
	    	if ( !(minAngle<0 || maxAngle<0 || minAngle>=maxAngle || minAngle>=LMAXANGLE || maxAngle>LMAXANGLE ) ) {

	    		final int minBIndex = (int)(minAngle/DEGPROBEAM); ///< Beam index of min deg.
	    		final int maxBIndex = (int)(maxAngle/DEGPROBEAM); ///< Beam index of max deg.
	    		double sumDist     = 0.; ///< Sum of BEAMCOUNT beam's distance.
	    		double averageDist = LPMAX; ///< Average of BEAMCOUNT beam's distance.

	    		// Read dynamic laser data
	    		int		laserCount  = this.laser.getCount();
	    		double[] laserValues = this.laser.getRanges();
	    		
	    		// Consistence check for error laser readings
	    		if (minBIndex<laserCount && maxBIndex<laserCount) {
	    			for (int beamIndex=minBIndex; beamIndex<maxBIndex; beamIndex++) {
	    				distCurr = laserValues[beamIndex];

	    				if (distCurr < 0.02) { // TODO no literal here
	    					sumDist += LPMAX;
	    				} else {
	    					sumDist += distCurr;
	    				}
	    				
	    				if((beamIndex-minBIndex) % BEAMCOUNT == 1) { ///< On each BEAMCOUNT's beam..
	    					averageDist = sumDist/BEAMCOUNT; ///< Calculate the average distance.
	    					sumDist = 0.; ///< Reset sum of beam average distance
	    					
	    					// Calculate the minimum distance for the arc
	    					if (averageDist < minDist) {
	    						minDist = averageDist;
	    					}
	    				}
	    				if ( isDebugLaser ) {
	    					System.out.printf("beamInd: %3d\tsumDist: %5.2f\taveDist: %5.2f\tminDist: %5.2f\n",
	    							beamIndex,
	    							sumDist,
	    							averageDist,
	    							minDist);
	    				}
	    			}
	    		} else {
	    			minDist = this.SHAPE_DIST;
	    		}
	    	}
	    }
		return minDist;
	}

	/**
	 * Returns the minimum distance of the given view direction.
	 * Robot shape shall be considered here by weighted SHAPE_DIST.
	 * Derived arcs, sonars and weights from graphic "PioneerShape.fig".
	 * NOTE: ALL might be slow due recursion, use it only for debugging!
	 * @param viewDirection Robot view direction
	 * @return Minimum distance of requested view Direction.
	 */
	protected final double getDistance( viewDirectType viewDirection )
	{		
		double[] sonarValues;
		int 	sonarCount  = 0;

		if (this.sonar != null) {
			sonarValues = this.sonar.getRanges();
			sonarCount  = this.sonar.getCount();
			if (sonarCount > 0) {
				// Check for dynamic sonar availability
				for (int i=16; i>0; i--) {
					if (i > sonarCount) { sonarValues[i-1] = (float)this.LPMAX;	}
					else { break; }
				}
			}  else { // NO sonar available
				sonarValues = new double[16];
				for (int i=0; i<16; i++) {
					sonarValues[i] = (float)this.LPMAX;
				}
			}
		} else { // NO sonar available
			sonarValues = new double[16];
			for (int i=0; i<16; i++) {
				sonarValues[i] = (float)this.LPMAX;
			}
		}
		
		// Scan safety areas for walls
		switch (viewDirection) {
		case LEFT      : return Math.min(getDistanceLas(LMIN,  LMAX) -HORZOFFSET-SHAPE_DIST, Math.min(sonarValues[0], sonarValues[15])-SHAPE_DIST);
		case RIGHT     : return Math.min(getDistanceLas(RMIN,  RMAX) -HORZOFFSET-SHAPE_DIST, Math.min(sonarValues[7], sonarValues[8]) -SHAPE_DIST);
		case FRONT     : return Math.min(getDistanceLas(FMIN,  FMAX)            -SHAPE_DIST, Math.min(sonarValues[3], sonarValues[4]) -SHAPE_DIST);
		case RIGHTFRONT: return Math.min(getDistanceLas(RFMIN, RFMAX)-DIAGOFFSET-SHAPE_DIST, Math.min(sonarValues[5], sonarValues[6]) -SHAPE_DIST);
		case LEFTFRONT : return Math.min(getDistanceLas(LFMIN, LFMAX)-DIAGOFFSET-SHAPE_DIST, Math.min(sonarValues[1], sonarValues[2]) -SHAPE_DIST);
		case BACK      : return Math.min(sonarValues[11], sonarValues[12])-MOUNTOFFSET-SHAPE_DIST; // Sorry, only sonar at rear
		case LEFTREAR  : return Math.min(sonarValues[13], sonarValues[14])-MOUNTOFFSET-SHAPE_DIST; // Sorry, only sonar at rear
		case RIGHTREAR : return Math.min(sonarValues[9] , sonarValues[10])-MOUNTOFFSET-SHAPE_DIST; // Sorry, only sonar at rear
		case ALL       : return Math.min(getDistance(viewDirectType.LEFT),
				Math.min(getDistance(viewDirectType.RIGHT),
						Math.min(getDistance(viewDirectType.FRONT),
								Math.min(getDistance(viewDirectType.BACK),
										Math.min(getDistance(viewDirectType.RIGHTFRONT),
												Math.min(getDistance(viewDirectType.LEFTFRONT),
														Math.min(getDistance(viewDirectType.LEFTREAR), getDistance(viewDirectType.RIGHTREAR) )))))));
		default: return 0.; // Should be recognized if happens
		}
	}
	
	/**
	 * Calculates the turnrate from range measurement and minimum wall follow
	 * distance.
	 * @return Turnrate to follow wall.
	 */
	protected final double wallfollow ()
	{
		double DistLFov  = 0;
		double DistL     = 0;
		double DistLRear = 0;

		// As long global goal is WF set it by default
		// Will potentially be overridden by higher prior behaviours
		this.currentState = StateType.LWALL_FOLLOWING;

		DistLFov  = getDistance(viewDirectType.LEFTFRONT);
		
		// do simple (left) wall following
		//do naiv calculus for turnrate; weight dist vector
		this.turnrate = Math.atan( (COS45*DistLFov - WALLFOLLOWDIST ) * 4 );
			
		// Normalize turnrate
		if (this.turnrate > Math.toRadians(TURN_RATE)) {
			this.turnrate = Math.toRadians(TURN_RATE);
		} else if (this.turnrate < -Math.toRadians(TURN_RATE)) {
			this.turnrate = -Math.toRadians(TURN_RATE);
		}

		// TODO implement wall searching behavior
		DistL     = getDistance(viewDirectType.LEFT);
		DistLRear = getDistance(viewDirectType.LEFTREAR);
		// Go straight if no wall is in distance (front, left and left front)
		if (DistLFov  >= WALLLOSTDIST  &&
			DistL     >= WALLLOSTDIST  &&
			DistLRear >= WALLLOSTDIST     )
		{
			this.turnrate = 0.;
			this.currentState = StateType.WALL_SEARCHING;
		}

		return this.turnrate;
	}

	/**
	 * Biased by left wall following
	 */
	protected final double collisionAvoid ()
	{
		// Scan FOV for Walls
		double distLeftFront  = getDistance(viewDirectType.LEFTFRONT);
		double distFront      = getDistance(viewDirectType.FRONT);
		double distRightFront = getDistance(viewDirectType.RIGHTFRONT);

		double distFrontRight = (distFront + distRightFront) / 2;
		double distFrontLeft  = (distFront + distLeftFront)  / 2;

		if ((distFrontLeft  < STOP_WALLFOLLOWDIST) ||
				(distFrontRight < STOP_WALLFOLLOWDIST)   )
		{
			this.currentState = StateType.COLLISION_AVOIDANCE;
			// Turn right as long we want left wall following
			return -Math.toRadians(STOP_ROT);
		} else {
			return this.turnrate;
		}
	}

	// TODO Code review
	protected final double calcspeed ()
	{
		double tmpMinDistFront = Math.min(getDistance(viewDirectType.LEFTFRONT), Math.min(getDistance(viewDirectType.FRONT), getDistance(viewDirectType.RIGHTFRONT)));
		double tmpMinDistBack  = Math.min(getDistance(viewDirectType.LEFTREAR), Math.min(getDistance(viewDirectType.BACK), getDistance(viewDirectType.RIGHTREAR)));
		double speed = VEL;

		if (tmpMinDistFront < WALLFOLLOWDIST) {
			speed = VEL * (tmpMinDistFront/WALLFOLLOWDIST);

			// Do not turn back if there is a wall!
			if (tmpMinDistFront<0 && tmpMinDistBack<0)
				//tmpMinDistBack<tmpMinDistFront ? speed=(VEL*tmpMinDistFront)/(tmpMinDistFront+tmpMinDistBack) : speed;
				if (tmpMinDistBack < tmpMinDistFront){
					speed = (VEL*tmpMinDistFront)/(tmpMinDistFront+tmpMinDistBack);
				}
				//speed=(VEL*(tmpMinDistBack-tmpMinDistFront))/SHAPE_DIST;
				//tmpMinDistBack<tmpMinDistFront ? speed=(VEL*(tmpMinDistFront-tmpMinDistBack))/WALLFOLLOWDIST : speed;
		}
		return speed;
	}

	/**
	 * Checks if turning the robot is not causing collisions.
	 * Implements more or less a rotation policy which decides depending on
	 * obstacles at the 4 robot edge surounding spots
	 * To not interfere to heavy to overall behaviour turnrate is only inverted (or
	 * set to zero)
	 * @return Safe turnrate.
	 */
	// TODO Code review
	protected final double checkrotate ()
	{
		double saveTurnrate = 0.;
		
		if (this.turnrate < 0) { // Right turn
			if (getDistance(viewDirectType.LEFTREAR) < 0) {
				saveTurnrate = 0;
			}
			if (getDistance(viewDirectType.RIGHT) < 0) {
				saveTurnrate = 0;
			}
		} else if (this.turnrate > 0){ // Left turn
			if (getDistance(viewDirectType.RIGHTREAR) < 0) {
				saveTurnrate = 0;
			}
			if (getDistance(viewDirectType.LEFT) < 0) {
				saveTurnrate = 0;
			}
		}
		return saveTurnrate;
	}
	@Override
	public void setGoal(Position goal) {}
	@Override
	public Position getGoal() {
		return null;
	}
}
