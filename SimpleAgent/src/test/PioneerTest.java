/**
 * 
 */
package test;

import junit.framework.TestCase;

import org.junit.Test;

import robot.IPioneer;
import robot.Pioneer;
import device.DeviceNode;

/**
 * @author sebastian
 *
 */
public class PioneerTest extends TestCase {

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
        pion.setTurnrate(0.0);
        
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getSpeed() > 0.0);
        testStop();
    }

    @Test public void testSetSpeed2() {
        pion.setSpeed(-0.4);
        pion.setTurnrate(0.0);
        
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        assertTrue(pion.getSpeed() < 0.0);
    }

    /**
     * Test method for {@link robot.Robot#setTurnrate(double)}.
     */
    @Test public void testSetTurnrate() {
        testStop();
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
    @Test public void testWallfollow() {
        pion.setCurrentState(IPioneer.StateType.LWALL_FOLLOWING);
        try { Thread.sleep(30000); } catch (InterruptedException e) { e.printStackTrace(); }

    }
    @Test public void testSetSpeed3() {
        pion.setCurrentState(IPioneer.StateType.SET_SPEED);
        testSetSpeed();
    }
    @Test public void testShutdown() {
        pion.shutdown();
        dn.shutdown();
    }

}
