package device;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.SonarInterface;
import javaclient3.structures.PlayerConstants;

public class Sonar implements Runnable{
	protected SonarInterface soni  = null;
	protected float[] ranges = null;
	protected int count = 0;
	protected final int SLEEPTIME = 100;
	
	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );

	// Host id
	public Sonar (PlayerClient host, int id) {
		try {
			this.soni = host.requestInterfaceSonar (0, PlayerConstants.PLAYER_OPEN_MODE);

			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);

		} catch ( PlayerException e ) {
			System.err.println ("Sonar: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new IllegalStateException();
		}
	}
	
	// Only to be called @~10Hz
	protected void updateRanges() {
		// Wait for sonar readings
		while ( ! soni.isDataReady ()){
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		}
		this.count = this.soni.getData().getRanges_count();
		if (this.count > 0) {
			this.ranges = soni.getData().getRanges();
		}
	}

	public float[] getRanges() {
		return ranges;
	}
	
	public int getCount() {
		return this.count;
	}

	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.updateRanges ();
		}
		System.out.println("Shutdown of " + this.toString());
	}
}
