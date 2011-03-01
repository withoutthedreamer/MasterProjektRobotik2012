/**
 * 
 */
package test;

import junit.framework.TestCase;

import org.junit.Test;

import robot.ExploreRobot;
import data.BlobfinderBlob;
import data.Position;
import device.DeviceNode;
import device.IBlobfinderListener;

/**
 * @author sebastian
 *
 */
public class ExploreRobotTest extends TestCase
{
    static DeviceNode dn;
    static ExploreRobot robot;
    
    /**
     * Test method for {@link robot.ExploreRobot#ExploreRobot(device.Device)}.
     */
    @Test public void testExploreRobot() {
        dn = new DeviceNode(new Object[]{"localhost", 6665, "localhost", 6666});
        dn.runThreaded();
        
        robot = new ExploreRobot(dn);
        assertNotNull(robot);
        
        robot.runThreaded();
        assertTrue(robot.isThreaded());
    }

    /**
     * Test method for {@link robot.ExploreRobot#blobsearch()}.
     */
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

    @Test public void testShutdown()
    {
        robot.stop();
        robot.shutdown();
        dn.shutdown();
    }

}
