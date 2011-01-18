package usecase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import robot.GripperRobot;
import device.RobotClient;



public class Test {

	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		GripperRobot pion = null;
		RobotClient roboClient = null, plannerClient = null;
		try {
			plannerClient = new RobotClient("localhost", 6668);
			roboClient = new RobotClient("localhost", 6667);

			roboClient.addDevicesOf(plannerClient);
			plannerClient = null;
			
			roboClient.runThreaded();
			
			pion = new GripperRobot(roboClient);
			pion.runThreaded();
		}
		catch (Exception e)	{
			e.printStackTrace();
		} try {
			in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pion.shutdown();
		roboClient.shutdown();
	}

}
