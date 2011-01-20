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
public class RobotClient extends Device {

	// Required to every player robot
	protected PlayerClient playerClient = null;
	private boolean isRunning = false;
	private boolean isThreaded = false;

	/**
	 * Constructor for a RobotClient.
	 * @param host The host name where the server is to connect to.
	 * @param port The port of the server listening for a client.
	 * @param clientId Robot id.
	 * @throws Exception
	 */
	public RobotClient (String host, int port) throws IllegalStateException
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
		}
		catch (PlayerException e)
		{
			Logger.logDeviceActivity(true, "Connecting", this);
			throw new IllegalStateException();
		}
	}
	@Override
	public void runThreaded() {
        isThreaded  = true;
		super.runThreaded();
//		playerClient.runThreaded(-1, -1);
	}

	/**
	 * Shutdown robot client and clean up
	 */
	@Override
	public void shutdown () {
		isThreaded = false;
		
		while (isRunning == true) { // wait to exit run thread
			try { Thread.sleep (10); } catch (Exception e) { }
		}
		super.shutdown();
		//		playerClient.interrupt();
		playerClient.close();
	}
	@Override
	protected void update() {
		isRunning = true;

		while (isThreaded == true)
		{
			//		playerClient.requestData();
			playerClient.readAll();
			Thread.yield ();
		}

		isRunning = false;
	}
	private void updateDeviceList()
	{
		//		if (playerClient.isReadyPDDList() == true) {
		//		boolean isReady = playerClient.isReadyPDDList();
		//			if (playerClient.isReadyRequestDevice() == true) {
		PlayerDeviceDevlist pDevList = playerClient.getPDDList();
		if (pDevList != null) {

			PlayerDevAddr[] pDevListAddr = pDevList.getDevList();
			if (pDevListAddr != null) {

				int devCount = pDevList.getDeviceCount();
				for (int i=0; i<devCount; i++) {

					int name = pDevListAddr[i].getInterf();
//					int hosts = pDevListAddr[i].getHost();
					int Indes = pDevListAddr[i].getIndex();
					// port will be taken from this object's field
					// host will be taken from this object's field
					// TODO instantiate Devices here
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
						//						addToDeviceList( new Blobfinder(roboClient, id)); break;
						//	
					case IDevice.DEVICE_GRIPPER_CODE : 
						dev = new Gripper(this, new Device(name, host, port, Indes)); break;
						//						addToDeviceList( new Gripper(roboClient, id)); break;

					case IDevice.DEVICE_SONAR_CODE : 
						dev = new RangerSonar(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_LASER_CODE : 
						dev = new RangerLaser(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_LOCALIZE_CODE : 
						dev = new Localize(this, new Device(name, host, port, Indes)); break;
	
						//					case DeviceCode.DEVICE_SIMULATION_CODE : break; 
						//	
					case IDevice.DEVICE_PLANNER_CODE : 
						dev = new Planner(this, new Device(name, host, port, Indes)); break;
						//						addToDeviceList( new Planner(roboClient, id)); break;

					default: break;
					}
					//					deviceList.add(new Device(name, host, port, Indes));
					if (dev != null) {
						deviceList.add(dev);
					}
				}
			}
		}
		//		}
	}

	/**
	 * 
	 * @return PlayerClient API
	 */
	public PlayerClient getClient() {
		return playerClient;
	}
}
