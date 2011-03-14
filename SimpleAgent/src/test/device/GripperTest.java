/**
 * 
 */
package test.device;

import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import data.Host;
import device.Device;
import device.DeviceNode;
import device.Gripper;
import device.IDevice;

/**
 * @author sebastian
 *
 */
public class GripperTest
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
	 * Test method for {@link device.Gripper#stop()}.
	 */
	@Test public void testStop()
	{
	    System.out.println("Test stop..");
		gripper.stop();
	}

	/**
	 * Test method for {@link device.Gripper#open()}.
	 */
	@Test public void testOpen()
	{
	    System.out.println("Test open..");
	    gripper.open();
		assertTrue( getState() == Gripper.stateType.OPEN );
	}

	/**
	 * Test method for {@link device.Gripper#close()}.
	 */
	@Test public void testClose()
	{
	    System.out.println("Test close..");
	    gripper.close();
		assertTrue( getState() == Gripper.stateType.CLOSED );
	}

	/**
	 * Test method for {@link device.Gripper#lift()}.
	 */
	@Test public void testLift()
	{
	    System.out.println("Test lift..");
	    gripper.lift();
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Test method for {@link device.Gripper#release()}.
	 */
	@Test public void testRelease()
	{
	    System.out.println("Test release..");
	    gripper.release();
		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	/**
	 * Test method for {@link device.Gripper#getState()}.
	 */
	public Gripper.stateType getState()
	{
		Gripper.stateType state = gripper.getState();
		
		System.out.println("Gripper state: "+state);
		
		return state;
	}
	public void testLiftWithObject()
	{
		gripper.liftWithObject();
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(GripperTest.class); 
    }
}
