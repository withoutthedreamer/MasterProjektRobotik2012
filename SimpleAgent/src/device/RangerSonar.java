package device;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.SonarInterface;
import javaclient3.structures.PlayerConstants;
/// TODO test this
public class RangerSonar extends Ranger {

	protected SonarInterface soni  = null;
	protected float[] sonRanges = null;

	public RangerSonar(PlayerClient host, int id) {
		try {
			soni = host.requestInterfaceSonar(0, PlayerConstants.PLAYER_OPEN_MODE);

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
		}
	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	protected void update() {
		// Wait for the laser readings
		while ( ! soni.isDataReady() ) {
			try { Thread.sleep (SLEEPTIME); }
			catch (InterruptedException e) { thread.interrupt(); }
		}
		count = soni.getData().getRanges_count();
		if (count > 0) {
			sonRanges = soni.getData().getRanges();
		}
	}

	public double[] getRanges () {
		double[] convRanges = null;
		if (count > 0) {
			convRanges = new double[count];
			// convert from float to double
			for (int i=0; i<count; i++) {
				convRanges[i] = (double) sonRanges[i];
			}
		}
		return convRanges;
	}
}
