package usecase;
/// @file wallfollowing.cpp
/// @author Sebastian Rockel
///
/// @mainpage Robotic Project 2009
///
/// @par Copyright
/// Copyright (C) 2009 Sebastian Rockel.
/// This program can be distributed and modified under the condition mentioning
/// the @ref author.
///
/// @par Description
/// Wall following example for Pioneer 2DX robot.
/// This is part of the robotics students project at Uni Hamburg in 2009.
/// The task was to implement sensor fusion (besides laser and sonar also
/// omnidirectional camera) to find and track a (pink) ball or when idle do
/// exploring floor and rooms (via wall following) always avoiding collisions
/// with static or dynamic obstacles.
///
/// @par Sensor fusion
/// The program depends on sensor fusion of 16 sonar rangers and a 240 degree
/// urg laser ranger (as well as an omni directional camera when enabled, see
/// makefile).
///
/// @par Source code
/// @code git clone git://github.com/buzzer/Pioneer-Project-2009.git @endcode
///
/// (Scalable) Vector Graphic formatted figures :
/// @image html PioneerShape.svg "Overview about how laser and sonar sensors are fused"
/// @image html AngleDefinition.svg "Calculating turning angle via the atan function"
/// @image latex PioneerShape.pdf "Overview about how laser and sonar sensors are fused" width=\textwidth
/// @image latex AngleDefinition.pdf "Calculating turning angle via the atan function" width=0.8\textwidth
/// Same graphics in Portable Network Graphic format (html only):
/// @image html PioneerShape.png "Overview about how laser and sonar sensors are fused"
/// @image html AngleDefinition.png "Calculating turning angle via the atan function"

//import java.text.NumberFormat;
import robot.*;
import data.Position;
import device.Device;
import device.IDevice;
import device.DeviceNode;
import device.Simulation;
import device.Tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Teamwork  {
	
	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

	public static void main (String[] args) {
		try {
			DeviceNode explDevices = new DeviceNode("localhost", 6665);
			explDevices.runThreaded();
			
			DeviceNode gripDevices = new DeviceNode("localhost", 6667);
			DeviceNode gripDevices2 = new DeviceNode("localhost", 6668);
			gripDevices.runThreaded();
			gripDevices2.runThreaded();
			Device gripperDevices = new Device( new Device[]{gripDevices, gripDevices2} );

			ExploreRobot explorer = new ExploreRobot(explDevices);
			explorer.runThreaded();
			
			NavRobot gripper = new NavRobot(gripperDevices);
			gripper.runThreaded();
			
			gripper.setPosition(new Position(-16,3,Math.toRadians(90)));

			// Testing planner
			gripper.setPosition(new Position(-3,-5,0));
			gripper.setGoal(new Position(-6,6,0));
//			// Testing Simulator
			Simulation simu = (Simulation) explDevices.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));; 
			Tracker tracker  = Tracker.getInstance(simu);
			tracker.addObject("r0", explorer);
			tracker.addObject("r1", gripper);
		
			// Wait until enter is pressed
			in.readLine();
			tracker.shutdown();
			
			explorer.shutdown();
			explDevices.shutdown();
			
			gripper.shutdown();
			gripDevices.shutdown();
			gripDevices2.shutdown();

			simu.shutdown();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
}
