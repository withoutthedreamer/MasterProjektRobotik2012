/**
 * 
 */
package test.device;

import java.util.concurrent.CopyOnWriteArrayList;

import javaclient3.GripperInterface;
import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;  
import static org.junit.Assert.*;

import data.Host;
import device.Device;
import device.DeviceNode;
import device.Gripper;
import device.external.IDevice;

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
	    int port = 6665;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_GRIPPER_CODE,host,port,0) );
        
        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
                
        /** Get the device node */
        deviceNode = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
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
