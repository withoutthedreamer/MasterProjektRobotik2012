/**
 * Copyright Sebastian Rockel 2010
 * Basic Pioneer 2DX class
 */
package robot;

import device.Device;

/**
 * This class represents a minimal or standard coniguration
 * of a Pioneer 2DX robot at TAMS laboratory at University Hamburg
 * informatics center
 * It can be instantiated or inherited to add other devices.
 * @author sebastian
 *
 */
public class Pioneer extends Robot implements IPioneer
{	
    StateType currentState = StateType.SET_SPEED;

    public Pioneer (Device roboDevices) {
		super(roboDevices);
	}
	
	@Override protected void update()
	{
	    debugSensorData();

	    if (getCurrentState() == StateType.LWALL_FOLLOWING || 
	            getCurrentState() == StateType.COLLISION_AVOIDANCE)
	    {
	        setCurSpeed(IPioneer.MAXSPEED);
            setCurTurnrate( planLeftWallfollow() );
	    }
	    else
	    {
	        setCurSpeed(getSpeed());
	        setCurTurnrate(getTurnrate());
	    }
	}
	/**
	 * Stops the robot motion immediately.
	 */
	public void stop()
	{
	    if (getPlanner() != null)
	        getPlanner().stop();
	    
	    getPosi().disableMotor();
	    
	    setTurnrate(0.0);
	    setSpeed(0.0);
	    setCurSpeed(0.0);
	    setCurTurnrate(0.0);
	}

	/**
	 * Tries to set the speed given and performs collision avoidance.
	 * The actual speed might be lower due to obstacles.
	 * @param maxSpeed
	 */
	protected void setCurSpeed(double maxSpeed)
	{
	    if (getPosi() != null) {
	        /** Set speed depending on the obstacle distance */
	        double saveSpeed = calcspeed(maxSpeed);
	        
	        if (Math.abs(saveSpeed) > MINSPEED)
	            getPosi().setSpeed(saveSpeed);
	        else
	            getPosi().setSpeed(0.0);
	    }
	    
	}
	/**
	 * Tries to set the turnrate given and performs collision avoidance.
	 * The actual turnrate might be lower due to obstacles.
	 * @param maxTurnrate
	 */
	protected void setCurTurnrate(double maxTurnrate)
	{
	    if (getPosi() != null) {
	        /** Set turnrate depending on the obstacle distance */
	        double saveTurnrate = checkrotate(maxTurnrate);
	        
	        if (Math.abs(saveTurnrate) > MINTURNRATE)
	            getPosi().setTurnrate(saveTurnrate);
	        else
	            getPosi().setTurnrate(0.0);
	    }
	}
	
