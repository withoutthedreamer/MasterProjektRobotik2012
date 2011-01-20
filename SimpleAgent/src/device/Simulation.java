package device;

import data.Position;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.*;

import javaclient3.structures.PlayerPose;
import javaclient3.structures.simulation.PlayerSimulationPose2dReq;

/**
 * Stage Gui to draw online robot(s) position
 * @author sebastian
 *
 */
public class Simulation extends PlayerDevice {
	
	protected static Simulation instance = null;
	
	protected ConcurrentHashMap<String,Position> objList = null;

	// Singleton
	protected Simulation(RobotClient roboClient, Device device)
	{
		super(roboClient, device);
		
		objList = new ConcurrentHashMap<String, Position>();			
	}
	/**
	 * Returns a Singleton instance of the Gui
	 * @param name Host name (or IP) of the player server. 
	 * @param port Port of the player server.
	 * @return Gui instance.
	 */
	public static Simulation getInstance (RobotClient roboClient, Device device)
	{
		if (instance == null) {
			instance = new Simulation(roboClient, device);
		}
		return instance;
	}
	/**
	 * Returns null if no Gui instance is instantiated.
	 * @return null or Gui instance.
	 */
	public static Simulation getInstance () {
		return instance; // whether or not it is null
	}
	
	// TODO Currently only 'static' objects should be modified
	@Override
	protected void update () {
//		while ( ! this.simu.isDataReady() ) { // TODO debug it
		//		PlayerPose pp = new PlayerPose(7,7,0);
		//		this.simu.set2DPose(identifier, pp);
		Set<Entry<String,Position>> set = this.objList.entrySet();
		Iterator<Entry<String, Position>> i = set.iterator();
		while(i.hasNext()) {
			Map.Entry<String, Position> me = (Map.Entry<String, Position>)i.next();
			String key = (String)me.getKey();
			Position pos = (Position)me.getValue();
			PlayerPose pp = new PlayerPose(pos.getX(), pos.getY(), pos.getYaw());
			((javaclient3.SimulationInterface) device).set2DPose(key, pp);
			// TODO player not working yet
			//			pos = this.getObjectPos(key);
			//			if (pos != null) {
			//				System.out.println(pos.toString());
			//			}
			// Unlock objects
			objList.clear();
		}
	}

	/**
	 * Shutdown Gui and clean up
	 */
	@Override
	public void shutdown () {
		super.shutdown();
		objList.clear();
	}
	/**
	 * Set a Gui object's position 
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @param value The new Position of that object.
	 */
	public void setObjectPos(String key, Position value) {
		objList.put(key, value);
//		((javaclient3.SimulationInterface) device).getSimulationPose2D();
	}
	/**
	 * Returns the last known object Position.
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @return Last known Position.
	 */
	// TODO test and make asynchronous
	// TODO not working yet
	public Position getObjectPos(String key) {
		((javaclient3.SimulationInterface) device).get2DPose (key);
		if ( ((javaclient3.SimulationInterface) device).isPose2DReady() ) {
			PlayerSimulationPose2dReq pose = ((javaclient3.SimulationInterface) device).getSimulationPose2D();
			return new Position(
					pose.getPose().getPx(),
					pose.getPose().getPy(),
					pose.getPose().getPa());
		} else {
			return null;
		}
	}
}
