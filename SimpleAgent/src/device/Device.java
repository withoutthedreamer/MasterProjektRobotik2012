package device;

import java.util.Iterator;
import java.util.LinkedList;

import core.Logger;

public class Device implements IDevice, Runnable {
	int id = -1;
	
//	List<Device> deviceList = null;
//	private LinkedHashMap<String[],Device> deviceList = null;
	LinkedList<Device> deviceList = null;
	
	int name = -1;
	int host = 0;
	int port = 6665;
	int deviceNumber = 0;
	
	// Every class of this type has it's own thread
	Thread thread = new Thread ( this );

	long SLEEPTIME = 100;
	
	public Device() {
		deviceList = new LinkedList<Device>();
	}
	
	protected Device (int identifier) {
		this();
		id = identifier;
	}
	/**
	 * Add one device to internal device list
	 * @param deviceToAdd
	 */
	public Device (Device deviceToAdd) {
		this();
		if (deviceToAdd != null) {
//			deviceList.put(deviceToAdd.getId(), deviceToAdd);
			addToDeviceList(deviceToAdd);
		}
	}
	/**
	 * Add a device list to internal device list
	 * @param deviceListToAdd
	 */
	public Device (Device[] deviceListToAdd) {
		this();
		// Take id from first device on list
//		this(deviceListToAdd[0].getId());
		// Add devices to device list
		if (deviceListToAdd != null) {
			for (int i=0; i<deviceListToAdd.length; i++) { 
//				deviceList.put(deviceListToAdd[i].getId(), deviceListToAdd[i]);
				addToDeviceList(deviceListToAdd[i]);
			}
		}
	}
	// TODO check if really needed
	public Device (int name, int host, int port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}
	public Device (int name, int host, int port, int devNum) {
		this(name,host,port);
		this.deviceNumber = devNum;
	}
//	public void setId(int id) {
//		this.id = id;
//	}
	/**
	 * A device is uniquely represented by its host, port and name
	 * @return String list representing the id
	 */
	// TODO check if needed
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
		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
	}
	/**
	 * Might be to be implemented by subclass to do something
	 */
	protected void update() {
	}

	public void runThreaded() {
		// Start all devices
		if (deviceList != null) {
			Iterator<Device> deviceIterator = deviceList.iterator();
			while (deviceIterator.hasNext()) {
				Device device = deviceIterator.next();
				
//				Logger.logActivity(false, "Running", device.toString(), device.getName(), device.thread.getName());

				// Start device
				device.runThreaded();
			}
		}
		thread.start();
		Logger.logActivity(false, "Running", this.toString(), id, thread.getName());
	}

	public void shutdown() {
		// Stop all devices
		if (deviceList != null) {
			Iterator<Device> deviceIterator = deviceList.iterator();
			while (deviceIterator.hasNext()) {
				Device device = deviceIterator.next();

//				Logger.logActivity(false, "Shutdown", device.toString(), device.getName(), device.thread.getName());
				
				// Stop device
//				device.shutdown();
				device.thread.interrupt();
			}
			// empty device list
			deviceList.clear();
//			deviceList = null;
		}
//		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
		thread.interrupt();
		//		while (thread.isAlive());
	}
	/**
	 * Returns a list of devices that this robot client provides
	 * @return Device list
	 */
	public final Device[] getDeviceList() {	
		return (Device[]) deviceList.toArray();
	}
	
	public final void addToDeviceList(Device dev) {

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
	public int getHost() {
		return host;
	}
	public void setHost(int host) {
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
}
