package device;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import data.BbNote;
import data.Trackable;
import simulator.Simulator;


public class Blackboard implements Runnable {
	
	protected static Blackboard instance = null;
	protected static Simulator simu = null;
	protected Trackable collectrobot = null;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	
	protected HashMap<String,BbNote> notehm = null;
	private static final long SLEEPTIME = 1000;

	protected Blackboard(Trackable robot) {
		try {
			// Assume there is already one simulation
			simu = Simulator.getInstance();
			if(simu == null) {
				// try standard config
				simu = Simulator.getInstance("localhost", 6665);
			}
			collectrobot = robot;
			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName());

		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit (1);
		}
	}
	@SuppressWarnings("rawtypes")
	public void update () {
		Set set = this.notehm.entrySet();
		Iterator i = set.iterator();
		// Track the 1st only
		if(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			String key = (String)me.getKey();
			BbNote note = (BbNote)me.getValue();
			if (note.isCompleted()) {
				notehm.remove(key);
			} else {
				note.update();
			}
		}

		try { Thread.sleep (SLEEPTIME); }
		catch (InterruptedException e) { this.thread.interrupt(); }
	}
	
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.update ();
		}
		System.out.println("Shutdown of " + this.toString());
	}

	public static Blackboard getInstance (Trackable robot) {
		if (instance == null) {
			instance = new Blackboard(robot);
		}
		return instance;
		
	}
	public static Blackboard getInstance () {
		return instance;
	}
	public void add(String key, BbNote note) {
		// TODO for testing only
		note.setTrackable(collectrobot);
		this.notehm.put(key, note);
	}
	public BbNote get(String key) {
		return this.notehm.get(key);
	}
}
