package test;

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
import device.Simulation;

public class NavRobotTest
{
	static NavRobot robot;
	static DeviceNode deviceNode;
	
    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        deviceNode = new DeviceNode( new Host[]{new Host("localhost",6665), new Host("localhost",6666)}, (Device[]) null );
        assertNotNull(deviceNode);
        
        robot = new NavRobot(deviceNode.getDeviceListArray());
        assertNotNull(robot);
        
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
		
		Simulation simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		simu.setPositionOf("r0", pose);
		
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
