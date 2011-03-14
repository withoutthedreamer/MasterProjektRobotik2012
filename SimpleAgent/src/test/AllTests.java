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

		/** Unit tests */
		suite.addTest(PositionTest.suite());  
		suite.addTest(DeviceTest.suite());
		suite.addTest(BoardTest.suite());
		suite.addTest(BlobfinderBlobTest.suite());

		/** System tests */
		suite.addTest(DeviceNodeTest.suite());
		suite.addTest(RobotDeviceTest.suite());  
		suite.addTest(SimulationTest.suite());  
        suite.addTest(LocalizerTest.suite());  
		suite.addTest(PlannerTest.suite());  
		suite.addTest(PioneerTest.suite());  
		suite.addTest(NavRobotTest.suite()); 
		suite.addTest(BlobfinderTest.suite());
        suite.addTest(ExploreRobotTest.suite());
		suite.addTest(GripperTest.suite());  

		return suite;
	}
}