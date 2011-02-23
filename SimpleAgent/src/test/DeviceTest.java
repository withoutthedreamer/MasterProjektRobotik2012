/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import device.Device;

/**
 * @author sebastian
 *
 */
public class DeviceTest {
    
    static Device dev;

    /**
     * Test method for {@link device.Device#Device()}.
     */
    @Test
    public void testDevice() {
        dev = new Device();
        assertNotNull(dev);
        assertTrue(dev.getDeviceList().isEmpty());
    }

    /**
     * Test method for {@link device.Device#Device(int, java.lang.String, int, int)}.
     */
    @Test
    public void testAddDevices() {

        dev.getDeviceList().add(new Device());
        assertFalse(dev.getDeviceList().isEmpty());
        
        dev.getDeviceList().add(new Device(1,"localhost",815,1));
    }

    /**
     * Test method for {@link device.Device#runThreaded()}.
     */
    @Test
    public void testRunThreaded() {
        
        dev.runThreaded();
        assertTrue(dev.isThreaded());

        Iterator<Device> it = dev.getDeviceList().iterator();
        while(it.hasNext()) {
            assertTrue(it.next().isThreaded());
        }
        
    }

    /**
     * Test method for {@link device.Device#getDeviceList()}.
     */
    @Test
    public void testGetDeviceList() {
        assertFalse(dev.getDeviceList().isEmpty());
    }

    /**
     * Test method for {@link device.Device#getDevice(device.Device)}.
     */
    @Test
    public void testGetDevice() {
        assertNotNull( dev.getDevice(new Device(1,"localhost",-1,-1)) );
    }

    /**
     * Test method for {@link device.Device#getHost()}.
     */
    @Test
    public void testGetHost() {
        assertTrue( dev.getDevice(new Device(1,"localhost",-1,-1)).getHost().equals("localhost") );
    }

    /**
     * Test method for {@link device.Device#getPort()}.
     */
    @Test
    public void testGetPort() {
        assertTrue( dev.getDevice(new Device(1,"localhost",-1,-1)).getPort() == 815);
    }

    /**
     * Test method for {@link device.Device#getName()}.
     */
    @Test
    public void testGetName() {
        assertTrue( dev.getDevice(new Device(1,"localhost",-1,-1)).getName() == 1);
    }

    /**
     * Test method for {@link device.Device#getDeviceNumber()}.
     */
    @Test
    public void testGetDeviceNumber() {
        assertTrue( dev.getDevice(new Device(1,"localhost",-1,-1)).getDeviceNumber() == 1);
    }

    /**
     * Test method for {@link device.Device#shutdown()}.
     */
    @Test
    public void testShutdown() {
        dev.shutdown();
        assertFalse(dev.isThreaded());
    }

}
