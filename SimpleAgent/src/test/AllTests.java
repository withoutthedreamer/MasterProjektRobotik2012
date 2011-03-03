/**
 * 
 */
package test;

import junit.framework.Test;
import junit.framework.TestSuite;
   
/**
 * @author sebastian
 *
 */
public class AllTests
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite(AllTests.class.getName());
      
      /** Call the static method suite() in each case test */
      suite.addTest(DeviceNodeTest.suite());
      suite.addTest(RobotDeviceTest.suite());  
      suite.addTest(SimulationTest.suite());  
      suite.addTest(PositionTest.suite());  
      suite.addTest(PlannerTest.suite());  
      suite.addTest(PioneerTest.suite());  
      suite.addTest(NavRobotTest.suite());  
      suite.addTest(LocalizerTest.suite());  
      suite.addTest(GripperTest.suite());  
      suite.addTest(ExploreRobotTest.suite());  
      suite.addTest(DeviceTest.suite());
      suite.addTest(BoardTest.suite());
            
      return suite;
   }
}