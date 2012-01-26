package device;

import java.util.logging.Logger;

import javaclient3.PlayerDevice;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

/**
 * Represents a practical robot device.
 * A robot device has a device of the underlying robot client layer.
 * @author sebastian
 */
public class RobotDevice extends Device
{
	/** Logging support */
    Logger logger = Logger.getLogger (RobotDevice.class.getName ());
    
    /**
     * DeviceNode to which this device is connected.
     * This reference can be used to interface other devices from the DeviceNode.
     */
    DeviceNode deviceNode;

    /**
     * The actual device of the underlying robot client layer.
     */
	PlayerDevice device;

	/**
	 * Do some standard init stuff.
	 */
	public RobotDevice () {}
	
	/**
	 * Creates a robot device.
	 * Connects to the underlying robot client layer.
	 * @param devNode The device node this device is connected to.
	 * @param devTemplate The device properties.
	 * @throws IllegalStateException When initializing fails.
	 */
	public RobotDevice (DeviceNode devNode, Device devTemplate) throws IllegalStateException
	{
		super(devTemplate);

        if (devNode == null)
        {
            throw new IllegalStateException("Primary device node is null at "+toString());
        }
        else
        {
            deviceNode = devNode;

            /** Get the actual DeviceNode on this' host and port */
            DeviceNode myNode = deviceNode.getDeviceNode ( devTemplate.getHost(), devTemplate.getPort() );

            if (myNode == null)
            {
                throw new IllegalStateException("Secondary device node is null at "+toString());
            }

            try
            {
                device = myNode.getPlayerClient().requestInterface
                (
                    devTemplate.getId(),
                    devTemplate.getIndex(),
                    PlayerConstants.PLAYER_OPEN_MODE
                );

                if(device == null)
                {
                    throw new IllegalStateException("Player device is null at "+toString());
                }
            }
            catch ( PlayerException e )
            {
                String log = "Error connecting robot device "+toString();
                logger.severe(log);
                throw new IllegalStateException(log);
            }
        }
	}

    /**
     * @return The @see PlayerDevice.
     */
    public PlayerDevice getDevice()
    {
        return device;
    }

    /**
     * @return the @see DeviceNode to which this device is connected.
     */
    public DeviceNode getDeviceNode()
    {
        return deviceNode;
    }

    /**
     * @return the logger
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * @return This object's string.
     */
    @Override public String toString()
    {
        return ""+getId()+"@"+getHost()+":"+getPort()+":"+getIndex();
    }
    
}
