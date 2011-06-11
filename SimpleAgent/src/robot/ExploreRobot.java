package robot;

import java.util.logging.Logger;

import device.Blackboard;
import device.Device;

/**
 * An exploration robot.
 * It will explore its environment.
 * 
 * @author sebastian
 */
@SuppressWarnings("deprecation")
public class ExploreRobot extends Pioneer
{
	/** Logging support */
    Logger logger = Logger.getLogger (ExploreRobot.class.getName ());

    /** @deprecated */
	protected Blackboard blackboard = null;

	/**
	 * @deprecated Use {@link #ExploreRobot(Device[])} instead.
	 * @param roboDevices
	 */
	public ExploreRobot(Device roboDevices)
	{
		super(roboDevices);
		// TODO add behaviour blobsearching
	}
	/**
	 * Creates an exploration robot.
	 * @param devList The devices the robot can use.
	 */
	public ExploreRobot(Device[] devList)
	{
	    super(devList);
	    
	    if (getBloFi() == null)
	    {
	        logger.config("No blobfinder device found "+this);
	        throw new IllegalStateException("No blobfinder device found");
	    }
	}
	
	/**
	 * @deprecated
	 * @param bb
	 */
	public void setBlackboard (Blackboard bb)
	{
		blackboard = bb;
	}
}
