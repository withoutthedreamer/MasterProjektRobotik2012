package test;

import java.util.logging.Level;

import junit.framework.TestCase;

import org.junit.Test;

import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.Localize;
import device.Planner;
import device.Simulation;

public class PlannerTest extends TestCase {
	
	static Planner planner = null;
	static Localize localizer = null;
	static DeviceNode deviceNode = null;
	static Simulation simu = null;

	@Test public void testInit() {
		deviceNode = new DeviceNode("localhost", 6666);
		assertNotNull(deviceNode);
		deviceNode.getClient().getLogger().setLevel(Level.FINEST);
		
		DeviceNode deviceNode2 = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode2);
		
		deviceNode.addDevicesOf(deviceNode2);

		deviceNode.runThreaded();
		
		assertEquals(deviceNode.isRunning(), true);
		assertEquals(deviceNode.isThreaded(), true);
		
		planner = (Planner) deviceNode.getDevice(new Device(IDevice.DEVICE_PLANNER_CODE, null, -1, -1));
		localizer = (Localize) deviceNode.getDevice(new Device(IDevice.DEVICE_LOCALIZE_CODE, null, -1, -1));
		simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		
		assertNotNull(planner);
		assertEquals(planner.getClass(),Planner.class);
		assertEquals(planner.isRunning(), true);
		assertEquals(planner.isThreaded(), true);
		
		planner.stop();
	}

	@Test public void testSetPosition() {
		Position pose = new Position(-6,-5,Math.toRadians(90));
		
		simu.setPositionOf("r0", pose);
		while(simu.getPositionOf("r0").isNearTo(pose) != true);
		
		planner.setPosition(pose);
		// TODO should not be neccessary. Only as workaround as long as the planner doesn't work
		localizer.setPosition(pose);
		
		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
		assertTrue(planner.getPosition().isNearTo(pose));
		assertTrue(false); // Fix planner setposition
	}
	@Test public void testGetPosition() {
		Position curPose = planner.getPosition();
		boolean isNear = curPose.isNearTo(new Position(-6,-5,Math.toRadians(90))); 
		if ( isNear == false ) {
			System.err.println("Planner position: "+curPose.toString());
			System.err.println("Localize position: "+localizer.getPosition().toString());
		}
		assertTrue(isNear);
	}

	@Test public void testSetGoal() {

		Position pose = new Position(-6.5,-2,Math.toRadians(75));

		planner.setGoal(pose);

		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	@Test public void testIsValid(){
		assertTrue(planner.isValidGoal());
	}
	@Test public void testIsDoneFalse() {
		assertFalse(planner.isDone());
	}
	@Test public void testGetGoal() {
		assertTrue(planner.getGoal().equals(new Position(-6.5,-2,Math.toRadians(75))));
	}
	@Test public void testWPCount() {
		assertNotNull(planner.getWayPointCount());
	}
	@Test public void testWPIndex() {
		assertNotNull(planner.getWayPointIndex());
	}
	@Test public void testGetCost() {
		double cost = planner.getCost();
		System.err.println("Cost: "+cost);
		assertTrue(cost > 0);
	}
	@Test public void testIsDone() {
		try { Thread.sleep(12000); } catch (InterruptedException e) { e.printStackTrace(); }
		assertTrue(planner.isDone());
	}
	@Test public void testSetGoalAbort(){
		Position pose = new Position(-2.5,-6,Math.toRadians(75));

		planner.setGoal(pose);

		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

	}
	@Test public void testCancel() {
		planner.stop();
		assertFalse(planner.getCost() >= 0.0);
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		assertTrue(planner.isDone());
	}
	@Test public void testSetFarGoal() {
		planner.getLogger().setLevel(Level.FINEST);
		planner.setGoal(new Position(0,5,0));
//		for (int i=0; i<10; i++) {
		while(planner.isDone() != true) {
			System.err.println("Cost: "+planner.getCost());
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	@Test public void testShutdown() {
		deviceNode.shutdown();
	}

}
