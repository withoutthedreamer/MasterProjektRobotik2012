package robot;

import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class Gripper implements Runnable {

	protected GripperInterface  grip  = null;
	protected final int SLEEPTIME = 100;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );

	public Gripper (PlayerClient host, int id) {
		try {
			this.grip  = host.requestInterfaceGripper(0, PlayerConstants.PLAYER_OPEN_MODE);
			
			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);

		} catch ( PlayerException e ) {
			System.err.println ("Blobfinder: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	protected void setGripPos () {
		while ( ! this.grip.isDataReady() ) {
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		}
	}
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.setGripPos();
		}
	}
}
