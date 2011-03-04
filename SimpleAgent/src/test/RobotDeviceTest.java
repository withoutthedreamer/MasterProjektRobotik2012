/**
 * 
 */
package test;

import javaclient3.GripperInterface;
import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;  
import static org.junit.Assert.*;

import device.Device;
import device.DeviceNode;
import device.Gripper;
import device.IDevice;

/**
 * @author sebastian
 *
 */
public class RobotDeviceTest
{
	static DeviceNode deviceNode;
	static Gripper gripper;

	@BeforeClass public static void setUpBeforeClass() throws Exception
	{
	    deviceNode = new DeviceNode("localhost", 6665);
        assertNotNull(deviceNode);
        
        deviceNode.runThreaded();
        
        gripper = (Gripper) deviceNode.getDevice(new Device(IDevice.DEVICE_GRIPPER_CODE, null, -1, -1));
        assertNotNull(gripper);

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
    @AfterClass public static void tearDownAfterClass() throws Exception
    {
        deviceNode.shutdown();
    }

	/**
	 * Test method for {@link device.RobotDevice#getDevice()}.
	 */
	@Test public void testGetDevice()
	{
		assertTrue(gripper.getDevice().getClass().getName().equals(GripperInterface.class.getName()));
	}

	/**
	 * Test method for {@link device.RobotDevice#getDeviceNode()}.
	 */
	@Test public void testGetDeviceNode()
	{
		assertTrue(gripper.getDeviceNode() == deviceNode);
	}
		
	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(RobotDeviceTest.class); 
    }
}
