package usecase;
/*
 *  Player Java Client 2 - Javaclient2Test.java
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
 * $Id: Javaclient2Test.java,v 1.0 2006/03/01 11:48:25 veedee Exp $
 *
 */
import java.text.FieldPosition;
import java.text.NumberFormat;

import javaclient3.*;
import javaclient3.structures.*;
import javaclient3.structures.blobfinder.*;
import javaclient3.structures.fiducial.*;
import javaclient3.structures.laser.*;
import javaclient3.structures.planner.*;
import javaclient3.structures.position3d.*;
import javaclient3.structures.ptz.*;
import javaclient3.structures.simulation.*;
import javaclient3.structures.sonar.*;
import javaclient3.structures.gripper.*;
import javaclient3.structures.localize.*;
import javaclient3.structures.rfid.*;

/**
 * Basic tests for a variety of interfaces. Just comment the ones that you don't
 * want to activate.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class Javaclient3Test {
	
	static NumberFormat fmt = NumberFormat.getInstance ();
	
	public static void main(String[] args) throws PlayerException {
		//System.setProperty ("PlayerClient.debug", "true");
		
		PlayerClient        robot = null;
		Position2DInterface p2di  = null;
		LaserInterface      lasi  = null;
		SimulationInterface simi  = null;
		MapInterface        mapi  = null;
		LogInterface        logi  = null;
		SonarInterface      soni  = null;
		PtzInterface        ptzi  = null;
		BlobfinderInterface bfli  = null;
		FiducialInterface   fidi  = null;
		GripperInterface    grii  = null;
		
		LocalizeInterface   loci  = null;
		PlannerInterface    plni  = null;
		
		RFIDInterface       rfid  = null;
		
		try {
			robot = new PlayerClient                 ("localhost", 6665);
			lasi  = robot.requestInterfaceLaser      (0, PlayerConstants.PLAYER_OPEN_MODE);
			p2di  = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			simi  = robot.requestInterfaceSimulation (0, PlayerConstants.PLAYER_OPEN_MODE);
			logi  = robot.requestInterfaceLog        (0, PlayerConstants.PLAYER_OPEN_MODE);
			
			mapi    = robot.requestInterfaceMap        (0, PlayerConstants.PLAYER_OPEN_MODE);
			
			soni    = robot.requestInterfaceSonar      (0, PlayerConstants.PLAYER_OPEN_MODE);
			ptzi    = robot.requestInterfacePtz        (0, PlayerConstants.PLAYER_OPEN_MODE);
			bfli    = robot.requestInterfaceBlobfinder (0, PlayerConstants.PLAYER_OPEN_MODE);
			fidi    = robot.requestInterfaceFiducial   (0, PlayerConstants.PLAYER_OPEN_MODE);
			grii    = robot.requestInterfaceGripper    (0, PlayerConstants.PLAYER_OPEN_MODE);
			
			loci    = robot.requestInterfaceLocalize   (0, PlayerConstants.PLAYER_OPEN_MODE);
			plni    = robot.requestInterfacePlanner    (0, PlayerConstants.PLAYER_OPEN_MODE);
			
			rfid    = robot.requestInterfaceRFID       (0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println ("Javaclient test: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		
		int i = 0;
		
		//try { Thread.sleep (2000); } catch (Exception e) { e.printStackTrace(); }
		
		robot.runThreaded (-1, -1);
//		robot.setNotThreaded ();
		
		// --[ Test Log
		logi.setFileName ("test.txt");
		// --]
		
		// --[ Test Simulation
		testSimulation (simi);
		// --]
		
		// --[ Test Position2D
		PlayerPose vel = new PlayerPose ();
		vel.setPa (0.1f);
//		p2di.setPosition (vel, 1);
		// --]
		
		// --[ Test Localize/Planner
		// initial values for the covariance matrix (c&p example from Player)
		double cov[] = {
				250,
				250,
				(Math.PI / 6.0) * (Math.PI / 6.0) * 180 / Math.PI * 3600 * 180
						/ Math.PI * 3600 };
		// set the initial guessed pose for localization (AMCL)
		PlayerLocalizeSetPose plsp = new PlayerLocalizeSetPose ();
		// set the mean values to 0,0,0
		plsp.setMean (new PlayerPose ());
		plsp.setCov (cov);
		loci.setPose (plsp);
		// set a new goal in the planner 
		PlayerPose goal = new PlayerPose ();
		goal.setPx (-4);
		goal.setPy (0);
		goal.setPa (1);
		plni.setGoal (goal);
		// --]
		
		while (true)
		{
			//robot.requestData ();
//			robot.readAll ();
			
			// --[ Test RFID
			if (rfid.isDataReady ())
				System.err.print (decodeRFIDData (rfid));
			// --]
			
			// --[ Test Planner
			if (plni.isDataReady ()) {
				PlayerPlannerData ppd = plni.getData ();
				System.err.println (ppd.getWaypoints_count ());
			}
			// --]
			
			testMap        (mapi);
//			testGripper    (grii);
			testFiducial   (fidi);
			testBlobfinder (bfli);
			testPtz        (ptzi);
			testSonar      (soni);
//			testPosition2D (p2di);
			testLaser      (lasi);
			
			try { Thread.sleep (100); } catch (Exception e) { e.printStackTrace(); }
			i++;
			
			if (i == -1)	// modify -1 to a number if you wish to terminate
			        	// after X iterations
				break;
		}
		robot.close ();
	}
	
	public static void testMap (MapInterface mapi) {
		mapi.requestMapInformation ();
		if (mapi.isDataReady ())
			System.err.println ("Map resolution: "
					+ mapi.getData ().getWidth () + "x"
					+ mapi.getData ().getHeight () + " with scale: "
					+ mapi.getData ().getScale ());
	}
	
	public static void testSimulation (SimulationInterface simi) {
		simi.get2DPose ("p0");
		if (simi.isPose2DReady ()) {
			PlayerSimulationPose2dReq pose = simi.getSimulationPose2D ();
			System.err.println (writePose (pose));
		}

		PlayerPose pp = new PlayerPose ();
		pp.setPx (1);
		pp.setPy (1);
		pp.setPa (1);
		simi.set2DPose ("p0", pp);
	}
	
//	public static void testGripper (GripperInterface grii) {
//		grii.setGripper (2, 4);
//		if (grii.isDataReady ())
//			System.err.println ("Beams: " + grii.getData ().getBeams ()
//					+ ", State: " + grii.getData ().getState ());
//		grii.queryGeometry ();
//		if (grii.isGeomReady ())
//			System.err.println (decodeGripperGeom (grii));
//	}

	public static void testFiducial (FiducialInterface fidi) {
		fidi.queryGeometry ();
		if (fidi.isGeomReady ())
			System.err.println (decodeFiducialGeom (fidi));
		fidi.queryFOV ();
		if (fidi.isFOVReady ())
			System.err.println ("FOV: [" + fidi.getFOV ().getMin_range ()
					+ ", " + fidi.getFOV ().getMax_range () + ", "
					+ fidi.getFOV ().getView_angle () + "]");
		fidi.setFiducialVal (1);
		fidi.queryFiducialVal ();
		if (fidi.isIDReady ())
			System.err.println ("ID: " + fidi.getID ());
		if (fidi.isDataReady ())
			System.err.println (decodeFiducialData (fidi));
	}

	public static void testBlobfinder (BlobfinderInterface bfli) {
		if (bfli.isDataReady ())
			System.err.println (decodeBlobfinderData (bfli));
	}

	public static void testPtz (PtzInterface ptzi) {
		PlayerPtzCmd ppc = new PlayerPtzCmd ();
		ppc.setPan (5);
		ppc.setTilt (3);
		for (int i = 0; i < 10; i++) {
			ppc.setZoom (i);
			ptzi.setPTZ (ppc);
		}
		ptzi.setPTZ (ppc);

		if (ptzi.isDataReady ())
			System.err.println (decodePtzData (ptzi));
	}

	public static void testSonar (SonarInterface soni) {
		if (soni.isDataReady ())
			System.err.println (decodeSonarData (soni));
		soni.queryGeometry ();
		if (soni.isGeomReady ()) {
			PlayerSonarGeom psg = soni.getGeom ();
			System.err.println ("Sonar poses: " + psg.getPoses_count ());
			for (int i = 0; i < psg.getPoses_count (); i++)
				System.err.print ("[" + psg.getPoses ()[i].getPx () + ", "
						+ psg.getPoses ()[i].getPy () + ", "
//						+ psg.getPoses ()[i].getPa () + "]"
						);
			System.err.println ();
		}
	}

//	public static void testPosition2D (Position2DInterface p2di) {
//		p2di.queryGeometry ();
//		if (p2di.isDataReady ()) {
//			PlayerPosition2dData pp2d = p2di.getData ();
//
//			System.err.println ("State: [ pos:[" + pp2d.getPos ().getPx ()
//					+ ", " + pp2d.getPos ().getPy () + ", "
//					+ pp2d.getPos ().getPa () + "]," + " vel:["
//					+ pp2d.getVel ().getPx () + ", " + pp2d.getVel ().getPy ()
//					+ ", " + pp2d.getVel ().getPa () + "], stall:"
//					+ pp2d.getStall () + " ]");
//		}
//		if (p2di.isGeomReady ()) {
//			PlayerPosition2dGeom pp2g = p2di.getGeom ();
//
//			System.err.println ("Geom: [" + pp2g.getPose ().getPx () + ", "
//					+ pp2g.getPose ().getPy () + ", "
//					+ pp2g.getPose ().getPa () + "] ");
//		}
//	}
	
	public static void testLaser (LaserInterface lasi) {
		lasi.queryGeometry ();
		if (lasi.isReadyPlgeom ())
			System.err.println (decodeLaserGeometry (lasi));
		lasi.getScanProperties ();
		if (lasi.isReadyPlconfig ())
			System.err.println (decodeLaserConfig (lasi));
		if (lasi.isDataReady ())
			System.err.println (decodeLaserData (lasi));
	}

	public static String decodeSonarData (SonarInterface si) {
		String out = "\nSonar ranges: \n";
		for (int i = 0; i < si.getData ().getRanges_count (); i++) {
			out += " [" + align (fmt, i + 1, 2) + "] = "
					+ align (fmt, si.getData ().getRanges ()[i], 5);
			if (((i + 1) % 8) == 0)
				out += "\n";
		}
		return out;
	}

	public static String decodeLaserData (LaserInterface li) {
		String out = "\nLaser values: \n";
		for (int i = 0; i < li.getData ().getRanges_count (); i++) {
			out += " [" + align (fmt, i + 1, 2) + "] = "
					+ align (fmt, li.getData ().getRanges ()[i], 5);
			if (((i + 1) % 8) == 0)
				out += "\n";
		}
		return out;
	}

	static String align (NumberFormat fmt, float n, int sp) {
		StringBuffer buf = new StringBuffer ();
		FieldPosition fpos = new FieldPosition (NumberFormat.INTEGER_FIELD);
		fmt.format (n, buf, fpos);
		for (int i = 0; i < sp - fpos.getEndIndex (); ++i)
			buf.insert (0, ' ');
		return buf.toString ();
	}

	static String writePose (PlayerSimulationPose2dReq pose) {
		return "Pose for [" + new String (pose.getName ()) + "]: ("
				+ pose.getPose ().getPx () + ", " + pose.getPose ().getPy ()
				+ ", " + pose.getPose ().getPa () + ")";
	}

	static String decodeLaserGeometry (LaserInterface lasi) {
		PlayerLaserGeom plg = lasi.getPlayerLaserGeom ();
		return "Laser geometry: pose: [" + plg.getPose ().getPx () + ", "
				+ plg.getPose ().getPy () + ", " + plg.getPose ().getPa ()
				+ "], size: [" + plg.getSize ().getSl () + ", "
				+ plg.getSize ().getSw () + "]";
	}

	static String decodeLaserConfig (LaserInterface lasi) {
		PlayerLaserConfig plc = lasi.getPlayerLaserConfig ();
		return "Laser configuration: \n" + "MinAngle:   " + plc.getMin_angle ()
				+ ", " + "MaxAngle:   " + plc.getMax_angle () + ", "
				+ "MaxRange:   " + plc.getMax_range () + ", " + "RangeRes:   "
				+ plc.getRange_res () + ", " + "Resolution: "
				+ plc.getResolution () + ", " + "Intensity : "
				+ plc.getIntensity ();
	}

	static String decodePtzData (PtzInterface ptzi) {
		PlayerPtzData ppdata = ptzi.getData ();
		return "PTZ data: \n" + "Pan:       " + ppdata.getPan () + "\n"
				+ "Tilt:      " + ppdata.getTilt () + "\n" + "Zoom:      "
				+ ppdata.getZoom () + "\n" + "PanSpeed:  "
				+ ppdata.getPanspeed () + "\n" + "TiltSpeed: "
				+ ppdata.getTiltspeed () + "\n";
	}

	static String decodeBlobfinderData (BlobfinderInterface blfi) {
		PlayerBlobfinderData pbdata = blfi.getData ();
		String s = "Blobfinder data: [" + pbdata.getWidth () + ", "
				+ pbdata.getHeight () + "] -> " + pbdata.getBlobs_count ()
				+ "\n";
		for (int i = 0; i < pbdata.getBlobs_count (); i++) {
			PlayerBlobfinderBlob pbb = pbdata.getBlobs ()[i];
			s += "ID:    " + pbb.getId () + "\n" + "Color: " + pbb.getColor ()
					+ "\n" + "Area:  " + pbb.getArea () + "\n" + "X, Y:  ["
					+ pbb.getX () + ", " + pbb.getY () + "]\n"
					+ "Top-Left, Right-Bottom:  [" + pbb.getTop () + ", "
					+ pbb.getLeft () + " -> " + pbb.getRight () + ","
					+ pbb.getBottom () + "]\n" + "Range:  " + pbb.getRange ()
					+ "\n";
		}
		return s;
	}

	static String decodeFiducialData (FiducialInterface fidi) {
		PlayerFiducialData pfdata = fidi.getData ();
		String s = "Fiducial data: -> " + pfdata.getFiducials_count () + "\n";
		for (int i = 0; i < pfdata.getFiducials_count (); i++) {
			PlayerFiducialItem pfi = pfdata.getFiducials ()[i];
			s += "   Pose : [" + pfi.getPose ().getPx () + ", "
					+ pfi.getPose ().getPy () + ", " + pfi.getPose ().getPz ()
					+ ", " + pfi.getPose ().getProll () + ", "
					+ pfi.getPose ().getPpitch () + ", "
					+ pfi.getPose ().getPyaw () + "]";
			s += "  UPose : [" + pfi.getUpose ().getPx () + ", "
					+ pfi.getUpose ().getPy () + ", "
					+ pfi.getUpose ().getPz () + ", "
					+ pfi.getUpose ().getProll () + ", "
					+ pfi.getUpose ().getPpitch () + ", "
					+ pfi.getUpose ().getPyaw () + "]\n";
		}
		return s;
	}

	static String decodeFiducialGeom (FiducialInterface fidi) {
		PlayerFiducialGeom pfgeom = fidi.getGeom ();
		return "Fiducial geometry: \n" + "    Pose: ["
				+ pfgeom.getPose ().getPx () + ", "
				+ pfgeom.getPose ().getPx () + ", "
				+ pfgeom.getPose ().getPx () + "]\n" + "    Size: ["
				+ pfgeom.getSize ().getSw () + "," + pfgeom.getSize ().getSw ()
				+ "]";

	}

	static String decodeGripperGeom (GripperInterface grii) {
		PlayerGripperGeom pggeom = grii.getGeom ();
		return "Fiducial geometry: \n" + "    Pose: ["
				+ pggeom.getPose ().getPx () + ", "
				+ pggeom.getPose ().getPx () + ", "
				+ pggeom.getPose ().getPx () + "]\n" + "    Size: ["
//				+ pggeom.getSize ().getSw () + "," + pggeom.getSize ().getSw ()
				+ "]";

	}

	static String decodeRFIDData (RFIDInterface rfid) {
		PlayerRfidData prdata = rfid.getData ();
		String s = "Tags found: " + prdata.getTags_count () + "\n";

		for (int i = 0; i < prdata.getTags_count (); i++) {
			PlayerRfidTag tag = prdata.getTags ()[i];
			String GUID = byteArrayToHexString (tag.getGuid ());
			byte[] type = new byte[1];
			type[0] = (byte) tag.getType ();
			s += "     Tag " + (i + 1) + " of type 0x"
					+ byteArrayToHexString (type) + ": [" + GUID + "]\n";
		}
		// s = "";
		return s;
	}

	static String byteArrayToHexString (byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F" };

		StringBuffer out = new StringBuffer (in.length * 2);
		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);
			// shift the bits down
			ch = (byte) (ch & 0x0F);
			// must do this is high order bit is on!

			out.append (pseudo[(int) ch]); // convert the nibble to a char
			ch = (byte) (in[i] & 0x0F);    // Strip off low nibble
			out.append (pseudo[(int) ch]); // convert the nibble to a char
			i++;
		}
		String rslt = new String (out);
		return rslt;
	}    
	
}