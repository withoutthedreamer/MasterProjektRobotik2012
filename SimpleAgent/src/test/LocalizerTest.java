package test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.ILocalizeListener;
import device.Localize;
import device.Position2d;
import device.Simulation;

public class LocalizerTest
{	
	static Localize localizer;
	static DeviceNode deviceNode;
	static Simulation simu;
	static Position2d motor;
	static Position curPosition;
	static boolean isNewPose;
	static boolean shutdown;

    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        int port = 6665;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1) );
        devList.add( new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,0) );
        devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,1) );
        devList.add( new Device(IDevice.DEVICE_LOCALIZE_CODE,host,port+1,0) );

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        hostList.add(new Host(host,port+1));
        
        /** Get the device node */
        deviceNode = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
        assertNotNull(deviceNode);
        
        deviceNode.runThreaded();
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
    @AfterClass public static void tearDownAfterClass() throws Exception
    {
        shutdown = false;
        while (shutdown == false)
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

        motor.setSpeed(0.0);
        
        deviceNode.shutdown();
        assertFalse(localizer.isRunning());
    }

	@Test public void testSetPosition()
	{
		Position pose = new Position(-6,-5,0);
		
		simu.setPositionOf("r0", pose);
		while(simu.getPositionOf("r0").distanceTo(pose) > 1.0);
		
		localizer.setPosition(pose);		
		
	}
	@Test public void testGetPosition()
	{
		isNewPose = false;
		while (isNewPose == false)
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(curPosition.distanceTo(new Position(-6,-5,0)) < 1.0);
	}
	
	@Test public void testGetPositionLoop()
	{
		motor.setSpeed(0.4);
		motor.setTurnrate(0.04);
		motor.syncSpeed();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() {
				shutdown = true;
			}
		}, 20000);
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(LocalizerTest.class); 
    }
}
