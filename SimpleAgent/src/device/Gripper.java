package device;

import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class Gripper implements Runnable {

	protected GripperInterface  grip  = null;
	protected final int SLEEPTIME = 1000;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	private int goalState = 1;
	private int curState = 0;
	
	protected static enum stateType {
		OPEN,
		CLOSE,
		ERROR
	}

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
//			System.exit (1);
			throw new IllegalStateException();
		}
	}
	protected void updateGripper () {
		if ( ! grip.isDataReady() ) {			
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		} else {
			curState = grip.getData().getState();
//			grip.setGripper(goalState);
		}
	}
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.updateGripper();
		}
		System.out.println("Shutdown of " + this.toString());
	}
	public void stop () {
		// stop
		this.goalState = 3;
	}
	public void open () {
		// open
		this.goalState = 1;
	}
	public void close () {
		// close
		this.goalState = 2;
	}
	public void lift () {
		this.goalState = 4;
	}
	public void release () {
		this.goalState = 5;
	}
	// Taken from player gripper IF doc
	public stateType getState() {
		stateType state = stateType.ERROR;
		
		switch (curState) {
			case 1:  state = stateType.OPEN;  break;
			case 2:  state = stateType.CLOSE; break;
			default: state = stateType.ERROR; break;
		}
		return state;
	}
}
