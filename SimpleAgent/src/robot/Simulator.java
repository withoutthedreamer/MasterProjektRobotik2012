package robot;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;

public class Simulator implements Runnable {
	
	protected PlayerClient playerclient = null;
	protected static Simulator instance = null;

	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );

	// Singleton
	protected Simulator(String name, int port) {
		try {
			// Connect to the Player server and request access to Position
			this.playerclient  = new PlayerClient (name, port);
			System.out.println("Running playerclient of: "
					+ this.toString()
					+ " in thread: "
					+ this.playerclient.getName());		
			// Automatically start own thread in constructor
			this.thread.start();
			
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName());
			
		} catch (PlayerException e) {
			System.err.println ("Simulator: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	
	public static Simulator getInstance (String name, int port) {
		if (instance == null) {
			instance = new Simulator(name, port);
		}
		return instance;
		
	}
	protected void update () {
		
	}

	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			// Should not be called more than @ 10Hz
			this.update();
			try { Thread.sleep (100); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		}
	}
	// Shutdown simulator and clean up
	public void shutdown () {
		// Cleaning up
		this.playerclient.close();
		this.thread.interrupt();
		while(this.thread.isAlive());
		System.out.println("Shutdown of " + this.toString());
	}
}
