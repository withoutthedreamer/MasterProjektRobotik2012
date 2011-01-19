package device;

import javaclient3.LaserInterface;

public class RangerLaser extends Ranger {
	
	protected LaserInterface las = null;
	protected float[] lasRanges = null;
		
	public RangerLaser (RobotClient roboClient, Device device) {
		super(roboClient, device);
	}
//		super(device);
//		try {
//			las = roboClient.getClient().requestInterfaceLaser(0, PlayerConstants.PLAYER_OPEN_MODE);
//
//			// Automatically start own thread in constructor
////			thread.start();
////			Logger.logActivity(false, "Running", this.toString(), id, thread.getName());
//
//		} catch ( PlayerException e ) {
////			System.err.println ("    [ " + e.toString() + " ]");
//			Logger.logDeviceActivity(true, "Connecting", this);
//			throw new IllegalStateException();
//		}
//	}
//	public RangerLaser(RobotClient roboClient, Device device) {
//		this(roboClient,device.getHost());
//		host = device.getHost();
//		name = device.getName();
//		deviceNumber = device.getDeviceNumber();
//		port = device.getPort();
//	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	protected void update() {
		// Wait for the laser readings
		while ( ! ((javaclient3.LaserInterface) device).isDataReady() ) {
//			try { Thread.sleep (SLEEPTIME); }
//			catch (InterruptedException e) { thread.interrupt(); }
		}
		count = ((javaclient3.LaserInterface) device).getData().getRanges_count();
		if (count > 0) {
			lasRanges = ((javaclient3.LaserInterface) device).getData().getRanges();
		}
	}
	
	public double[] getRanges () {
		double[] convRanges = null;
		if (count > 0) {
			convRanges = new double[count];
			// convert from float to double
			for (int i=0; i<count; i++) {
				convRanges[i] = (double) lasRanges[i];
			}
		}
		return convRanges;
	}
}
