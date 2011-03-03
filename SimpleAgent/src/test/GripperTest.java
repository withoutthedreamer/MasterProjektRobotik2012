/**
 * 
 */
package test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.Test;

import device.Device;
import device.DeviceNode;
import device.Gripper;
import device.IDevice;

/**
 * @author sebastian
 *
 */
public class GripperTest extends TestCase {

	static DeviceNode deviceNode;
	static Gripper gripper;

	@Test public void testInit() {
		deviceNode = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode);
		
		deviceNode.runThreaded();
		
		gripper = (Gripper) deviceNode.getDevice(new Device(IDevice.DEVICE_GRIPPER_CODE, null, -1, -1));
		assertNotNull(gripper);

//		testClose();
//		testRelease();
		
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	/**
	 * Test method for {@link device.Gripper#stop()}.
	 */
	@Test
	public void testStop() {
	    System.out.println("Test stop..");
		gripper.stop();
	}

	/**
	 * Test method for {@link device.Gripper#open()}.
	 */
	@Test
	public void testOpen() {
	    System.out.println("Test open..");
	    gripper.open();
		assertTrue( getState() == Gripper.stateType.OPEN );
	}

	/**
	 * Test method for {@link device.Gripper#close()}.
	 */
	@Test
	public void testClose() {
	    System.out.println("Test close..");
	    gripper.close();
		assertTrue( getState() == Gripper.stateType.CLOSED );
	}

	/**
	 * Test method for {@link device.Gripper#lift()}.
	 */
	@Test
	public void testLift() {
	    System.out.println("Test lift..");
	    gripper.lift();
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Test method for {@link device.Gripper#release()}.
	 */
	@Test
	public void testRelease() {
	    System.out.println("Test release..");
	    gripper.release();
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Test method for {@link device.Gripper#getState()}.
	 */
	public Gripper.stateType getState() {
		Gripper.stateType state = gripper.getState();
		
		System.out.println("Gripper state: "+state);
		
		return state;
	}
	public void testLiftWithObject() {
		gripper.liftWithObject();
	}
	@Test public void testShutdown() {
//		testClose();
//		testLift();
		deviceNode.shutdown();
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(GripperTest.class); 
    }
}
