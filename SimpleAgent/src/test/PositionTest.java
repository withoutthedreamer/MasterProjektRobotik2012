/**
 * 
 */
package test;

import static org.junit.Assert.*;

import org.junit.Test;

import data.Position;

/**
 * @author sebastian
 *
 */
public class PositionTest {

    /**
     * Test method for {@link data.Position#Position(double, double, double)}.
     */
    @Test
    public void testPositionDoubleDoubleDouble() {
        Position pose = new Position(1.,1.,1.);
        assertTrue(pose.getX() == 1.);
        assertTrue(pose.getY() == 1.);
        assertTrue(pose.getYaw() == 1.);
    }

    /**
     * Test method for {@link data.Position#Position()}.
     */
    @Test
    public void testPosition() {
        Position pose = new Position();
        assertTrue(pose.getX() == 0.);
        assertTrue(pose.getY() == 0.);
        assertTrue(pose.getYaw() == 0.);
    }

    /**
     * Test method for {@link data.Position#Position(data.Position)}.
     */
    @Test
    public void testPositionPosition() {
        Position pose = new Position(1.,1.,1.);
        Position pose2 = new Position(pose);
        assertTrue(pose2.getX() == 1.);
        assertTrue(pose2.getY() == 1.);
        assertTrue(pose2.getYaw() == 1.);
   }

    /**
     * Test method for {@link data.Position#setPosition(data.Position)}.
     */
    @Test
    public void testSetPositionPosition() {
        Position pose = new Position(1.,1.,1.);
        Position pose2 = new Position();
        pose2.setPosition(pose);
        
        assertTrue(pose2.getX() == 1.);
        assertTrue(pose2.getY() == 1.);
        assertTrue(pose2.getYaw() == 1.);
    }

    /**
     * Test method for {@link data.Position#setPosition(double, double, double)}.
     */
    @Test
    public void testSetPositionDoubleDoubleDouble() {
        Position pose = new Position();
        pose.setPosition(1., 1., 1.);
        
        assertTrue(pose.getX() == 1.);
        assertTrue(pose.getY() == 1.);
        assertTrue(pose.getYaw() == 1.);
   }

    /**
     * Test method for {@link data.Position#equals(data.Position)}.
     */
    @Test
    public void testEqualsPosition() {
        Position pose = new Position(1.,1.,1.);
        Position pose2 = new Position(1.,1.,1.);
        
        assertTrue(pose.equals(pose2));
    }

    /**
     * Test method for {@link data.Position#distanceTo(data.Position)}.
     */
    @Test
    public void testDistanceTo() {
        Position pose = new Position(0.,0.,0.);
        Position pose2 = new Position(1.,1.,0.);
        
        assertTrue(pose2.distanceTo(pose) == Math.sqrt(2.));
    }

    /**
     * Test method for {@link data.Position#getGlobalCoordinates(data.Position)}.
     */
    @Test
    public void testGetGlobalCoordinates() {
        Position pose = new Position(1.,1.,1.);
        Position pose2 = new Position();
        
        assertTrue(pose.getGlobalCoordinates(pose2).equals(pose));
        
        pose = new Position(1.,1.,0.);
        pose2 = new Position(pose);
        
        assertTrue(pose.getGlobalCoordinates(pose2).equals(new Position(2.,2.,0.)));
        
        pose2 = new Position(0.,0.,Math.PI/2);
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,Math.PI/2)));
        
        pose2 = new Position(0.,0.,Math.PI*1.5);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,Math.PI*(-0.5))));
        
        pose2 = new Position(0.,0.,Math.toRadians(50));
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,Math.toRadians(50))));
        
        pose2 = new Position(0.,0.,Math.PI*2.1);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,Math.PI*0.1)));
        
        pose2 = new Position(0.,0.,Math.PI*-1.5);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,Math.PI*0.5)));

        pose2 = new Position(0.,0.,0.);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,0.)));

        pose2 = new Position(0.,0.,Math.PI);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,-Math.PI)));

        pose2 = new Position(0.,0.,-Math.PI);
//        System.out.println(pose2.getGlobalCoordinates(pose));
        assertTrue(pose2.getGlobalCoordinates(pose).equals(new Position(1.,1.,-Math.PI)));
    }

}
