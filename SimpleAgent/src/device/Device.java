package device;

//import java.util.Collections;
import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
import java.util.concurrent.*;

import core.Logger;

public class Device implements IDevice, Runnable {
//	int id = -1;
	
//	List<Device> deviceList = null;
//	private LinkedHashMap<String[],Device> deviceList = null;
//	protected List<Device> deviceList = null;
	protected ConcurrentLinkedQueue<Device> deviceList = null;
	
	int name = -1;
	String host = "0";
	int port = 0;
	int deviceNumber = 0;
	
	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );

	long SLEEPTIME = 100;
	
	public Device() {
//		deviceList = Collections.synchronizedList( new LinkedList<Device>() );
		deviceList = new ConcurrentLinkedQueue<Device>();
	}
	//	protected Device (int identifier) {
	//		this();
	//		id = identifier;
	//	}
	//	/**
	//	 * Add one device to internal device list
	//	 * @param deviceToAdd
	//	 */
	//	public Device (Device deviceToAdd) {
	//		this();
	//		if (deviceToAdd != null) {
	////			deviceList.put(deviceToAdd.getId(), deviceToAdd);
	//			addToDeviceList(deviceToAdd);
	//		}
	//	}
	//	/**
	//	 * Add a device list to internal device list
	//	 * @param deviceListToAdd
	//	 */
	//	public Device (Device[] deviceListToAdd) {
	//		this();
	//		// Take id from first device on list
	////		this(deviceListToAdd[0].getId());
	//		// Add devices to device list
	//		if (deviceListToAdd != null) {
	//			for (int i=0; i<deviceListToAdd.length; i++) { 
	////				deviceList.put(deviceListToAdd[i].getId(), deviceListToAdd[i]);
	//				addToDeviceList(deviceListToAdd[i]);
	//			}
	//		}
	//	}
	//	// TODO check if really needed
	//	public Device (int name, int host, int port) {
	//		this.name = name;
	//		this.host = host;
	//		this.port = port;
	//	}
	/**
	 * This constructor is used to create a data device object
	 * and has no internal devicelist or thread
	 */
	public Device (int name, String host, int port, int devNum) {
		this.name = name;
		this.host = host;
		this.port = port;
		deviceNumber = devNum;
	}
	/**
	 * This constructor adds all devices of the devices in the list
	 * to its internal device list.
	 * @param aDeviceList The list of devices that contain devices.
	 */
	public Device (Device[] aDeviceList) {
		if (aDeviceList != null) {
			for (int i=0; i<aDeviceList.length; i++) {
				addDevicesOf(aDeviceList[i]);
			}
		}
	}
	/**
	 * Adds all devices of the given device (if any and without itself)
	 * to the internal device list of this device.
	 * @param yad A device containing other devices.
	 */
	public void addDevicesOf ( Device yad ) {
		if (yad != null && deviceList != null) {
			Iterator<Device> devIt = yad.getDeviceIterator();
			while (devIt.hasNext()) {
				deviceList.add(devIt.next());
			}
		}
	}
	////	public void setId(int id) {
	//		this.id = id;
	//	}
//	/**
//	 * A device is uniquely represented by its host, port and name
//	 * @return String list representing the id
//	 */
//	public String[] getId() {
//		return new String[] {host, new Integer(port).toString(), new Integer(name).toString()};
//	}
	@Override
	public void run() {
		while ( ! thread.isInterrupted()) {
			update();
			try { Thread.sleep ( SLEEPTIME ); }
			catch (InterruptedException e) { thread.interrupt(); }
		}
//		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
		Logger.logDeviceActivity(false, "Shutdown", this);
	}
	/**
	 * Might be to be implemented by subclass to do something
	 */
	protected void update() {
	}

	public synchronized void runThreaded() {
		// Start all devices
		if (deviceList.size() > 0) {
			Iterator<Device> deviceIterator = deviceList.iterator();
			while (deviceIterator.hasNext()) {
				Device device = deviceIterator.next();
				
				// Start device
				device.runThreaded();
				while (device.thread.isAlive() == false);
			}
		}
		
		thread.start();
		while (thread.isAlive() == false);
//		Logger.logActivity(false, "Running", this.toString(), id, thread.getName());
		Logger.logDeviceActivity(false, "Running", this);
	}

	public synchronized void shutdown() {
		thread.interrupt();
		while (thread.isAlive());
		
		// Stop all devices
		if (deviceList.size() > 0) {
			Iterator<Device> deviceIterator = deviceList.iterator();
			while (deviceIterator.hasNext()) {
				Device device = deviceIterator.next();

//				Logger.logActivity(false, "Shutdown", device.toString(), device.getName(), device.thread.getName());
				
				// Stop device
//				device.shutdown();
				device.thread.interrupt();
				while (device.thread.isAlive());
			}
			// empty device list
			deviceList.clear();
//			deviceList = null;
		}
//		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
		//		while (thread.isAlive());
	}
	/**
	 * Returns a list of devices that this robot client provides
	 * @return Device list
	 */
//	public final Device[] getDeviceList() {
	public final ConcurrentLinkedQueue<Device> getDeviceList() {
		return deviceList;
	}
	
	public synchronized final void addToDeviceList(Device dev) {

		// Check if it has other devices linked
		Iterator<Device> iter = dev.getDeviceIterator(); 
		if (iter != null) {
			while (iter.hasNext()) {
				// Add the device to internal list	
				addToDeviceList(dev);
			}
		}
		deviceList.add(dev);
	}
	
	public final Iterator<Device> getDeviceIterator() {
		if (deviceList != null) {
			return (Iterator<Device>) deviceList.iterator();
		} else {
			return null;
		}
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}

	public int getDeviceNumber() {
		return deviceNumber;
	}
	public String getThreadName() {
		if (thread != null) {
			return thread.getName();
		} else {
			return null;
		}
	}
}
