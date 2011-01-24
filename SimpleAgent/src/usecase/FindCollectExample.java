package usecase;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import robot.ExploreRobot;
import robot.GripperRobot;
import data.Position;
import device.Blackboard;
import device.Device;
import device.IDevice;
import device.DeviceNode;
import device.Simulation;

public class FindCollectExample {

	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

	
	public static void main (String[] args) {
		try {
			DeviceNode explDevices = new DeviceNode("localhost", 6665);
			explDevices.runThreaded();
			
			DeviceNode gripDevices = new DeviceNode("localhost", 6667);
//			DeviceNode gripDevices2 = new DeviceNode("localhost", 6668);
			gripDevices.runThreaded();
//			gripDevices2.runThreaded();
//			Device gripperDevices = new Device( new Device[]{gripDevices, gripDevices2} );

//			ExploreRobot explorer = new ExploreRobot(explDevices);	
//			GripperRobot gripper = new GripperRobot(gripperDevices);
			
			Simulation simu = (Simulation) explDevices.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));; 
			simu.initPositionOf("r0");
			simu.initPositionOf("r1");
//			gripper.setPosition(new Position(-3,-5,Math.toRadians(90)));
//			explorer.setPosition(new Position(-6,-5,Math.toRadians(90)));
//			Position simuPose = simu.getPositionOf("r0");
//			while( simu.getPositionOf("r0").equals(new Position(0,0,0)) == true ) {
//				System.err.print(".");
//			}
			
//			try { Thread.sleep(300); } catch (InterruptedException e) {	e.printStackTrace(); }
//			explorer.setPosition(simu.getPositionOf("r0"));
//			gripper.setPosition(simu.getPositionOf("r1"));

//			explorer.runThreaded();
//			gripper.runThreaded();	
			
			// Task synchronization
//			Blackboard blackb= Blackboard.getInstance(gripper);
			// wants to write notes
//			explorer.setBlackboard(blackb);
			
			
			// for modifying world
//			Simulator simu   = Simulator.getInstance("localhost", 6665);
//			blackb.setSimulation(simu);
//			blackb.runThreaded();
			
//			Tracker tracker  = Tracker.getInstance(simu, null);
//			tracker.addObject("r0", pionSB);
//			tracker.addObject("r1", pionLG);
//			while (true) {
//				Thread.sleep(1000);
//				System.out.println(pionRG.getPosition().toString());
//			}
			// Wait until enter is pressed
			in.readLine();
//			tracker.shutdown()
//			blackb.shutdown();
						
//			explorer.shutdown();
			explDevices.shutdown();
			
//			gripper.shutdown();
			gripDevices.shutdown();
//			gripDevices2.shutdown();

			simu.shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exit with errors");
		}
	}
}
