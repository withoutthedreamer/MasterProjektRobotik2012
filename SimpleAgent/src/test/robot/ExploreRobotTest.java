/**
 * 
 */
package test.robot;

import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import robot.ExploreRobot;
import data.BlobfinderBlob;
import data.Host;
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
        int port = 6669;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,-1) );
        devList.add( new Device(IDevice.DEVICE_SONAR_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_BLOBFINDER_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,-1,-1) );
        devList.add( new Device(IDevice.DEVICE_LOCALIZE_CODE,host,port+1,0) );
        /** TODO dummy that player read() will not block on shutdown */
        devList.add( new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,0) );

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        hostList.add(new Host(host,port+1));
        hostList.add(new Host(host,6665));
        
        /** Get the device node */
        dn = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
        dn.runThreaded();
        
        robot = new ExploreRobot(dn.getDeviceListArray());
        assertNotNull(robot);
        robot.setRobotId("r2");
        
        robot.runThreaded();
        assertTrue(robot.isThreaded());
        
        /** Set the robots init pose */
        robot.setPosition(new Position(-6.,6.,0.));
        /** Correct it in the simulation */
        Simulation simu = (Simulation) dn.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE,null,-1,-1));
        assertNotNull(simu);
        
        /** Position some blobs */
        simu.setPositionOf("green", new Position(0,5,0));
        simu.setPositionOf("black", new Position(-4,6,0));
        simu.setPositionOf("red", new Position(-4,4,0));
        
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