	protected double planLeftWallfollow ()
	{
		double newTurnrate;

		// (Left) Wall following
		newTurnrate = wallfollow();
		// Collision avoidance overrides other turnrate if neccessary!
		// May change this.turnrate or this.currentState
		newTurnrate = collisionAvoid(newTurnrate);

		// Set speed dependend on the wall distance
//		setSpeed( calcspeed(getSpeed()) );

		// Check if rotating is safe
		// tune turnrate controlling here
//		tmp_turnrate = checkrotate(getTurnrate());

		// Fusion of the vectors makes a smoother trajectory
		//		this.turnrate = (tmp_turnrate + this.turnrate) / 2;
//		double weight = 0.5;
//		setTurnrate( weight*newTurnrate + (1-weight)*getTurnrate() );
//		setTurnrate(newTurnrate);
		return newTurnrate;
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
		double minDist         = LPMAX; /** Min distance in the arc. */
		double distCurr        = LPMAX; /** Current distance of a laser beam */

		if (getLaser() != null) {
			if ( !(minAngle<0 || maxAngle<0 || minAngle>=maxAngle || minAngle>=LMAXANGLE || maxAngle>LMAXANGLE ) ) {

				final int minBIndex = (int)(minAngle/DEGPROBEAM); ///< Beam index of min deg.
				final int maxBIndex = (int)(maxAngle/DEGPROBEAM); ///< Beam index of max deg.
				double sumDist     = 0.; ///< Sum of BEAMCOUNT beam's distance.
				double averageDist = LPMAX; ///< Average of BEAMCOUNT beam's distance.

				/** Read dynamic laser data */
				int		laserCount  = getLaser().getCount();
				double[] laserValues = getLaser().getRanges();

				// Consistency check for error laser readings
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

		if (getSonar() != null) {
			sonarValues = getSonar().getRanges();
			sonarCount  = getSonar().getCount();
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
		double newTurnrate;

		// As long global goal is WF set it by default
		// Will potentially be overridden by higher prior behaviours
		setCurrentState( StateType.LWALL_FOLLOWING );

		DistLFov  = getDistance(viewDirectType.LEFTFRONT);

		// do simple (left) wall following
		//do naiv calculus for turnrate; weight dist vector
		newTurnrate = Math.atan( (COS45*DistLFov - WALLFOLLOWDIST ) * 4 );

		// Normalize turnrate
		if (newTurnrate > Math.toRadians(TURN_RATE)) {
		    newTurnrate = Math.toRadians(TURN_RATE);
		} else if (newTurnrate < -Math.toRadians(TURN_RATE)) {
		    newTurnrate = -Math.toRadians(TURN_RATE);
		}

		// TODO implement wall searching behavior
		DistL     = getDistance(viewDirectType.LEFT);
		DistLRear = getDistance(viewDirectType.LEFTREAR);
		// Go straight if no wall is in distance (front, left and left front)
		if (DistLFov  >= WALLLOSTDIST  &&
		DistL     >= WALLLOSTDIST  &&
		DistLRear >= WALLLOSTDIST     )
		{
		    newTurnrate = 0.0;
			setCurrentState( StateType.WALL_SEARCHING );
		}

		return newTurnrate;
	}

	/**
	 * Biased by left wall following
	 */
	protected final double collisionAvoid (double checkTurnrate)
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
		    setCurrentState( StateType.COLLISION_AVOIDANCE );
			// Turn right as long we want left wall following
			return -Math.toRadians(STOP_ROT);
		} else {
			return checkTurnrate;
		}
	}

	/**
	 * Calculates a safe speed regarding collision avoiding obstacles.
	 * The Speed will be zero if to near to any obstacle.
	 * @param maxSpeed The speed being trying to set.
	 * @return The safe speed.
	 */
	protected final double calcspeed (double maxSpeed)
	{
		double tmpMinDistFront = Math.min(getDistance(viewDirectType.LEFTFRONT), Math.min(getDistance(viewDirectType.FRONT), getDistance(viewDirectType.RIGHTFRONT)));
		double tmpMinDistBack  = Math.min(getDistance(viewDirectType.LEFTREAR), Math.min(getDistance(viewDirectType.BACK), getDistance(viewDirectType.RIGHTREAR)));
		double speed = maxSpeed;

		/**
		 * Check forward direction
		 */
		if (maxSpeed > 0.0)
		{
		    if (tmpMinDistFront < WALLFOLLOWDIST)
		    {
		        speed = maxSpeed * (tmpMinDistFront/WALLFOLLOWDIST);

		        /** Do not turn back if there is a wall! */
		        if (tmpMinDistFront<0 && tmpMinDistBack<0)
		        {
		            if (tmpMinDistBack < tmpMinDistFront)
		            {
		                speed = (maxSpeed*tmpMinDistFront)/(tmpMinDistFront+tmpMinDistBack);
		            }
		        }
		    }
		}
		else
		{
		    /**
	         * Check backward direction
	         */
		    if (maxSpeed < 0.0)
		    {
		        if (tmpMinDistBack < WALLFOLLOWDIST)
		        {
		            speed = maxSpeed * (tmpMinDistBack/WALLFOLLOWDIST);

		            /** Do not turn forward if there is a wall! */
		            if (tmpMinDistBack<0 && tmpMinDistFront<0)
		            {
		                if (tmpMinDistFront < tmpMinDistBack)
		                {
		                    speed = (maxSpeed*tmpMinDistBack)/(tmpMinDistBack+tmpMinDistFront);
		                }
		            }
		        }
		    }
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
	protected final double checkrotate (double maxTurnrate)
	{
		double saveTurnrate = maxTurnrate;

		/**
		 * Check for a right turn.
		 */
		if (maxTurnrate < 0) {
			if (getDistance(viewDirectType.LEFTREAR) < 0) {
				saveTurnrate = 0;
			}
			if (getDistance(viewDirectType.RIGHT) < 0) {
				saveTurnrate = 0;
			}
		/**
		 * Check for a left turn.
		 */
		} else if (maxTurnrate > 0){
			if (getDistance(viewDirectType.RIGHTREAR) < 0) {
				saveTurnrate = 0;
			}
			if (getDistance(viewDirectType.LEFT) < 0) {
				saveTurnrate = 0;
			}
		}
		return saveTurnrate;
	}

    /**
     * @return the currentState
     */
    public StateType getCurrentState() {
        return currentState;
    }

    /**
     * @param currentState the currentState to set
     */
    void setCurrentState(StateType currentState) {
        this.currentState = currentState;
    }
    public void setWallfollow() {
        getPosi().enableMotor();
        setCurrentState(StateType.LWALL_FOLLOWING);
    }

    public void setCommand() {
        getPosi().enableMotor();
        setCurrentState(StateType.SET_SPEED);        
    }

    @SuppressWarnings("unused")
    private void debugSensorData()
    {
        if (isDebugSonar && getSonar() != null){
            double[] sonarValues = getSonar().getRanges();  
            int     sonarCount  = getSonar().getCount();
    
            System.out.println();
            for(int i=0; i< sonarCount; i++)
                System.out.println("Sonar " + i + ": " + sonarValues[i]);
        }
    
        if (isDebugState) {
            System.out.printf("turnrate/speed/state:\t%5.2f\t%5.2f\t%s\n", getTurnrate(), getSpeed(), currentState.toString());
        }
        if (isDebugDistance) {
            if (this.laser != null) {
                System.out.print("Laser (l/lf/f/rf/r/rb/b/lb):\t");
                System.out.printf("%5.2f", getDistanceLas(LMIN,  LMAX)-HORZOFFSET); System.out.print("\t");
                System.out.printf("%5.2f", getDistanceLas(LFMIN, LFMAX)-DIAGOFFSET);    System.out.print("\t");
                System.out.printf("%5.2f", getDistanceLas(FMIN,  FMAX));                System.out.print("\t");
                System.out.printf("%5.2f", getDistanceLas(RFMIN, RFMAX)-DIAGOFFSET);    System.out.print("\t");
                System.out.printf("%5.2f", getDistanceLas(RMIN,  RMAX) -HORZOFFSET);
                System.out.println("\t" + " XXXX" + "\t" + " XXXX" + "\t" + " XXXX");
            } else {
                System.out.println("No laser available!");
            }
    
            if (this.sonar != null) {
                double[] sonarValues = this.sonar.getRanges();      
                System.out.print("Sonar (l/lf/f/rf/r/rb/b/lb):\t");
                System.out.printf("%5.2f", Math.min(sonarValues[15],sonarValues[0]));   System.out.print("\t");
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
            System.out.printf("%5.2f", posi.getPosition().getX());  System.out.print("\t");
            System.out.printf("%5.2f", posi.getPosition().getY());  System.out.print("\t");
            System.out.printf("%5.2f\n", java.lang.Math.toDegrees(posi.getPosition().getYaw()));
        }
        
    }
}

