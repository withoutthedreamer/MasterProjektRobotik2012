package simulator;

import data.Position;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import core.Logger;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.SimulationInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.simulation.PlayerSimulationPose2dReq;

/**
 * Stage Gui to draw online robot(s) position
 * @author sebastian
 *
 */
public class Simulator implements Runnable {
	
	protected PlayerClient playerclient = null;
	protected SimulationInterface simu = null;
	protected static Simulator instance = null;
	
//	protected HashMap<String,Position> objList = null;
	protected ConcurrentHashMap<String,Position> objList = null;

	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );

	// Singleton
	// TODO change to new interface
	protected Simulator(String name, int port) {
		try {
			// Connect to the Player server and request access to Position
			this.playerclient  = new PlayerClient (name, port);
			System.out.println("Running playerclient of: "
					+ this.toString()
					+ " in thread: "
					+ this.playerclient.getName());
			
			this.simu = this.playerclient.requestInterfaceSimulation(0, PlayerConstants.PLAYER_OPEN_MODE);
			this.objList = new ConcurrentHashMap<String, Position>();
			
			// Automatically start own thread in constructor
			this.thread.start();
			
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName());
			
		} catch (PlayerException e) {
			Logger.logActivity(true, "Connecting", this.toString(), -1, thread.getName());
//			System.err.println ("    [ " + e.toString() + " ]");
		}
	}
	/**
	 * Returns a Singleton instance of the Gui
	 * @param name Host name (or IP) of the player server. 
	 * @param port Port of the player server.
	 * @return Gui instance.
	 */
	public static Simulator getInstance (String name, int port) {
		if (instance == null) {
			instance = new Simulator(name, port);
		}
		return instance;
	}
	/**
	 * Returns null if no Gui instance is instantiated.
	 * @return null or Gui instance.
	 */
	public static Simulator getInstance () {
		return instance; // whether or not it is null
	}
	
	// TODO Currently only 'static' objects should be modified
	@SuppressWarnings("rawtypes")
	protected void update () {
//		while ( ! this.simu.isDataReady() ) { // TODO debug it
		//		PlayerPose pp = new PlayerPose(7,7,0);
		//		this.simu.set2DPose(identifier, pp);
		Set set = this.objList.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			String key = (String)me.getKey();
			Position pos = (Position)me.getValue();
			PlayerPose pp = new PlayerPose(pos.getX(), pos.getY(), pos.getYaw());
			this.simu.set2DPose(key, pp);
			// TODO player not working yet
			//			pos = this.getObjectPos(key);
			//			if (pos != null) {
			//				System.out.println(pos.toString());
			//			}
			// Unlock objects
			this.objList.clear();
		}
		try { Thread.sleep (100); }
		catch (InterruptedException e) { this.thread.interrupt(); }
		//		}
	}

	@Override
	public void run() {
//		this.test();
		while ( ! this.thread.isInterrupted()) {
			// Should not be called more than @ 10Hz
			this.update();
		}
	}
	/**
	 * Shutdown Gui and clean up
	 */
	public void shutdown () {
		// Cleaning up
		if (playerclient != null) {
			playerclient.close();
		}
		thread.interrupt();
		while(this.thread.isAlive());
		System.out.println("Shutdown of " + this.toString());
	}
	/**
	 * Set a Gui object's position 
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @param value The new Position of that object.
	 */
	public void setObjectPos(String key, Position value) {
		objList.put(key, value);
//		this.simu.getSimulationPose2D();
	}
	/**
	 * Returns the last known object Position.
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @return Last known Position.
	 */
	// TODO test and make asynchronous
	// TODO not working yet
	public Position getObjectPos(String key) {
		simu.get2DPose (key);
		if ( simu.isPose2DReady() ) {
			PlayerSimulationPose2dReq pose = simu.getSimulationPose2D();
			return new Position(
					pose.getPose().getPx(),
					pose.getPose().getPy(),
					pose.getPose().getPa());
		} else {
			return null;
		}
	}
}
