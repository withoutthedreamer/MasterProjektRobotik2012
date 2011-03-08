package device;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * A Device represents an abstract entity, i.e. device node, that can have
 * other devices in its internal list. It will start and shutdown
 * these devices (if any) and itself in a dedicated thread per device.
 * 
 * @author sebastian
 */
public class Device implements IDevice, Runnable
{
	/** Logging support */
    Logger logger = Logger.getLogger (Device.class.getName ());

    /**
     * The internal device list can contain other devices.
     * For example if there is a device hierarchy.
     */
	ConcurrentLinkedQueue<Device> deviceList;
	
	/**
	 * If it is an abstract device the following fields are not set.
	 */
	/** The numerical identifiers of this device. */
	int name = -1;
	/** The host string, i.e. network name. */
	String host = null;
	/** The port number, i.e. host port. */
	int port = -1;
	/** The numerical device number. */
	int deviceNumber = -1;
	
	/** Every class of this type has it's own thread */
	Thread thread = new Thread ( this );

	/** The periodical idle time in ms this device sleeps between action cycles. */
	long SLEEPTIME = 100;
	
	/** Indicating if this device is currently in an action cycle. */
	boolean isRunning = false;
	/** Indicating if this device is started in its own thread. */
	boolean isThreaded = false;
	
	/**
	 * Creates a Device and initializes the internal device list.
	 */
	public Device()
	{
		deviceList = new ConcurrentLinkedQueue<Device>();
	}
	/**
     * This constructor is used to create a data device object
     * and has no internal devicelist or thread
     *
	 * @param name The Device id.
	 * @param host The host to which this Device is connected.
	 * @param port The port to which this Device is connected.
	 * @param devNum The Device number (aka index) of the Device.
	 */
	public Device (int name, String host, int port, int devNum)
	{
		this();
		this.name = name;
		if (host != null)
			this.host = host;
		this.port = port;
		deviceNumber = devNum;
	}
	/**
	 * Creates a device with the given properties.
	 * @param device A device template to create a new device
	 */
	public Device (Device device)
	{
	    this (
            device.getName(),
            device.getHost(),
            device.getPort(),
            device.getDeviceNumber()
	    );
	}
	/**
	 * @deprecated
	 * This constructor adds all devices of the devices in the list
	 * to its internal device list.
	 * @param aDeviceList The list of devices that contain devices.
	 */
	public Device (Device[] aDeviceList)
	{
		this();
		
		if (aDeviceList != null)
		{
			for (int i=0; i<aDeviceList.length; i++)
			{
				addDevicesOf(aDeviceList[i]);
			}
		}
	}
	/**
	 * @deprecated
	 * Adds all devices of the given device (if any and without itself)
	 * to the internal device list of this device.
	 * @param yad A device containing other devices.
	 */
	public void addDevicesOf ( Device yad )
	{
		if (yad != null && deviceList != null)
		{
			Iterator<Device> devIt = yad.getDeviceIterator();
			
			while (devIt.hasNext())
			{
				deviceList.add(devIt.next());
			}
		}
	}
	/**
	 * Might be to be implemented by subclass to do something
	 */
	protected void update() {}

	/**
	 * Starts this device (and its sub-devices) in a thread each.
	 */
	public synchronized void runThreaded()
	{
	    if (isThreaded() != true)
		{
			/** Start all devices */
			if (deviceList != null && deviceList.size() > 0)
			{
				Iterator<Device> deviceIterator = deviceList.iterator();

				while (deviceIterator.hasNext())
				{
					Device device = deviceIterator.next();

					/** Start device */
					device.runThreaded();

					/** Wait for device to be started to avoid device start order race conditions */
					while (device.isThreaded() == false)
					{
		                try { Thread.sleep (10); } catch (InterruptedException e) { e.printStackTrace(); }
					}
				}
			}

			isThreaded  = true;

			thread.start();

			logger.fine("Running "+getClass().toString()+" in "+thread.getName());
		}
	}

