package device;

import core.Logger;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerDevAddr;
import javaclient3.structures.player.PlayerDeviceDevlist;

/**
 * Client API to the robot server.
 * Whatever the server is, this class is the basic interface
 * onto all other robot devices will be hooked.
 * @author sebastian
 *
 */
public class DeviceNode extends Device {

	// Required to every player robot
	protected PlayerClient playerClient = null;

	/**
	 * Constructor for a RobotClient.
	 * @param host The host name where the server is to connect to.
	 * @param port The port of the server listening for a client.
	 * @param clientId Robot id.
	 * @throws Exception
	 */
	public DeviceNode (String host, int port) throws IllegalStateException
	{
		try
		{
			this.host = host;
			this.port = port;
			// Connect to the Player server
			playerClient  = new PlayerClient (host, port);
			// Requires that above call has internally updated device list already!
			// TODO check this
        	updateDeviceList();
			playerClient.setNotThreaded();
			
			// Get the devices available
			playerClient.requestDataDeliveryMode(PlayerConstants.PLAYER_DATAMODE_PUSH);
			// Push requires no sleep time
			setSleepTime(0);
		}
		catch (PlayerException e)
		{
			Logger.logDeviceActivity(true, "Connecting", this);
			throw new IllegalStateException();
		}
	}
	@Override
	public void runThreaded()
	{
		super.runThreaded();
//		playerClient.runThreaded(-1, -1);
	}
	@Override
	protected void update()
	{
		playerClient.readAll();
	}
	/**
	 * Shutdown robot client and clean up
	 */
	@Override
	public void shutdown ()
	{
		super.shutdown();
		playerClient.close();
	}
	
	private void updateDeviceList()
	{
		PlayerDeviceDevlist pDevList = playerClient.getPDDList();
		if (pDevList != null) {

			PlayerDevAddr[] pDevListAddr = pDevList.getDevList();
			if (pDevListAddr != null) {

				int devCount = pDevList.getDeviceCount();
				for (int i=0; i<devCount; i++) {

					int name = pDevListAddr[i].getInterf();
					int Indes = pDevListAddr[i].getIndex();
					// port will be taken from this object's field
					// host will be taken from this object's field
					Device dev = null;
					switch (name)
					{
					case IDevice.DEVICE_POSITION2D_CODE :
						if (Indes == 0)
							dev = new Position2d(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_RANGER_CODE : 
						dev = new Ranger(this, new Device(name, host, port, Indes)); break;
						
					case IDevice.DEVICE_BLOBFINDER_CODE :
						dev = new Blobfinder(this, new Device(name, host, port, Indes)); break;
	
					case IDevice.DEVICE_GRIPPER_CODE : 
						dev = new Gripper(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_SONAR_CODE : 
						dev = new RangerSonar(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_LASER_CODE : 
						dev = new RangerLaser(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_LOCALIZE_CODE : 
						dev = new Localize(this, new Device(name, host, port, Indes)); break;
	
					case IDevice.DEVICE_SIMULATION_CODE : 
						dev = new Simulation(this, new Device(name, host, port, Indes)); break; 

					case IDevice.DEVICE_PLANNER_CODE : 
						dev = new Planner(this, new Device(name, host, port, Indes)); break;

					default: break;
					}
					if (dev != null) {
						deviceList.add(dev);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return PlayerClient API
	 */
	public PlayerClient getClient() {
		return playerClient;
	}
}
