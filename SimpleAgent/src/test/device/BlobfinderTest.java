/**
 * 
 */
package test.device;

import static org.junit.Assert.*;

import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.BlobfinderBlob;
import data.Board;
import data.BoardObject;
import data.Host;
import data.Position;
import device.Blobfinder;
import device.Device;
import device.DeviceNode;
import device.IBlobfinderListener;
import device.IDevice;
import device.Simulation;

/**
 * @author sebastian
 *
 */
public class BlobfinderTest
{
    static DeviceNode dn;
    static Blobfinder bf;
    static Simulation si;
    static Position robotPose = new Position(-6,6,0);
    static IBlobfinderListener cb;
    static Board bb;
    Position[] blobPoses = new Position[]
    {
        new Position(robotPose.getX()+1,robotPose.getY(),robotPose.getYaw()),
        new Position(robotPose.getX()+2,robotPose.getY()-1,robotPose.getYaw()),
        new Position(robotPose.getX()+2,robotPose.getY()-0.5,robotPose.getYaw())
    };
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass public static void setUpBeforeClass() throws Exception
    {
        int port = 6669;
        String host = "localhost";
        
        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_BLOBFINDER_CODE,host,port,0) );
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,-1,-1) );

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        hostList.add(new Host(host,6665));
        
        /** Get the device node */
        dn = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
        dn.runThreaded();
        
        bf = (Blobfinder) dn.getDevice(new Device(IDevice.DEVICE_BLOBFINDER_CODE,null,-1,-1));
        assertNotNull(bf);
        
        si = (Simulation) dn.getDevice(new Device (IDevice.DEVICE_SIMULATION_CODE,null,-1,-1));
        assertNotNull(si);
        
        bb = new Board();
        assertNotNull(bb);

        /** Set robot */
        si.setPositionOf("r2", robotPose);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass public static void tearDownAfterClass() throws Exception
    {
        System.out.print("See "+bf.getCount()+ " blob(s)");
        System.out.println(", know "+bf.getBlobs().size()+" blob(s)");

        assertTrue(bf.removeBlobListener(cb));

        dn.shutdown();
        bb.clear();        
    }

    /**
     * @throws java.lang.Exception
     */
    @Before public void setUp() throws Exception
    {
        /** Set blobs */
        si.setPositionOf("black", blobPoses[0]);
        si.setPositionOf("red", blobPoses[1]);
        si.setPositionOf("green", blobPoses[2]);
        
//        for (int i=0; i<blobPoses.length; i++)
//            System.out.println(blobPoses[i]);
    }

    /**
     * @throws java.lang.Exception
     */
    @After public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link device.Blobfinder#addBlobListener(device.IBlobfinderListener)}.
     */
    @Test public void testAddBlobListener()
    {
        cb = new IBlobfinderListener()
        {
            @Override public void newBlobFound(BlobfinderBlob newBlob)
            {
                /** Board object */
                if (bb.getObject(newBlob.getColorString()) == null)
                {
                    Position blobPose = new Position(newBlob.getRange(),0,newBlob.getAngle(Math.PI/2,80));
                    System.out.print(""+newBlob+",\t");
                    Position globPose = blobPose.getCartesianCoordinates().getGlobalCoordinates(robotPose); 
                    System.out.print("global pose: " + globPose );
                    
                    
                    BoardObject bo = new BoardObject();
                    bo.setTopic(""+newBlob.getClass());
                    bo.setPosition(blobPose);

                    bb.addObject(newBlob.getColorString(), bo);
                    
                    if (newBlob.getColorString().equals("black"))
                        System.out.println("\tplanar delta: "+globPose.distanceTo(blobPoses[0]));
                    else
                        if (newBlob.getColorString().equals("red"))
                            System.out.println("\tplanar delta: "+globPose.distanceTo(blobPoses[1]));
                        else
                            if (newBlob.getColorString().equals("green"))
                            System.out.println("\tplanar delta: "+globPose.distanceTo(blobPoses[2]));
                }
            }
        };
        
        /** Add blob callback */
        assertTrue(bf.addBlobListener(cb));
    }

    /**
     * Test method for {@link device.Blobfinder#Blobfinder(device.DeviceNode, device.Device)}.
     */
    @Test public void testBlobfinder()
    {
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        si.setPositionOf("green", new Position(robotPose.getX()+3,robotPose.getY(),robotPose.getYaw()));
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    /**
     * Test method for {@link device.Blobfinder#getBlobs()}.
     */
    @Test public void testGetBlobs()
    {
        assertTrue(bf.getBlobs().size() > 0);
    }

    /**
     * Test method for {@link device.Blobfinder#getCount()}.
     */
    @Test public void testGetCount()
    {
        assertTrue(bf.getCount() > 0);
    }

    /**
     * Test method for {@link device.Blobfinder#removeBlobListener(device.IBlobfinderListener)}.
     */
    @Test public void testRemoveBlobListener()
    {
    }
   
    /** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(BlobfinderTest.class); 
    }
}
