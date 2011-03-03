package test;

import java.util.Timer;
import java.util.TimerTask;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.Test;

import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.ILocalizeListener;
import device.Localize;
import device.Position2d;
import device.Simulation;

public class LocalizerTest extends TestCase {
	
	static Localize localizer = null;
	static DeviceNode deviceNode = null;
	static Simulation simu = null;
	static Position2d motor = null;
	static Position curPosition;
	static boolean isNewPose;
	static boolean shutdown;

	@Test public void testInit() {
		deviceNode = new DeviceNode(new Object[]{"localhost",6665, "localhost",6666});
		assertNotNull(deviceNode);
		
		deviceNode.runThreaded();
		
//		assertEquals(deviceNode.isRunning(), true);
		assertEquals(deviceNode.isThreaded(), true);
		
		localizer = (Localize) deviceNode.getDevice(new Device(IDevice.DEVICE_LOCALIZE_CODE, null, -1, -1));
		simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		motor = (Position2d) deviceNode.getDevice(new Device(IDevice.DEVICE_POSITION2D_CODE, null, -1,-1));
		
		assertNotNull(localizer);
		assertEquals(localizer.getClass(),Localize.class);
		assertEquals(localizer.isRunning(), true);
		assertEquals(localizer.isThreaded(), true);
		
		localizer.addListener(new ILocalizeListener()
		{
			public void newPositionAvailable(Position newPose)
			{
				isNewPose = true;
				curPosition = newPose;
				System.err.println("New position: "+newPose.toString());
			}
		});
	}

	@Test public void testSetPosition() {
		Position pose = new Position(-6,-5,0);
		
		simu.setPositionOf("r0", pose);
		while(simu.getPositionOf("r0").distanceTo(pose) > 1.0);
		
		localizer.setPosition(pose);		
		
	}
	@Test public void testGetPosition() {
		isNewPose = false;
		while (isNewPose == false)
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(curPosition.distanceTo(new Position(-6,-5,0)) < 1.0);
	}
	
	@Test public void testGetPositionLoop() {
		motor.setSpeed(0.4);
		motor.setTurnrate(0.04);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() {
				shutdown = true;
			}
		}, 20000);
	}

	@Test public void testShutdown() {
		shutdown = false;
		while (shutdown == false)
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		motor.setSpeed(0.0);
		
		deviceNode.shutdown();
		assertFalse(localizer.isRunning());
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(LocalizerTest.class); 
    }
}
