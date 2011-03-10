package test;

import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import robot.NavRobot;

import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;

public class NavRobotTest
{
	static NavRobot robot;
	static DeviceNode deviceNode;
	
    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        int port = 6665;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1) );
        devList.add( new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,-1) );
        devList.add( new Device(IDevice.DEVICE_LOCALIZE_CODE,host,port+1,-1) );
        devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,-1));

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        hostList.add(new Host(host,port+1));

        deviceNode = new DeviceNode( hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]) );
        assertNotNull(deviceNode);
        
        robot = new NavRobot(deviceNode.getDeviceListArray());
        assertNotNull(robot);
        robot.setRobotId("r0");
        
        deviceNode.runThreaded();
        assertTrue(deviceNode.isThreaded());
        
        robot.runThreaded();
        assertTrue(robot.isThreaded());
    }
    @AfterClass public static void tearDownAfterClass() throws Exception
    {
        robot.shutdown();
        assertFalse(robot.isThreaded());
        
        deviceNode.shutdown();
        assertFalse(deviceNode.isThreaded());
    }

    @Test public void testSetPosition()
    {
		Position pose = new Position(-6,-5,Math.toRadians(90));

		robot.setPosition(pose);
	}

	@Test public void testGetPosition()
	{
		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(robot.getPosition().distanceTo((new Position(-6,-5,Math.toRadians(90)))) < 1.0);
	}

	@Test public void testSetGoal()
	{
		robot.setGoal(new Position(-6.5,-2,Math.toRadians(75)));
	}

	@Test public void testGetGoal()
	{
		assertTrue(robot.getGoal().equals(new Position(-6.5,-2,Math.toRadians(75))));
	}
	
	@Test public void testGetPositionAtGoal()
	{
		try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
		
		Position robotPose = robot.getPosition();
		Position goalPose = new Position(-6.5,-2,Math.toRadians(75));
		
		System.out.println(robotPose+" "+goalPose);
		assertTrue(robot.getPosition().isNearTo(goalPose, 2, Math.toRadians(30)));
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(NavRobotTest.class); 
    }
}
