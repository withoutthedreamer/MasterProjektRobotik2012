package robot;

import device.Device;

/**
 * A navigation robot.
 * It can be given goal positions and it will plan a path to it
 * and avoid obstacles.
 * 
 * @author sebastian
 */
public class NavRobot extends Pioneer
{
    /**
     * @deprecated Use {@link #NavRobot(Device[])} instead.
     * @param roboDevices
     */
	public NavRobot (Device roboDevices) { super(roboDevices); }
	
	/**
	 * Creates a navigation robot.
	 * @param devList The devices the robot can use.
	 */
	public NavRobot (Device[] devList)
	{
	    super(devList);
	    
	    if (getPlanner() == null)
	        throw new IllegalStateException("No planner device found "+this);
	    
	    if (getLocalizer() == null)
	        logger.info("No localize device found "+this);
//	        throw new IllegalStateException("No localize device found "+this);
	}

	@Override protected void update () {	/** Robot is planner controlled */	}
	
}
