package robot;

import java.util.logging.Logger;

import device.Device;

/**
 * An exploration robot.
 * It will explore its environment.
 * 
 * @author sebastian
 */
public class ExploreRobot extends Pioneer
{
	/** Logging support */
    Logger logger = Logger.getLogger (ExploreRobot.class.getName ());

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
}