	/**
	 * Manages the periodical action cycles and idle times.
	 * @throws IllegalStateException When updating the device fails.
	 */
	@Override public void run() throws IllegalStateException
	{
	    try
	    { 
	        isRunning = true;
	      
	        /**
	         * The device' run loop.
	         * This has to stay as optimized as possible.
	         */
	        while (isThreaded == true)
	        {
	            /** Do sub-class specific stuff */
	            update();

	            if (SLEEPTIME > 0)
	            {
	                Thread.sleep ( SLEEPTIME );
	            }
	            else
	                if (SLEEPTIME == 0)
	                {
	                    Thread.yield();
	                }
	            /** else if (SLEEPTIME < 0) do nothing */				
	        }
	    }
        catch(InterruptedException ie)
        {
            /** Sleep interrupted */
        }
	    catch (Exception e)
	    { 
	        throw new IllegalStateException("Error updating device "+toString());
	    }
	    finally
	    {
            isRunning = false; /** sync with setNotThreaded */
            isThreaded = false; /** Thread is interrupted */
	     
            logger.fine("Shutdown "+this.getClass().toString()+" in "+thread.getName());
	    }
	}
	
	/**
	 * Shuts down this (and any sub-) device.
	 */
	public synchronized void shutdown()
	{
	    if (isThreaded() == true)
	    {
	        long delayCount = 0;

	        /** Sync with run() method */
	        isThreaded = false;

	        /** Interrupt in case it is sleeping too long */
	        if (getSleepTime() > 10000)
	            thread.interrupt();

	        /** wait to exit run loop */
	        while (isRunning() == true)
	        {
	            delayCount += 1;
	            if (delayCount > 2)
	                logger.finer("Shutdown delayed " + getClass().getName());

	            try { Thread.sleep (10); } catch (Exception e) { e.printStackTrace(); }
	        }
	    }
		
	    /** Stop all devices */
		if (deviceList.size() > 0)
		{
		    /**
		     * Loop through Device List in reverse order.
		     * That's important to to handle device dependencies correctly.
		     * E.g. a @see RobotDevice depends on a @see DeviceNode.
		     */
			Object[] devList = deviceList.toArray();
			
			for (int i=devList.length-1; i>=0; i--)
			{
			    Device device = (Device)devList[i];
				
				/** Stop device */
				device.shutdown();
			}
			
			/** empty device list */
			deviceList.clear();
		}
	}
	/**
	 * Returns a list of devices that this robot client provides.
	 * @return The device list.
	 */
	public final ConcurrentLinkedQueue<Device> getDeviceList()
	{
		return deviceList;
	}
	
	/**
     * @param deviceList the deviceList to set
     */
    protected final void setDeviceList(ConcurrentLinkedQueue<Device> deviceList)
    {
        this.deviceList = deviceList;
    }
    /**
     * @return A Device array list of the internal devices (if any)
     */
    public final Device[] getDeviceListArray()
    { 
        return deviceList.toArray(new Device[deviceList.size()]);
    }
    
    /**
     * Adds the devices of the given device node to this one.
     * @param dev The device node containing other devices.
     */
    public synchronized final void addToDeviceList(Device dev)
    {
		/** Check if it has other devices linked */
		Iterator<Device> iter = dev.getDeviceIterator(); 
		
		if (iter != null)
		{
			while (iter.hasNext()) {
				/** Add the device to internal list */	
				addToDeviceList(dev);
			}
		}
		
		deviceList.add(dev);
	}
	
