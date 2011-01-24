package device;

import core.Logger;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class RobotDevice extends Device
{
	javaclient3.PlayerDevice device = null;

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
			Logger.logDeviceActivity(true, "Connecting", this);
			throw new IllegalStateException();
		}

	}
}
