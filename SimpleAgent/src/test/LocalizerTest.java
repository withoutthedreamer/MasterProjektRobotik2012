package test;

import junit.framework.TestCase;

import org.junit.Test;

import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.Localize;
import device.Position2d;
import device.Simulation;

public class LocalizerTest extends TestCase {
	
	static Localize localizer = null;
	static DeviceNode deviceNode = null;
	static Simulation simu = null;
	static Position2d motor = null;

	@Test public void testInit() {
		deviceNode = new DeviceNode(new Object[]{"localhost",6665, "localhost",6666});
		assertNotNull(deviceNode);
		
		deviceNode.runThreaded();
		
		assertEquals(deviceNode.isRunning(), true);
		assertEquals(deviceNode.isThreaded(), true);
		
		localizer = (Localize) deviceNode.getDevice(new Device(IDevice.DEVICE_LOCALIZE_CODE, null, -1, -1));
		simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		motor = (Position2d) deviceNode.getDevice(new Device(IDevice.DEVICE_POSITION2D_CODE, null, -1,-1));
		
		assertNotNull(localizer);
		assertEquals(localizer.getClass(),Localize.class);
		assertEquals(localizer.isRunning(), true);
		assertEquals(localizer.isThreaded(), true);
	}

	@Test public void testSetPosition() {
		Position pose = new Position(-6,-5,Math.toRadians(90));
		
		simu.setPositionOf("r0", pose);
		while(simu.getPositionOf("r0").isNearTo(pose) != true);
		
		localizer.setPosition(pose);
		
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	@Test public void testGetPosition() {
		assertTrue(localizer.getPosition().isNearTo(new Position(-6,-5,Math.toRadians(90))));
	}
	
	@Test public void testGetPositionLoop() {
		motor.setSpeed(0.3);
		for (int i=0; i<10; i++) {
			System.err.println("getPosition: "+localizer.getPosition().toString());
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
		motor.setSpeed(0.0);
	}

	@Test public void testShutdown() {
		deviceNode.shutdown();
		assertFalse(localizer.isRunning());
	}

}
