/**
 * 
 */
package test;

import static org.junit.Assert.*;

import org.junit.Test;

import device.Device;
import device.DeviceNode;
import device.Gripper;
import device.IDevice;

/**
 * @author sebastian
 *
 */
public class GripperTest {

	static DeviceNode deviceNode;
	static Gripper gripper;

	@Test public void testInit() {
		deviceNode = new DeviceNode("localhost", 6665);
		assertNotNull(deviceNode);
		
		deviceNode.runThreaded();
		
		gripper = (Gripper) deviceNode.getDevice(new Device(IDevice.DEVICE_GRIPPER_CODE, null, -1, -1));
		assertNotNull(gripper);

		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	/**
	 * Test method for {@link device.Gripper#stop()}.
	 */
	@Test
	public void testStop() {
		gripper.stop();
	}

	/**
	 * Test method for {@link device.Gripper#open()}.
	 */
	@Test
	public void testOpen() {
		gripper.open();
		assertTrue( getState() == Gripper.stateType.OPEN );
	}

	/**
	 * Test method for {@link device.Gripper#close()}.
	 */
	@Test
	public void testClose() {
		gripper.close();
		assertTrue( getState() == Gripper.stateType.CLOSED );
	}

	/**
	 * Test method for {@link device.Gripper#lift()}.
	 */
	@Test
	public void testLift() {
		gripper.lift();
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Test method for {@link device.Gripper#release()}.
	 */
	@Test
	public void testRelease() {
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
	@Test public void testShutdown() {
		deviceNode.shutdown();
	}

}
