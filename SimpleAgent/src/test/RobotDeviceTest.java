/**
 * 
 */
package test;

import javaclient3.GripperInterface;
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
public class RobotDeviceTest extends TestCase
{
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
	 * Test method for {@link device.RobotDevice#getDevice()}.
	 */
	@Test
	public void testGetDevice() {
		assertTrue(gripper.getDevice().getClass().getName().equals(GripperInterface.class.getName()));
	}

	/**
	 * Test method for {@link device.RobotDevice#getDeviceNode()}.
	 */
	@Test
	public void testGetDeviceNode() {
		assertTrue(gripper.getDeviceNode() == deviceNode);
	}
	@Test public void testShutdown() {
		deviceNode.shutdown();
	}
}
