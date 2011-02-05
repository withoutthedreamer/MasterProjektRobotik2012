package device;

import java.util.logging.Logger;

import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class RobotDevice extends Device
{
	// Logging support
    private static Logger logger = Logger.getLogger (RobotDevice.class.getName ());

	javaclient3.PlayerDevice device = null;

	public RobotDevice () {}
	
	public RobotDevice (DeviceNode roboClient, Device device) {
		super(device);
		try
		{
			this.device = roboClient.getClient().requestInterface (name, deviceNumber, PlayerConstants.PLAYER_OPEN_MODE);
			if(this.device == null)
				throw new IllegalStateException();
		}
		catch ( PlayerException e )
		{
//			ProjectLogger.logDeviceActivity(true, "Connecting", this);
			logger.severe("Connecting");
			throw new IllegalStateException();
		}

	}
}
