package device;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

public class Ranger implements Runnable {

	protected RangerInterface rang = null;
	protected double[] ranges	= null;
	protected int count;
	protected final static int SLEEPTIME = 100;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );

	public Ranger () {};
	
	public Ranger (PlayerClient host, int id, int device) {
		try {
			this.rang = host.requestInterfaceRanger (device, PlayerConstants.PLAYER_OPEN_MODE);

			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);

		} catch ( PlayerException e ) {
			System.err.println ("Ranger: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	protected void update() {
		if ( ! rang.isDataReady() ) {
			try { Thread.sleep (SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		} else {
			count = rang.getData().getRanges_count();
			ranges = rang.getData().getRanges();
		}
	}

	public double[] getRanges () {
		return ranges;
	}

	public int getCount () {
		return count;
	}

	@Override
	public void run() {
		while ( ! thread.isInterrupted()) {
			update();
		}
		System.out.println("Shutdown of " + this.toString());
	}
}
