/*
 *  Player Java Client 2 Examples - BlobfinderExample.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: BlobfinderExample.java,v 1.0 2005/12/14 17:42:00 rusu Exp $
 *
 */

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.SonarInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;

public class BlobfinderExample {
	
	// define the threshold (any value under this is considered an obstacle)
	static float SONAR_THRESHOLD = 0.5f;
	// define the wheel diameter (~example for a Pioneer 3 robot)
	static float WHEEL_DIAMETER  = 24.0f;
	
	// define the default rotational speed in rad/s
	static float DEF_YAW_SPEED   = 0.30f;
	static float DEF_X_SPEED     = 0.50f;
	
	// array to hold the SONAR sensor values
	static float[] sonarValues;
	// translational/rotational speed
	static float xspeed, yawspeed;
	static float leftSide, rightSide;
	
	// the number of blobs found
	static int blobCount;
	
	public static void main (String[] args) {
		PlayerClient        robot = null;
		Position2DInterface posi  = null;
		SonarInterface      soni  = null;
		BlobfinderInterface  bfi  = null;
		
		try {
			// Connect to the Player server
			robot  = new PlayerClient ("localhost", 6665);
			posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			soni = robot.requestInterfaceSonar      (0, PlayerConstants.PLAYER_OPEN_MODE);
			bfi  = robot.requestInterfaceBlobfinder (0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println ("BlobfinderExample: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		
		robot.runThreaded (-1, -1);
		
		while (true) {
			while (!soni.isDataReady ());
			// get all SONAR values
			sonarValues = soni.getData ().getRanges ();
			
			// read and average the sonar values on the left and right side
			leftSide  = (sonarValues [1] + sonarValues [2]) / 2; // + sonarValues [3]) / 3;
			rightSide = (sonarValues [5] + sonarValues [6]) / 2; // + sonarValues [4]) / 3;
			
			leftSide = leftSide / 10;
			rightSide = rightSide / 10;
			
			// calculate the translational and rotational velocities
			xspeed = (leftSide + rightSide) / 2;
			yawspeed = (float)((leftSide - rightSide) * (180 / Math.PI) / WHEEL_DIAMETER);
			
			try { Thread.sleep (100); } catch (Exception e) { }
			
			// if the path is clear on the left OR on the right, use {x,yaw}speed
			if ((
					(sonarValues [1] > SONAR_THRESHOLD) && 
					(sonarValues [2] > SONAR_THRESHOLD) && 
					(sonarValues [3] > SONAR_THRESHOLD) )  ||
					(
							(sonarValues [4] > SONAR_THRESHOLD) && 
							(sonarValues [5] > SONAR_THRESHOLD) && 
							(sonarValues [6] > SONAR_THRESHOLD) 
					))
				posi.setSpeed (xspeed, yawspeed);
			else
				// if we have obstacles in front (both left and right), rotate
				if (sonarValues [0] < sonarValues [7])
					posi.setSpeed (0, -DEF_YAW_SPEED);	
				else
					posi.setSpeed (0, DEF_YAW_SPEED);
			
			// get the number of blobs detected
			while (!bfi.isDataReady ());
			blobCount = bfi.getData ().getBlobs_count ();
			
			if (blobCount > 0)
				for (int i = 0; i < blobCount; i++) {
					PlayerBlobfinderBlob unblob = bfi.getData ().getBlobs ()[i];
					
					int x = unblob.getX (); 
					int y = unblob.getY ();
					
					int left  = unblob.getLeft  (); 
					int right = unblob.getRight ();
					
					int top    = unblob.getTop    (); 
					int bottom = unblob.getBottom ();
					
					int area   = unblob.getArea   ();
					
					System.out.println ("Blob [" + i + "], has area: [" + area + 
							"] blob coords: ["+ right + ", " + top + "] -> " + 
							"[" + left + "," + bottom + "]" + 
							" with center at: [" + x + "," + y + "]");
				}
		}
	}
}