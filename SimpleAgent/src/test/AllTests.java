/**
 * 
 */
package test;

import test.data.BlobfinderBlobTest;
import test.data.BoardTest;
import test.data.PositionTest;
import test.device.BlobfinderTest;
import test.device.DeviceNodeTest;
import test.device.DeviceTest;
import test.device.GripperTest;
import test.device.LocalizerTest;
import test.device.PlannerTest;
import test.device.RangerTest;
import test.device.RobotDeviceTest;
import test.device.SimulationTest;
import test.robot.ExploreRobotTest;
import test.robot.NavRobotTest;
import test.robot.PioneerTest;
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
		suite.addTest(RangerTest.suite());
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