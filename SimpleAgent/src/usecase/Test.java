package usecase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import robot.GripperRobot;
import data.Position;
import device.Device;
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
			// Init the robot clients
			plannerClient = new RobotClient("localhost", 6668);
			roboClient = new RobotClient("localhost", 6667);
			// Start the robot clients
			plannerClient.runThreaded();
			roboClient.runThreaded();
			// Create a Device containing all the clients devices
			Device gripperDevices = new Device( new Device[]{roboClient, plannerClient} );
			// Init the robot with the devices
			pion = new GripperRobot(gripperDevices);
			pion.setPosition(new Position(-3,-5,Math.toRadians(90)));
			for(int i=0; i<10; i++) {
				System.out.println(
						pion.getPosition().toString());
				Thread.sleep(1000);
			}
			// Start the robot
			pion.runThreaded();
			in.readLine();
				pion.shutdown();
		roboClient.shutdown();
		plannerClient.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
