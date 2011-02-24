package test;

import junit.framework.TestCase;

import org.junit.Test;

import device.DeviceNode;

public class DeviceNodeTest extends TestCase {

	static DeviceNode deviceNode = null;

	@Test
	public void testDeviceNode1Node() {
		deviceNode = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode);
	}

	@Test
	public void testRunThreaded() {
		deviceNode.runThreaded();
		assertTrue(deviceNode.isThreaded());
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		/** Check machine load. Must not be at ~100% */
	}

	@Test
	public void testShutdown() {
		deviceNode.shutdown();
		assertFalse(deviceNode.isThreaded());
	}

	@Test
	public void testDeviceNode2Nodes() {
	    deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666});
	    assertNotNull(deviceNode);

	    testRunThreaded();

	    testShutdown();
	}

	@Test
	public void testDeviceNode3Nodes() {
		deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666, "localhost",6667});
		assertNotNull(deviceNode);
		
		testRunThreaded();
			
		testShutdown();
	}

	@Test
    public void testDeviceNode6Nodes() {
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
        assertNotNull(deviceNode);
        
        testRunThreaded();
            
        testShutdown();
    }

	@Test
	public void testDeviceNodeAddDevices() {
		deviceNode = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode);
		
		DeviceNode devNode2 = new DeviceNode("localhost", 6666);
		assertNotNull(devNode2);
		
		deviceNode.addDevicesOf(devNode2);

		testRunThreaded();

		testShutdown();
	}	
}
