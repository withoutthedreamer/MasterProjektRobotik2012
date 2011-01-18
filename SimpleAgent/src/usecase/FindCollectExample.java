package usecase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import robot.ExploreRobot;
import robot.GripperRobot;
import data.Position;
import device.RobotClient;

public class FindCollectExample {

	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

	
	public static void main (String[] args) {
		try {
			RobotClient explDevices = new RobotClient("localhost", 6665);
			explDevices.runThreaded();
			
			RobotClient gripDevices = new RobotClient("localhost", 6667);
			gripDevices.runThreaded();
			
			ExploreRobot explorer = new ExploreRobot(explDevices);
			explorer.runThreaded();
			
			RobotClient gripDevices2 = new RobotClient("localhost", 6668);
//			gripDevices2.runThreaded();
			gripDevices.addDevicesOf(gripDevices2);
			gripDevices2 = null;
			
			GripperRobot gripper = new GripperRobot(gripDevices);
//			gripper.addDevices(gripDevices2);
			gripper.runThreaded();
			
			gripper.setPosition(new Position(-16,3,Math.toRadians(90)));
			
			// Task synchronization
//			Blackboard blackb= Blackboard.getInstance(pionRG);
			// wants to write notes
//			pionRsB.setBlackboard(blackb);
			
			// for modifying world
//			Simulator simu   = Simulator.getInstance("localhost", 6665);
//			blackb.setSimulation(simu);
//			Tracker tracker  = Tracker.getInstance(simu, null);
//			tracker.addObject("r0", pionSB);
//			tracker.addObject("r1", pionLG);
//			while (true) {
//				Thread.sleep(1000);
//				System.out.println(pionRG.getPosition().toString());
//			}
			// Wait until enter is pressed
			try {
				in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			tracker.shutdown()
//			blackb.shutdown();
			
			explorer.shutdown();
			explDevices.shutdown();
			
			gripper.shutdown();
			gripDevices.shutdown();
//			gripDevices2.shutdown();

//			simu.shutdown();
			
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("Exit with errors");
		}
	}
}
