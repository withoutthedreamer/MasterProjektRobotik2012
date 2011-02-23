package test;

import junit.framework.TestCase;

import org.junit.Test;

import device.DeviceNode;

public class DeviceNodeTest extends TestCase {

	static DeviceNode deviceNode = null;

	@Test
	public void testDeviceNodeStringInteger() {
		deviceNode = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode);
	}

	@Test
	public void testRunThreaded() {
		deviceNode.runThreaded();
		assertTrue(deviceNode.isThreaded());
		try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
		/** Check machine load. Must not be at ~100% */
	}

	@Test
	public void testShutdown() {
		deviceNode.shutdown();
		assertFalse(deviceNode.isThreaded());
	}

	@Test
	public void testDeviceNode3Robots() {
//		deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666});
		deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666, "localhost",6667});
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
	
	@Test
	public void testDeviceNodePlayerClient() {
		
		testDeviceNodeStringInteger();
		testRunThreaded();
		
		// Create a node only for client passing
		DeviceNode node = new DeviceNode(deviceNode.getClient());
		
		assertNotNull(node);
		
		// Should not start an own thread
		node.runThreaded();
		assertFalse(node.isThreaded());

		node.shutdown();
		assertTrue(deviceNode.isThreaded());
		
		testShutdown();
	}

}
