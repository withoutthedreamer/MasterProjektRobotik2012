package device;

import core.Logger;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class PlayerDevice extends Device
{
	javaclient3.PlayerDevice device = null;

	public PlayerDevice (RobotClient roboClient, Device device) {
		super(device);
		try
		{
			this.device = roboClient.getClient().requestInterface (name, deviceNumber, PlayerConstants.PLAYER_OPEN_MODE);
		}
		catch ( PlayerException e )
		{
			Logger.logDeviceActivity(true, "Connecting", this);
			throw new IllegalStateException();
		}

	}
}
