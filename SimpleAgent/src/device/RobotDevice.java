package device;

import java.util.logging.Logger;

import javaclient3.PlayerDevice;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class RobotDevice extends Device
{
	/** Logging support */
    static Logger logger = Logger.getLogger (RobotDevice.class.getName ());
    
    /**
     * DeviceNode to which this device is connected.
     * This reference can be used to interface other devices from the DeviceNode.
     */
    DeviceNode deviceNode;

	PlayerDevice device;

	public RobotDevice () {}
	
	public RobotDevice (DeviceNode devNode, Device device)
	{
		super(device);
		
		try
		{
			this.device = devNode.getClient().requestInterface (name, deviceNumber, PlayerConstants.PLAYER_OPEN_MODE);
			
			if(this.device == null)
			{
				throw new IllegalStateException();
			}
			else
			{
			    deviceNode = devNode;
			}
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
