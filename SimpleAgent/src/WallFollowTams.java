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

import java.text.NumberFormat;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.RangerInterface;
import javaclient3.Position2DInterface;
import javaclient3.SonarInterface;
import javaclient3.structures.PlayerConstants;

public class WallFollowTams  {

	public static void main (String[] args) {
		PlayerClient        robot = null;
		Position2DInterface posi  = null;
		SonarInterface      soni  = null;
		RangerInterface		rang  = null;
		
		try {
			// Connect to the Player server and request access to Position and Sonar
			robot  = new PlayerClient ("localhost", 6665);
			posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			soni = robot.requestInterfaceSonar      (0, PlayerConstants.PLAYER_OPEN_MODE);
			rang = robot.requestInterfaceRanger		(0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println ("WallFollowerExample: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		
		robot.runThreaded (-1, -1);
		
//		// Go ahead and find a wall and align to it on the robot's left side
//		getWall (posi, soni);
//		
		while (true) {
//			// get all SONAR values and perform the necessary adjustments
//			getSonars (soni);
//			
//			// by default, just move in front
//			xSpeed   = DEF_X_SPEED;
//			yawSpeed = 0;
//			
//			// if we're getting too close to the wall with the front side...
//			if (frontSide < MAX_WALL_THRESHOLD) {
//				// back up a little bit if we're bumping in front
//				xSpeed   = -0.10f;
//				yawSpeed = - DEF_YAW_SPEED * 4;
//			} else
//				// if we're getting too close to the wall with the left side...
//				if (leftSide < MIN_WALL_THRESHOLD) {
//					// move slower at corners
//					xSpeed   = DEF_X_SPEED / 2;
//					yawSpeed = - DEF_YAW_SPEED ;
//				}
//				else
//					// if we're getting too far away from the wall with the left side...
//					if (leftSide > MAX_WALL_THRESHOLD) {
//						// move slower at corners
//						xSpeed   = DEF_X_SPEED / 2;
//						yawSpeed = DEF_YAW_SPEED;
//					}
//			
//			// Move the robot
//			posi.setSpeed (xSpeed, yawSpeed);
//			System.out.println ("Left side : [" + leftSide + "], xSpeed : [" + xSpeed + "], yawSpeed : [" + yawSpeed + "]");
			try { Thread.sleep (100); } catch (Exception e) { }
			
		}
	}
	
}
