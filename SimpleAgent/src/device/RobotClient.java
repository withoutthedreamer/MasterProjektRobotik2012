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

	// player device list
	//	LinkedList<Device> roboDevList = null;

	protected int id = -1;

	/**
	 * Constructor for a RobotClient.
	 * @param host The host name where the server is to connect to.
	 * @param port The port of the server listening for a client.
	 * @param clientId Robot id.
	 * @throws Exception
	 */
	public RobotClient (String host, int port) throws IllegalStateException
	{
		try {
			this.host = host;
			this.port = port;
			// Connect to the Player server
			playerClient  = new PlayerClient (host, port);
			// Requires that above call has internally updated device list already!
			// TODO check this
        	updateDeviceList();
			playerClient.setNotThreaded();
			//			roboDevList = new LinkedList<Device>();

			//			playerClient.readAll();
			// Fill the device list of this device
			//			while (playerClient.isReadyPDDList() != true);
			// Get the devices available
			playerClient.requestDataDeliveryMode(PlayerConstants.PLAYER_DATAMODE_PULL);
			// Read and print (on screen) all available devices
//			playerClient.requestDeviceList();
//            playerClient.readAll ();
//            if (playerClient.isReadyPDDList()) {
//            }

		} catch (PlayerException e) {
			Logger.logActivity(true, "Connecting", this.toString(), id, null);
			throw new IllegalStateException();
		}
	}

//	// TODO check if needed
//	public RobotClient (String name, int port, int clientId) throws IllegalStateException
//	{
//		//		super(clientId);
//		this(name,port);
//	}
	//	public void addRobotClient(RobotClient aRoboClient) {
	//		// Add new robo client to device list
	//		addToDeviceList(aRoboClient);
	//	}
	/**
	 * Shutdown robot client and clean up
	 */
	@Override
	public void shutdown () {
		// Cleaning up
		//		this.posi.thread.interrupt();
		//		while (this.posi.thread.isAlive());
		// TODO run player in non threaded mode
		//		while (playerclient.isAlive());
		//		Logger.logActivity(false, "Shutdown", this.toString(), id, null);
//		this.thread.interrupt();
		// Note RobotClient must not be in the list of devices!
		// Let shutdown all devices in list and this thread
		super.shutdown();
		playerClient.close();
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

						//					case DeviceCode.DEVICE_LOCALIZE_CODE : break;
						//	
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
	/**
	 * Start PlayerClient thread.
	 * Has to be called in object constructor!
	 * Otherwise program will block forever
	 * This call has to be after all device requests!
	 */
//		@Override
//		public void runThreaded() {
//			super.runThreaded();
//			// Start player thread
////			playerClient.runThreaded (-1, -1);
//		}	
	@Override
	protected void update() {
//		if (thread.isInterrupted() != true) {
//			playerClient.readAll();
//		}
		playerClient.requestData();
		playerClient.readAll();
//		if (this.deviceList.size() < 1) {
//			updateDeviceList();
//		}
	}
}
