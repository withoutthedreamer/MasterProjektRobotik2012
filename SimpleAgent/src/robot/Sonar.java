package robot;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.SonarInterface;
import javaclient3.structures.PlayerConstants;

public class Sonar {
	
	protected SonarInterface soni  = null;
	protected float[] ranges = null;
	protected int count = 0;
	
	public Sonar (PlayerClient host) {
		try {
			this.soni = host.requestInterfaceSonar (0, PlayerConstants.PLAYER_OPEN_MODE);
			this.count = this.soni.getData().getRanges_count();

		} catch ( PlayerException e ) {
			System.err.println ("Sonar: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	
	// Only to be called @~10Hz
	public void updateRanges() {
		// Wait for sonar readings
		while (!soni.isDataReady ());
		this.ranges = soni.getData().getRanges();
	}

	public float[] getRanges() {
		return ranges;
	}
	
	public int getCount() {
		return this.count;
	}

}
