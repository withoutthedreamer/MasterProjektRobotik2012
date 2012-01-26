package test.robot;

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
import device.external.IDevice;

public class SwarmRobotTest
{
	static NavRobot robot;
	static DeviceNode deviceNode;
	
    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        Host h = new Host("localhost",6665);
        int devIdx = 0;
        
        /** Get the device node */
        deviceNode = new DeviceNode(
            new Host[]
            {
                h,
                new Host(h.getHostName(),h.getPortNumber()+1)
            },
            new Device[]
            {
                new Device(IDevice.DEVICE_POSITION2D_CODE,h.getHostName(),h.getPortNumber(),devIdx),
                new Device(IDevice.DEVICE_RANGER_CODE,h.getHostName(),h.getPortNumber(),devIdx),
                new Device(IDevice.DEVICE_PLANNER_CODE,h.getHostName(),h.getPortNumber()+1,devIdx),
                new Device(IDevice.DEVICE_SIMULATION_CODE,h.getHostName(),h.getPortNumber(),-1)
            });
        
        assertNotNull(deviceNode);
        
        robot = new NavRobot(deviceNode.getDeviceListArray());
        robot.setRobotId("r0");
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
		Position pose = new Position(0,0,0);		
		robot.setPosition(pose);
	}

	@Test public void testGetPosition()
	{
		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(robot.getPosition().distanceTo((new Position(0,0,0))) < 1.0);
	}

	@Test public void testSetGoal()
	{
		robot.setGoal(new Position(5,3,Math.toRadians(75)));
	}

	@Test public void testGetGoal()
	{
		assertTrue(robot.getGoal().equals(new Position(5,3,Math.toRadians(75))));
	}
	
	@Test public void testGetPositionAtGoal()
	{
		try { Thread.sleep(20000); } catch (InterruptedException e) { e.printStackTrace(); }
		
		Position robotPose = robot.getPosition();
		Position goalPose = new Position(5,3,Math.toRadians(75));
		
		System.out.println(robotPose+" "+goalPose);
		assertTrue(robot.getPosition().isNearTo(goalPose, 2, Math.toRadians(30)));
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(SwarmRobotTest.class); 
    }
}
