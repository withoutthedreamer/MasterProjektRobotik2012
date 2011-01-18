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
			roboClient = new RobotClient("localhost", 6667);
			roboClient.runThreaded();
			
			plannerClient = new RobotClient("localhost", 6668);
			plannerClient.runThreaded();
			
			pion = new GripperRobot(roboClient);
			pion.addDevices(plannerClient);
			pion.runThreaded();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pion.shutdown();
		plannerClient.shutdown();
		roboClient.shutdown();
	}

}
