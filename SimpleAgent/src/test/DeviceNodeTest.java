package test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;

import data.Host;
import device.Device;
import device.DeviceNode;
import device.IDevice;

public class DeviceNodeTest extends TestCase
{
	DeviceNode deviceNode;

	@Test public void testDeviceNodeTemplate()
	{
	    deviceNode = new DeviceNode(new Host("localhost", 6665), new Device(IDevice.DEVICE_POSITION2D_CODE,"localhost",6665,0));
	    
	    Device[] dl = deviceNode.getDeviceListArray();
	    for (int i=0; i<dl.length; i++)
	    {
	        System.out.println(""+dl[i]);
	    }
	}
	
	@Test public void testDeviceNode1Node()
	{
		deviceNode = new DeviceNode(new Host("localhost", 6665));
	}

	@Test public void testDeviceNode2Nodes()
	{
	    deviceNode = new DeviceNode (
	            new Host[] {
                    new Host("localhost",6665),
                    new Host("localhost",6666)
	            });
	}

	@Test public void testDeviceNode3Nodes()
	{
	    deviceNode = new DeviceNode (
	            new Host[] {
    	            new Host("localhost",6665),
    	            new Host("localhost",6666),
    	            new Host("localhost",6667)
	            });
	}

	@Test public void testDeviceNode6Nodes()
	{
	    deviceNode = new DeviceNode (
            new Host[] {
                new Host("localhost",6665),
                new Host("localhost",6666),
                new Host("localhost",6667),
                new Host("localhost",6668),
                new Host("localhost",6669),
                new Host("localhost",6670)
            });
    }

	@Test public void testDeviceNodeAddDevices()
	{
		deviceNode = new DeviceNode(new Host("localhost", 6665));
		
		DeviceNode devNode2 = new DeviceNode(new Host("localhost", 6666));
		assertNotNull(devNode2);
		
		deviceNode.addDevicesOf(devNode2);
	}	
	
	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(DeviceNodeTest.class); 
    }

    /**
	 * @throws java.lang.Exception
	 */
    @After protected void tearDown() throws Exception
    {
	    assertNotNull(deviceNode);

    	deviceNode.runThreaded();
		assertTrue(deviceNode.isThreaded());
		
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		/** Check machine load. Must not be at ~100% */

		deviceNode.shutdown();
		assertFalse(deviceNode.isThreaded());
	}
}
