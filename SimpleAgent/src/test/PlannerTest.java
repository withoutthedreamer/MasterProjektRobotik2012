package test;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;

import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.IPlannerListener;
import device.Localize;
import device.Planner;
import device.Simulation;

public class PlannerTest extends TestCase {
	
	static Planner planner = null;
	static Localize localizer = null;
	static DeviceNode deviceNode = null;
	static Simulation simu = null;

	 // Logging support
    Logger logger = Logger.getLogger (PlannerTest.class.getName ());
	static boolean isDone;

    @Test public void testInit() {
		deviceNode = new DeviceNode("localhost", 6666);
		assertNotNull(deviceNode);
		
		DeviceNode deviceNode2 = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode2);
		
		deviceNode.addDevicesOf(deviceNode2);

		deviceNode.runThreaded();
				
		planner = (Planner) deviceNode.getDevice(new Device(IDevice.DEVICE_PLANNER_CODE, null, -1, -1));
		localizer = (Localize) deviceNode.getDevice(new Device(IDevice.DEVICE_LOCALIZE_CODE, null, -1, -1));
		simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		
		assertNotNull(planner);
		assertEquals(planner.getClass(),Planner.class);
		assertEquals(planner.isRunning(), true);
		assertEquals(planner.isThreaded(), true);
	}

	@Test public void testSetPosition() {
		Position pose = new Position(-6,-5,Math.toRadians(90));
		
		simu.setPositionOf("r0", pose);
		while(simu.getPositionOf("r0").isNearTo(pose, 2, Math.toRadians(10)) != true);
		
		// Planner does not currently provide set position service.
		assertTrue(localizer.setPosition(pose));		
//		assertTrue(localizer.getPosition().distanceTo(pose) < 2.0);
	}
	@Test public void testGetPosition() {
		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
		
		Position curPose = planner.getPosition();
		
		boolean isNear = curPose.distanceTo(new Position(-6,-5,Math.toRadians(90))) < 2.0; 
		
		if ( isNear == false ) {
			logger.info("Planner position: "+curPose.toString());
			logger.info("Localize position: "+localizer.getPosition().toString());
		}
		
		assertTrue(isNear);
	}

	@Test public void testSetGoal() {

		Position pose = new Position(-6.5,-2,Math.toRadians(75));
		isDone = false;
		
		// Add isDone listener
		planner.addIsDoneListener(new IPlannerListener()
		{
			@Override public void callWhenIsDone() {
				isDone = true;
				logger.info("Planner is done.");
			}
		});
		
		assertTrue(planner.setGoal(pose));
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
		logger.info("Cost: "+cost);
		assertTrue(cost > 0);
	}
	@Test public void testIsActive() {
		assertTrue(planner.isActive());
	}
	@Test public void testIsDone() {
		while (isDone == false) {
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}
		assertTrue(isDone);
	}
	@Test public void testNotActive() {
		assertFalse(planner.isActive());
	}
	@Test public void testCancel() {
		Position pose = new Position(0,-6,Math.toRadians(75));

		isDone = false;
		assertTrue(planner.setGoal(pose));

		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(planner.stop());
		// TODO assert condition
	}
	@Test public void testGetCostPosition() {
		Position pose = new Position(-7,1.5,0);
		double cost;
		
		for (int i=0; i<5; i++) {
			cost = planner.getCost(pose);
			logger.info("Cost: "+cost+" to pose "+pose.toString());
			assertTrue(cost > 0);
			pose.setX(pose.getX()+1);
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	@Test public void testGetCostInvalidPosition() {
		Position pose = new Position(-90,-90,0);
		double cost;

		cost = planner.getCost(pose);
		logger.info("Cost: "+cost+" to pose "+pose.toString());
		
		assertFalse(cost > 0);
	}
	@Test public void testSetFarGoal() {
		planner.stop();
		
		// Add isDone listener
		planner.addIsDoneListener(new IPlannerListener()
		{
			@Override public void callWhenIsDone() {
				logger.info("Planner will be shutdown.");
			}
		});

//		planner.getLogger().setLevel(Level.FINEST);
		isDone = false;
		planner.setGoal(new Position(0,5,0));
	}
	@Test public void testShutdown() {
		while (isDone == false) {
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}

		deviceNode.shutdown();		
		assertFalse(planner.isRunning());
	}

}
