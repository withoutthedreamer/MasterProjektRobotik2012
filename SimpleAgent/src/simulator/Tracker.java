package simulator;

import java.util.Vector;

import data.Position;
import data.SimuObject;
import data.Trackable;

import javaclient3.PlayerException;
/**
 * Keeps position of simulator and outside objects in sync.
 * Singleton.
 * @author sebastian
 *
 */
public class Tracker implements Runnable {

	protected Simulator simu = null;
	protected Vector<SimuObject> simuObjs = null;
	protected static Tracker instance = null;
	
	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );

	/**
	 * Creates the Tracker.
	 * @param simu Simulator that contains objects to be updated.
	 * @param simuObjs Vector of objects to be followed.
	 */
	protected Tracker(Simulator simu, Vector<SimuObject> simuObjs) {
		try {
			this.simu = simu;
			if (simuObjs == null) {
				this.simuObjs = new Vector<SimuObject>();
			} else {
				this.simuObjs = simuObjs;
			}
			
			// Automatically start own thread in constructor
			this.thread.start();
			
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName());
			
		} catch (PlayerException e) {
			System.err.println ("Tracker: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}

	/**
	 * Returns a Singleton instance of the tracker.
	 * @param simu Simulator that contains objects to be updated.
	 * @param simuObjs Vector of objects to be followed.
	 * @return Instace of the tracker
	 */
	public static Tracker getInstance (Simulator simu, Vector<SimuObject> simuObjs) {
		if (instance == null) {
			instance = new Tracker(simu, simuObjs);
		}
		return instance;
	}
	
	protected void update () {
		if (this.simuObjs == null) { return; }
		int count = this.simuObjs.size();
		for (int i=0; i<count; i++) {
			// update objects position
			String   id  = this.simuObjs.get(i).getId();
			Position pos = this.simuObjs.get(i).getObject().getPosition();
			
			// update the simulator
			this.simu.setObjectPos(id, pos);
		}
		
		try { Thread.sleep (100); }
		catch (InterruptedException e) { this.thread.interrupt(); }

	}
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.update();
		}		
	}

	public void shutdown() {
		// Cleaning up
		this.thread.interrupt();
		while(this.thread.isAlive());
		System.out.println("Shutdown of " + this.toString());		
	}
	public void addObject(String id, Trackable obj) {
		this.simuObjs.add(new SimuObject(id, obj));
	}

}
