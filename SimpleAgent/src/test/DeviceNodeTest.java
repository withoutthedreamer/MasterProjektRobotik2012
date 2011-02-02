package test;

import junit.framework.TestCase;

import org.junit.Test;

import device.DeviceNode;

public class DeviceNodeTest extends TestCase {

	static DeviceNode deviceNode = null;

	@Test
	public void testDeviceNodeStringInteger() {
		deviceNode = new DeviceNode("localhost", 6666);
		assertNotNull(deviceNode);
	}

	@Test
	public void testRunThreaded() {
		deviceNode.runThreaded();
		assertTrue(deviceNode.isThreaded());
	}

	@Test
	public void testShutdown() {
		deviceNode.shutdown();
		assertFalse(deviceNode.isThreaded());
	}

	@Test
	public void testDeviceNodeObjectArray() {
		deviceNode = new DeviceNode(new Object[] {"localhost",6665, "localhost",6666});
		assertNotNull(deviceNode);
		
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
