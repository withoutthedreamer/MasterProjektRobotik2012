/**
 * Copyright Sebastian Rockel 2010
 * Basic Pioneer 2DX class
 */
package robot;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import core.Logger;

import data.*;
import device.*;

/**
 * This class represents a minimal or standard coniguration
 * of a Pioneer 2DX robot at TAMS laboratory at University Hamburg
 * informatics center
 * It can be instantiated or inherited to add other devices.
 * @author sebastian
 *
 */
public class Pioneer extends Device implements Trackable, IPioneer
{
	// Standard devices
	Position2d posi = null;
	Ranger laser = null;
	Ranger sonar = null;
	Planner planner = null;
	Gripper gripper = null;
	Blobfinder bloFi = null;
	
	int id = -1;
	double speed = -1.0;
	double turnrate = -1.0;

	StateType currentState;

	/**
	 * Default constructor
	 */
	public Pioneer(){}
	/**
	 * This constructor has to be overwritten in any subclasses!
	 */
	//	public Pioneer (String name, int port, int id) throws IllegalStateException {
	public Pioneer (RobotClient roboClient) throws IllegalStateException {

		// Get the available devices
//		deviceList = roboClient.getDeviceList();
		
		// Make the devices available
		connectDevices(roboClient.getDeviceList());
	}
	/**
	 * It is possible to give this robot more devices by passing an
	 * RobotClient object which includes new devices.
	 * The new devices will be added to the internal device list.
	 * @param roboClient @ref RobotClient object containing new devices
	 */
	public void addDevices (RobotClient roboClient) {
		if (roboClient != null)
			connectDevices(roboClient.getDeviceList());
	}
	
	/**
	 * Initiate standard variables to this robot for the devices
	 * Note that if there are duplicate devices in the list
	 * always the last one of the same device code will be chosen!
	 * @param deviceList 
	 */
	void connectDevices (ConcurrentLinkedQueue<Device> deviceList) {
		
		if (deviceList != null) {
			Iterator<Device> devIt = deviceList.iterator();

			if (devIt != null) {
				while (devIt.hasNext()) {
					Device dev = devIt.next();

					switch (dev.getName())
					{
					case IDevice.DEVICE_POSITION2D_CODE :
						posi = (Position2d) dev; break;

					case IDevice.DEVICE_RANGER_CODE : 
						if (dev.getDeviceNumber() == 0) {
							sonar = (Ranger) dev; break;
						} else {
							laser = (Ranger) dev; break;
						}

					case IDevice.DEVICE_SONAR_CODE : 
						sonar = (RangerSonar) dev; break;

					case IDevice.DEVICE_LASER_CODE : 
						laser = (RangerLaser) dev; break;

					case IDevice.DEVICE_PLANNER_CODE :
						planner = (Planner) dev; break;
							
					case IDevice.DEVICE_BLOBFINDER_CODE :
						bloFi = (Blobfinder) dev; break;
	
					case IDevice.DEVICE_GRIPPER_CODE : 
						gripper = (Gripper) dev; break;

					default: break;
					}
				}
			}
		}
	}

//		@Override
//		public void runThreaded () {
//	//		isRunning = true;
//			Logger.logActivity(false, "Running", this.toString(), this.id, thread.getName());
//	//		super.runThreaded();
//			thread.start();
//		}
	@Override
	protected void update() {
		// Sensor read is done asynchronously
		plan();
		execute();
	}

	//	 Shutdown robot and clean up
//	@Override
//	public void shutdown () {
//		Logger.logActivity(false, "Shutdown", this.toString(), this.id, thread.getName());
//		// Cleaning up
//		thread.interrupt();
//		////		while(this.thread.isAlive());
//		//		isRunning = false;
//		//		super.shutdown();
//		//		
//		//	}
//		//	public boolean isRunning(){
//		//		return isRunning;
//	}

	/**
	 * To be implemented by subclasses.
	 * When they have additional devices.
	 */
	//	protected void shutdownDevices(){};

	@SuppressWarnings("unused")
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
		turnrate = wallfollow();
		// Collision avoidance overrides other turnrate if neccessary!
		// May change this.turnrate or this.currentState
		turnrate = collisionAvoid();

		// Set speed dependend on the wall distance
		speed = calcspeed();

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
		if (posi != null) {
			posi.setSpeed(speed);
			posi.setTurnrate(turnrate);
		}
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

		if (laser != null) {
			if ( !(minAngle<0 || maxAngle<0 || minAngle>=maxAngle || minAngle>=LMAXANGLE || maxAngle>LMAXANGLE ) ) {

				final int minBIndex = (int)(minAngle/DEGPROBEAM); ///< Beam index of min deg.
				final int maxBIndex = (int)(maxAngle/DEGPROBEAM); ///< Beam index of max deg.
				double sumDist     = 0.; ///< Sum of BEAMCOUNT beam's distance.
				double averageDist = LPMAX; ///< Average of BEAMCOUNT beam's distance.

				// Read dynamic laser data
				int		laserCount  = laser.getCount();
				double[] laserValues = laser.getRanges();

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
					minDist = SHAPE_DIST;
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

		if (sonar != null) {
			sonarValues = sonar.getRanges();
			sonarCount  = sonar.getCount();
			if (sonarCount > 0) {
				// Check for dynamic sonar availability
				for (int i=16; i>0; i--) {
					if (i > sonarCount) { sonarValues[i-1] = (float)LPMAX;	}
					else { break; }
				}
			}  else { // NO sonar available
				sonarValues = new double[16];
				for (int i=0; i<16; i++) {
					sonarValues[i] = (float)LPMAX;
				}
			}
		} else { // NO sonar available
			sonarValues = new double[16];
			for (int i=0; i<16; i++) {
				sonarValues[i] = (float)LPMAX;
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
		currentState = StateType.LWALL_FOLLOWING;

		DistLFov  = getDistance(viewDirectType.LEFTFRONT);

		// do simple (left) wall following
		//do naiv calculus for turnrate; weight dist vector
		turnrate = Math.atan( (COS45*DistLFov - WALLFOLLOWDIST ) * 4 );

		// Normalize turnrate
		if (turnrate > Math.toRadians(TURN_RATE)) {
			turnrate = Math.toRadians(TURN_RATE);
		} else if (this.turnrate < -Math.toRadians(TURN_RATE)) {
			turnrate = -Math.toRadians(TURN_RATE);
		}

		// TODO implement wall searching behavior
		DistL     = getDistance(viewDirectType.LEFT);
		DistLRear = getDistance(viewDirectType.LEFTREAR);
		// Go straight if no wall is in distance (front, left and left front)
		if (DistLFov  >= WALLLOSTDIST  &&
		DistL     >= WALLLOSTDIST  &&
		DistLRear >= WALLLOSTDIST     )
		{
			turnrate = 0.;
			currentState = StateType.WALL_SEARCHING;
		}

		return turnrate;
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
			currentState = StateType.COLLISION_AVOIDANCE;
			// Turn right as long we want left wall following
			return -Math.toRadians(STOP_ROT);
		} else {
			return turnrate;
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

		if (turnrate < 0) { // Right turn
			if (getDistance(viewDirectType.LEFTREAR) < 0) {
				saveTurnrate = 0;
			}
			if (getDistance(viewDirectType.RIGHT) < 0) {
				saveTurnrate = 0;
			}
		} else if (turnrate > 0){ // Left turn
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
	public void setPosition(Position position) {		
	}
}
