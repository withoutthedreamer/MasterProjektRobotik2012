/**
 * 
 */
package test;

import static org.junit.Assert.*;

import org.junit.Test;

import robot.Pioneer;
import device.DeviceNode;

/**
 * @author sebastian
 *
 */
public class PioneerTest {

    static DeviceNode dn;
    static Pioneer pion;
    
    @Test public void testInit() {
        dn = new DeviceNode("localhost", 6665);
        dn.runThreaded();
        
        pion = new Pioneer(dn);
        assertNotNull(pion);
        
        pion.runThreaded();
        assertTrue(pion.isThreaded());
    }
    /**
     * Test method for {@link robot.Robot#setSpeed(double)}.
     */
    @Test public void testSetSpeed() {
        pion.setSpeed(0.4);
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getSpeed() > 0.0);
        testStop();
    }

    @Test public void testSetSpeed2() {
        pion.setSpeed(-0.4);
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getSpeed() < 0.0);
        testStop();
    }

    /**
     * Test method for {@link robot.Robot#setTurnrate(double)}.
     */
    @Test public void testSetTurnrate() {
        pion.setTurnrate(0.4);
        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getTurnrate() > 0.0);
    }
    @Test public void testSetTurnrate2() {
        pion.setTurnrate(-0.4);
        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getTurnrate() < 0.0);
    }
    @Test public void testStop() {
        pion.stop();
        assertTrue(pion.getSpeed() == 0.0);
    }
    @Test public void testShutdown() {
        pion.shutdown();
        dn.shutdown();
    }

}
