package device;

import core.Logger;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
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
			// Connect to the Player server
			playerClient  = new PlayerClient (host, port);
//			roboDevList = new LinkedList<Device>();

//			playerClient.readAll();
			// Fill the device list of this device
//			if (playerClient.isReadyPDDList() == true) {
//			while (playerClient.isReadyPDDList() != true);
			
		
			PlayerDeviceDevlist pDevList = playerClient.getPDDList();
			if (pDevList != null) {
				
				PlayerDevAddr[] pDevListAddr = pDevList.getDevList();
				if (pDevListAddr != null) {
					
					int devCount = pDevList.getDeviceCount();
					for (int i=0; i<devCount; i++) {
						
						int name = pDevListAddr[i].getInterf();
						int hosts = pDevListAddr[i].getHost();
						int Indes = pDevListAddr[i].getIndex();
						// TODO instantiate Devices here
						Device dev = null;
						switch (name)
						{
						case IDevice.DEVICE_POSITION2D_CODE :
							dev = new Position2d(this, new Device(name, hosts, port, Indes)); break;
							//						addToDeviceList(new Position2d(roboClient, id)); break;

						case IDevice.DEVICE_BLOBFINDER_CODE :
							dev = new Blobfinder(this, new Device(name, hosts, port, Indes)); break;
//							addToDeviceList( new Blobfinder(roboClient, id)); break;
							//	
						case IDevice.DEVICE_GRIPPER_CODE : 
							dev = new Gripper(this, new Device(name, hosts, port, Indes)); break;
							//						addToDeviceList( new Gripper(roboClient, id)); break;

						case IDevice.DEVICE_SONAR_CODE : 
							dev = new RangerSonar(this, new Device(name, hosts, port, Indes)); break;

						case IDevice.DEVICE_LASER_CODE : 
							dev = new RangerLaser(this, new Device(name, hosts, port, Indes)); break;

							//					case DeviceCode.DEVICE_LOCALIZE_CODE : break;
							//	
							//					case DeviceCode.DEVICE_SIMULATION_CODE : break; 
							//	
						case IDevice.DEVICE_PLANNER_CODE : 
							dev = new Planner(this, new Device(name, hosts, port, Indes)); break;
							//						addToDeviceList( new Planner(roboClient, id)); break;

						case IDevice.DEVICE_RANGER_CODE : 
//							int devId = Indes;
//							if (devId == 0) {
								dev = new Ranger(this, new Device(name, hosts, port, Indes)); break;
//							} else {
//								dev = new Ranger(this, new Device(name, hosts, port, Indes)); break;
//							}

						default: break;
						}
//						deviceList.add(new Device(name, hosts, port, Indes));
						if (dev != null) {
							deviceList.add(dev);
						}
					}
				}
			}

			
		} catch (PlayerException e) {
			Logger.logActivity(true, "Connecting", this.toString(), id, null);
			throw new IllegalStateException();
		}
	}
	// TODO check if needed
	public RobotClient (String name, int port, int clientId) throws IllegalStateException
	{
//		super(clientId);
		this(name,port);
	}
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
		playerClient.close();
//		while (playerclient.isAlive());
//		Logger.logActivity(false, "Shutdown", this.toString(), id, null);
		super.shutdown();
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
//	@Override
//	public void runThreaded() {
//		// Start player thread
////		playerClient.runThreaded (-1, -1);
//		super.runThreaded();
//	}	
	@Override
	protected void update() {
		playerClient.readAll();
	}
}
