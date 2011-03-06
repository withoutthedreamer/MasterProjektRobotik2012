/**
 * 
 */
package test;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import robot.ExploreRobot;
import data.BlobfinderBlob;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IBlobfinderListener;
import device.IDevice;
import device.Simulation;

/**
 * @author sebastian
 *
 */
public class ExploreRobotTest
{
    static DeviceNode dn;
    static ExploreRobot robot;
    
    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        dn = new DeviceNode(new Object[]{"localhost", 6669, "localhost", 6670});
        dn.runThreaded();
        
        robot = new ExploreRobot(dn);
        assertNotNull(robot);
        
        robot.runThreaded();
        assertTrue(robot.isThreaded());
        
        /** Set the robots init pose */
        robot.setPosition(new Position(-6.,6.,0.));
        /** Correct it in the simulation */
        DeviceNode simuDev = new DeviceNode("localhost", 6665);
        simuDev.runThreaded();
        Simulation simu = (Simulation) simuDev.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE,null,-1,-1));
        simu.setPositionOf("r2", new Position(-6,6,0));
        /** Position some blobs */
        simu.setPositionOf("green", new Position(0,5,0));
        simu.setPositionOf("black", new Position(-4,6,0));
        simu.setPositionOf("red", new Position(-4,4,0));
        simuDev.shutdown();
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
      
    }
    @AfterClass public static void tearDownAfterClass() throws Exception
    {
        robot.stop();
        robot.shutdown();
        dn.shutdown();
    }

    @Test public void testBlobsearch()
    {
        /** Add blob callback */
        if (robot.getBloFi() != null)
        {
            robot.getBloFi().addBlobListener(new IBlobfinderListener()
            {
                @Override public void newBlobFound(BlobfinderBlob newBlob)
                {
                    if (newBlob != null)
                    {
                        Position blobPose = new Position(newBlob.getRange(),0,newBlob.getAngle(Math.PI/2,80));
                        Position robotPose = robot.getPosition();
                        
                        System.out.print(""+newBlob+",\t");
                        
                        System.out.print("global pose: " +
                                blobPose.getCartesianCoordinates().getGlobalCoordinates(robotPose) );
                        
                        System.out.println(",\t robot pose: "+robotPose);
                    }
                }
            });
        }
        
        robot.setWallfollow();
        try { Thread.sleep(50000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    /** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(ExploreRobotTest.class); 
    }
}