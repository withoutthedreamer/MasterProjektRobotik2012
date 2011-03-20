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
import device.IDevice;
import device.Ranger;

/**
 * @author sebastian
 *
 */
public class RangerTest
{
	static DeviceNode deviceNode;
	static Ranger ranger;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass public static void setUpBeforeClass() throws Exception
	{
		int port = 6665;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,1) );
                
        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
                
        /** Get the device node */
        deviceNode = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
        assertNotNull(deviceNode);
        
        deviceNode.runThreaded();
        
        ranger = (Ranger) deviceNode.getDevice(new Device(IDevice.DEVICE_RANGER_CODE, null, -1, -1));
        assertNotNull(ranger);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass public static void tearDownAfterClass() throws Exception
	{
		deviceNode.shutdown();
	}

	@Test public void testRead()
	{
	    assertNotNull(ranger);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        int count = ranger.getCount();
        double[] ranges = ranger.getRanges();
        
        System.out.print(count);
       
        for (int i=0; i<count; i++)
        {
            String rString = String.format("|%4.1f", ranges[i]);
            System.out.print(rString);
        }
        
        System.out.println();
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(RangerTest.class); 
    }
}
