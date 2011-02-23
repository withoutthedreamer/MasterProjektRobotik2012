package device;

import java.util.logging.Logger;

import javaclient3.PlayerDevice;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class RobotDevice extends Device
{
	/** Logging support */
    Logger logger = Logger.getLogger (RobotDevice.class.getName ());
    
    /**
     * DeviceNode to which this device is connected.
     * This reference can be used to interface other devices from the DeviceNode.
     */
    DeviceNode deviceNode;

	PlayerDevice device;

	public RobotDevice () {}
	
	public RobotDevice (DeviceNode devNode, Device devTemplate)
	{
		super(devTemplate);
		    
		deviceNode = devNode;

        if (deviceNode == null)
            throw new IllegalStateException("DeviceNode is null");

        /** Get the actual DeviceNode on this' host and port */
        DeviceNode myNode = deviceNode.getDeviceNode ( devTemplate.getHost(), devTemplate.getPort() );
        
        if (myNode == null)
            throw new IllegalStateException("DeviceNode is null");

        try
		{
            device = myNode.getPlayerClient().requestInterface
            (
                devTemplate.getName(), devTemplate.getDeviceNumber(), PlayerConstants.PLAYER_OPEN_MODE
            );

            if(device == null)
				throw new IllegalStateException();
		}
		catch ( PlayerException e )
		{
			logger.severe("Connecting");
			throw new IllegalStateException();
		}
	}

    /**
     * @return the device
     */
    public PlayerDevice getDevice() {
        return device;
    }

    /**
     * @return the @see DeviceNode to which this device is connected.
     */
    public DeviceNode getDeviceNode() {
        return deviceNode;
    }
}
