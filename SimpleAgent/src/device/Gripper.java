package device;

import core.Logger;
import javaclient3.GripperInterface;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;

public class Gripper extends Device implements Runnable {

	protected GripperInterface  grip  = null;
	protected final int SLEEPTIME = 1000;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	@SuppressWarnings("unused")
	private int goalState = 1;
	private int curState = 0;
	
	protected static enum stateType {
		OPEN,
		CLOSE,
		ERROR
	}

	public Gripper(RobotClient roboClient, int id) {
		super(id);
		try {
			grip = roboClient.getClient().requestInterfaceGripper(0, PlayerConstants.PLAYER_OPEN_MODE);

			// Automatically start own thread in constructor
			this.thread.start();
			
			Logger.logActivity(false, "Running", this.toString(), id, thread.getName());

		} catch ( PlayerException e ) {
//			System.err.println ("    [ " + e.toString() + " ]");
			Logger.logActivity(true, "Connecting", this.toString(), id, thread.getName());
			throw new IllegalStateException();
		}
	}
	protected void update () {
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
			this.update();
		}
		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
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
