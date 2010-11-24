package device;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import data.BbNote;
import data.Position;
import data.Trackable;
import simulator.Simulator;


public class Blackboard implements Runnable {
	
	protected static Blackboard instance = null;
	protected static Simulator simu = null;
	protected Trackable collectrobot = null;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	
	protected LinkedHashMap<String,BbNote> notehm = null;
	private static final long SLEEPTIME = 500;

	protected Blackboard(Trackable robot) {
		try {
			// Assume there is already one simulation
//			simu = Simulator.getInstance();
//			if(simu == null) {
//				// try standard config
//				simu = Simulator.getInstance("localhost", 6665);
//			}
			notehm = new LinkedHashMap<String,BbNote>();
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
		// FIFO HashMap to keep goal order
		if(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			String key = (String)me.getKey();
			BbNote note = (BbNote)me.getValue();
			if (note.isCompleted()) {
				notehm.remove(key);
				System.out.println("Removed note from BB: " + key);
				// Set object in simulator
				if (simu != null) {
					simu.setObjectPos(key, new Position(-3, -5, 0));
				}
			} else {
				note.update();
//				System.out.println("Update of note : " + key);
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
		if ( notehm.get(key) == null ) {
			// TODO for testing only
			note.setTrackable(collectrobot);
			this.notehm.put(key, note);
			System.out.println("BB: added note " + key);
		}
//		System.out.println("Added note: " + key + "\t" + note.getPose() + "\t" + note.getGoal());
	}
	public BbNote get(String key) {
		return this.notehm.get(key);
	}
	public void setSimulation (Simulator simu2) {
		simu = simu2;
	}
	@SuppressWarnings("rawtypes")
	public void shutdown() {
		// TODO debug only
		Set set = this.notehm.entrySet();
		Iterator i = set.iterator();

		while (i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			String key = (String)me.getKey();
			System.out.println("Still on blackboard: " + key);
		}
		
		this.thread.interrupt();
		while (this.thread.isAlive());
		System.out.println("Shutdown of " + this.toString());		
	}
}
