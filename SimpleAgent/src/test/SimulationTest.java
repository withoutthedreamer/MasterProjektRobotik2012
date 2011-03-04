package test;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.Simulation;

public class SimulationTest
{	
	static Simulation simu = null;
	static DeviceNode deviceNode = null;

	@BeforeClass public static void setUpBeforeClass() throws Exception
	{
	    deviceNode = new DeviceNode("localhost", 6665); 
        assertNotNull(deviceNode);

        deviceNode.runThreaded();
        assertEquals(deviceNode.isThreaded(), true);
        
        simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
        
        assertNotNull(simu);
        assertEquals(simu.getClass(),Simulation.class);
        assertEquals(simu.isRunning(), true);
        assertEquals(simu.isThreaded(), true);	    
	}
   
	@AfterClass public static void tearDownAfterClass() throws Exception
    {
        deviceNode.shutdown();
        
        assertEquals(simu.isRunning(), false);
        assertEquals(simu.isThreaded(), false);
        assertEquals(simu.getObjListCount(), 0);
        assertEquals(simu.getIsDirtyListCount(), 0);
    }

	@Test public void testInitPositionOf()
	{
		int before = simu.getObjListCount();
		int before1 = simu.getIsDirtyListCount();
		simu.initPositionOf("r0");
		assertEquals(before+1, simu.getObjListCount());
		assertEquals(before1+1, simu.getIsDirtyListCount());
		
		before = simu.getObjListCount();
		before1 = simu.getIsDirtyListCount();
		simu.initPositionOf("r1");
		assertEquals(before+1, simu.getObjListCount());
		assertEquals(before1+1, simu.getIsDirtyListCount());
	}

	@Test public void testSetPositionOf()
	{
		Position pose = new Position (-7,-7,0);
		Position pose2 = new Position (-6,-7,0);
		
		for (int i=1; i<11; i++)
		{	
			simu.setPositionOf("r0", pose);
			assertEquals(simu.getPositionOf("r0").equals(pose), true);
					
			simu.setPositionOf("r1", pose2);
			assertEquals(simu.getPositionOf("r1").equals(pose2), true);

			simu.setPositionOf("green", new Position(pose2.getX()+1,pose2.getY(),pose2.getYaw()));
			simu.setPositionOf("black", new Position(pose2.getX()+2,pose2.getY(),pose2.getYaw()));
			simu.setPositionOf("red", new Position(pose2.getX()+3,pose2.getY(),pose2.getYaw()));
			
			simu.setPositionOf("nottExist", new Position(pose2.getX()+4,pose2.getY(),pose2.getYaw()));
			
			pose.setY(0.5+pose.getY());
			pose.setX(0.5+pose.getX());
			pose.setYaw(0.628+pose.getYaw());
			
			pose2.setY(0.5+pose2.getY());
			pose2.setX(0.5+pose2.getX());
			pose2.setYaw(0.628+pose2.getYaw());

			// Pause to verify position in gui window
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	/** To use JUnit  test suite */
	public static JUnit4TestAdapter suite()
	{ 
	   return new JUnit4TestAdapter(SimulationTest.class); 
	}
}
