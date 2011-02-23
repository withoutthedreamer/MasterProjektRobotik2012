package device;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerDevAddr;
import javaclient3.structures.player.PlayerDeviceDevlist;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Client API to the robot server.
 * Whatever the server is, this class is the basic interface
 * onto all other robot devices will be hooked.
 * @author sebastian
 *
 */
public class DeviceNode extends Device {

	/** Logging support */
    Logger logger = Logger.getLogger (DeviceNode.class.getName ());

	/** A list of all connected robot clients of this node */
	CopyOnWriteArrayList<PlayerClient> playerClientList = null;
	
//	CopyOnWriteArrayList<DeviceNode> deviceNodeList;
	
	private DeviceNode () {
		playerClientList = new CopyOnWriteArrayList<PlayerClient>();
//		deviceNodeList = new CopyOnWriteArrayList<DeviceNode>();
	}
	
	/**
	 * Constructor for a RobotClient.
	 * @param host The host name where the server is to connect to.
	 * @param port The port of the server listening for a client.
	 * @param clientId Robot id.
	 * @throws Exception
	 */
	public DeviceNode (String host, Integer port) 
	{
		this();
		InitRobotClient( host, new Integer(port) );
	}
	/**
	 * Accepts a list of following structure:
	 * String "hostname",Integer port
	 * All clients on the list will be instantiated on the hosts and ports given.
	 * @param origin
	 */
	public DeviceNode (final Object[] origin){
		this();
		if (origin != null) {
		int count = origin.length;
		if (count % 2 == 0) {
			for (int i=0; i<count; i+=2) {
				InitRobotClient( (String) origin[i], (Integer) origin[i+1] );
			}
		}
		}
	}
	/**
	 * Just for passing a PlayerClient reference.
	 * No instantiation is made.
	 * @param client
	 */
	public DeviceNode (final PlayerClient client) {
		this();
		playerClientList.add(client);
	}
	/**
	 * Connects to the underlying robot service and retrieves a list of all devices.
	 * Only to be called by root DeviceNode!
	 * @param host
	 * @param port
	 * @throws IllegalStateException
	 */
	void InitRobotClient(String host, Integer port) throws IllegalStateException {
		try
		{
//			this.host = host;
//			this.port = port;
			
			PlayerClient client = new PlayerClient (host, port);
			/** Do not add a playerclient as this is a root DeviceNode */
//			playerClientList.add( client );
			
			/** Create a new DeviceNode with the playerclient */
            DeviceNode devNode = new DeviceNode(client);
            devNode.setHost(host);
            devNode.setPort(port);
            /** Push requires no sleep time */
            devNode.setSleepTime(0);
            /** Add it to the internal DeviceNode list */
//            deviceNodeList.add(devNode);
//            addToDeviceList(devNode);
            getDeviceList().add(devNode);

            /** Requires that above call has internally updated device list already! */
			/** Add devices of that client to internal list */
			updateDeviceList(client, host, port);
			client.setNotThreaded();

			/** Get the devices available */
			client.requestDataDeliveryMode(PlayerConstants.PLAYER_DATAMODE_PUSH);
//			/** Push requires no sleep time */
//			setSleepTime(0);
		}
		catch (PlayerException e)
		{
		    logger.severe("Connecting");
		    throw new IllegalStateException();
		}		
	}
//	@Override
//	public void runThreaded()
//	{
//	    /**
//	     *  Start all children DeviceNodes
//	     */
//	    for (int i=0; i<deviceNodeList.size(); i++) {
//	        deviceNodeList.get(i).runThreaded();
//	    }
//	    /**
//	     * Start all devices connected to this DeviceNode
//	     */
//		if (getDeviceList().size() > 0) {
//			super.runThreaded();
//			Iterator<PlayerClient> it = playerClientList.iterator();
//			while (it.hasNext()) { it.next().runThreaded(-1, -1); }
//		}
//	}
	@Override
	protected void update()
	{
		Iterator<PlayerClient> it = playerClientList.iterator();
		while (it.hasNext()) { it.next().readAll(); }
	}
	/**
	 * Shutdown robot client and clean up
	 */
	@Override
	public void shutdown ()
	{
//	    /**
//	     * Stop all children DeviceNodes
//	     */
//	    for (int i=0; i<deviceNodeList.size(); i++) {
//            deviceNodeList.get(i).shutdown();
//        }	    
//		if (isThreaded() == true) {
			super.shutdown();
			Iterator<PlayerClient> it = playerClientList.iterator();
			while (it.hasNext()) { it.next().close(); }
//		}
//		super.shutdown();
	}
	
	/**
	 * Connects to known devices of the underlying robot service.
	 * @param playerClient
	 * @param host
	 * @param port
	 */
	private void updateDeviceList(PlayerClient playerClient, String host, int port)
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
						try {
						dev = new Planner(this, new Device(name, host, port, Indes));
						} catch (IllegalStateException e) {
							dev = new Planner(this, new Device(name, host, port, Indes));
						}
						break;

					case IDevice.DEVICE_ACTARRAY_CODE :
					    dev = new Actarray(this, new Device(name, host, port, Indes)); break;

					case IDevice.DEVICE_DIO_CODE :
					    dev = new Dio(this, new Device(name, host, port, Indes)); break;

					default: break;
					}
					if (dev != null) {
//						deviceList.add(dev);
						getDeviceList().add(dev);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return PlayerClient reference
	 */
	public PlayerClient getClient() {
		if (playerClientList.size() > 0) {
			return playerClientList.get(0);
		} else {
			return null;
		}
	}
//	public PlayerClient getPlayerClient(String host, int port) {
//	    for (int i=0; i<playerClientList.size(); i++) {
//	        // TODO check also host
//	        if(playerClientList.get(i).getPortNumber() == port)
//	            return playerClientList.get(i);
//	    }
//	    return null;
//	}
	public DeviceNode getDeviceNode(String host, int port)
	{
	    Iterator<Device> devIt = getDeviceList().iterator();
	    /** Search all DeviceNodes */
	    while (devIt.hasNext()) {
	        Device dev = devIt.next();
	        if (dev.getClass() == DeviceNode.class) {
	            /** Check for wanted DeviceNode */
	            if (dev.getHost().equals(host) && dev.getPort() == port) {
	                return (DeviceNode) dev;
	            }
	        }
	    }
//	    
//	    for (int i=0; i<deviceNodeList.size(); i++)
//	    {
//	        if (
//	                deviceNodeList.get(i).getHost().equals(host) &&
//	                deviceNodeList.get(i).getPort() == port
//	           )
//	        {
//	            return deviceNodeList.get(i);
//	        }
//	    }
	    return null;
	}
//	/**
//	 * Finds the @see PlayerDevice of this DeviceNode (if any).
//	 * @param id The @see IDevice code.
//	 * @param devNumber The device number aka index.
//	 * @param playerMode The Player mode, normally 'open mode'
//	 * @return The PlayerDevice if found, null else.
//	 */
//	public PlayerDevice getDevice (int id, int devNumber, short playerMode) {
//	    return ((RobotDevice) getDevice (
//	                new Device(id,null,-1,devNumber)
//	           )).getDevice();
//	}
}
