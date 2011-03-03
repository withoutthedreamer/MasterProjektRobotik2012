package test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;

import device.DeviceNode;

public class DeviceNodeTest extends TestCase
{
	DeviceNode deviceNode;

	@Test public void testDeviceNode1Node()
	{
		deviceNode = new DeviceNode("localhost", 6665);
	}

	@Test public void testDeviceNode2Nodes()
	{
	    deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666});
	}

	@Test public void testDeviceNode3Nodes()
	{
		deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666, "localhost",6667});
	}

	@Test public void testDeviceNode6Nodes()
	{
	    deviceNode = new DeviceNode
	    (
            new Object[]
           {
                "localhost",6665,
                "localhost",6666,
                "localhost",6667,
                "localhost",6668,
                "localhost",6669,
                "localhost",6670
           }
	    );
    }

	@Test public void testDeviceNodeAddDevices()
	{
		deviceNode = new DeviceNode("localhost", 6665);
		
		DeviceNode devNode2 = new DeviceNode("localhost", 6666);
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