    /**
     * @return An iterator of the internal device list.
     */
	public final Iterator<Device> getDeviceIterator()
	{
		if (deviceList != null)
		{
			return (Iterator<Device>) deviceList.iterator();
		}
		else
		{
			return null;
		}
	}
	/**
	 * Searches the internal device list for the device with the properties given.
	 * @param dev Device template (0 or null for args not to take care of).
	 * @return A device matching the given template when found in list, null otherwise.
	 */
	public final Device getDevice (Device dev)
	{
		int name = dev.getName();
		int number = dev.getDeviceNumber();
		Device found = null;
		
		Iterator<Device> it = getDeviceIterator();
		
		while (it.hasNext())
		{
			Device curDev = it.next();
			if (curDev.getName() == name)
			{
				if (number != -1)
				{
					if (curDev.getDeviceNumber() == number)
					{
						found = curDev;
					}
				}
				else
				{
					found = curDev;
				}
			}
		}
		return found;
	}
	/**
	 * @return This device' host string, can be null.
	 */
	public String getHost()
	{
	    return host;
	}
	/**
	 * Sets this device' host string.
	 * @param host The host string.
	 */
	public void setHost(String host)
	{
		this.host = host;
	}
	/**
	 * @return This device' host port.
	 */
	public int getPort()
	{
		return port;
	}
	/**
	 * Sets this device' host port.
	 * @param port
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	/**
	 * @return This device' numerical identifier.
	 */
	public int getName()
	{
		return name;
	}
	/**
	 * Sets this device' numerical identifier.
	 * @param name The identifier.
	 */
	public void setName(int name) {
		this.name = name;
	}
	/**
	 * @return The device number.
	 */
	public int getDeviceNumber()
	{
		return deviceNumber;
	}
	/**
	 * @return This device' thread name.
	 */
	public String getThreadName()
	{
		if (thread != null)
		{
			return thread.getName();
		}
		else
		{
			return null;
		}
	}
	/**
	 * @param time The idle time of this device.
	 */
	public void setSleepTime(long time)
	{
		this.SLEEPTIME = time;
	}
	/**
	 * @return This device' idle time.
	 */
	public long getSleepTime()
	{
		return SLEEPTIME;
	}
	/**
	 * @return true if this device is currently doing something (action cycle).
	 */
	public boolean isRunning()
	{
		return isRunning;
	}
	/**
	 * @return true if this device is threaded, false else.
	 */
	public boolean isThreaded()
	{
		return isThreaded;
	}
    /**
     * @return The logger.
     */
    public Logger getLogger()
    {
        return logger;
    }
    @Override public String toString()
    {
        return ""+getName()+","+getHost()+":"+getPort()+":"+getDeviceNumber()+"("+getSleepTime()+"ms)";
    }
    /**
     * Compares a Device according to its properties to another one
     * @param aDevice The device to compare to.
     * @return true if they equal, false else.
     */
    public boolean equals(Device aDevice)
    {
        if (
            getName() == aDevice.getName() &&
            getHost().equals(aDevice.getHost()) == true &&
            getPort() == aDevice.getPort() &&
            getDeviceNumber() == aDevice.getDeviceNumber()
        )
        {
            return true;
        }
        else
        {
            return false;   
        }
    }
    /**
     * Matches this device with the given one.
     * It compares the explicitly set device properties only.
     * Disabled properties will be ignored.
     * @param aDevice The device to match against.
     * @return true if the device matches, false else.
     */
    public boolean matches(Device aDevice)
    {
        if (aDevice == null)
            return true;
        
        if ( getName() == aDevice.getName() || getName() == -1 || aDevice.getName() == -1 )
        {
            if ( getHost() == null || aDevice.getHost() == null || getHost().equals(aDevice.getHost()) == true )
            {
                if ( getPort() == aDevice.getPort() || getPort() == -1 || aDevice.getPort() == -1 )
                {
                    if ( getDeviceNumber() == aDevice.getDeviceNumber() || getDeviceNumber() == -1 || aDevice.getDeviceNumber() == -1 )
                    {
                        return true;
                    }
                }
            }
        }
      
        return false;
    }
    /**
     * Checks if this device properties matches against any of the given device list.
     * @param aDevList The list to search in.
     * @return true if any device of the list matches, false else.
     */
    public boolean matchesList(Device[] aDevList)
    {
        for (int i=0; i<aDevList.length; i++)
        {
            if (matches(aDevList[i]) == true)
            {
                return true;
            }
        }
        
        return false;
    }
    /**
     * Checks if this device properties is in the given device list.
     * @param aDevList The list to search in.
     * @return true if any device of the list equals, false else.
     */
    public boolean isInList(Device[] aDevList)
    {
        for (int i=0; i<aDevList.length; i++)
        {
            if (equals(aDevList[i]) == true)
            {
                return true;
            }
        }
        
        return false;
    }
    public boolean isSupported()
    {
        return false;
    }
}
