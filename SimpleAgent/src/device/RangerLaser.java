package device;

import javaclient3.LaserInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class RangerLaser extends Ranger {
	
	protected LaserInterface las = null;
	protected float[] lasRanges = null;
		
	public RangerLaser (PlayerClient host, int id) {
		try {
			this.las = host.requestInterfaceLaser(0, PlayerConstants.PLAYER_OPEN_MODE);

			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);

		} catch ( PlayerException e ) {
			System.err.println ("RangerLaser: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new IllegalStateException();
		}
	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	protected void update() {
		// Wait for the laser readings
		while ( ! las.isDataReady() ) {
			try { Thread.sleep (SLEEPTIME); }
			catch (InterruptedException e) { thread.interrupt(); }
		}
		count = las.getData().getRanges_count();
		if (count > 0) {
			lasRanges = las.getData().getRanges();
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
