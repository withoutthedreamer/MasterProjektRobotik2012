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
public class Simulation extends RobotDevice {
		
	static Simulation instance = null;
	// Objects of interest in the simulation
	ConcurrentHashMap<String,Position> objList = null;
	ConcurrentHashMap<String,Boolean> isDirtyList = null;
//	Set<Entry<String,Position>> set = null;
//	Iterator<Entry<String, Position>> i = null;

	protected Simulation () {
		super();
	}
	
	// Singleton
	protected Simulation(DeviceNode roboClient, Device device)
	{
		super(roboClient, device);
		
		objList = new ConcurrentHashMap<String, Position>();	
		isDirtyList = new ConcurrentHashMap<String, Boolean>();
		
//		setSleepTime(1000);
	}
	/**
	 * Returns a Singleton instance of the Gui
	 * @param name Host name (or IP) of the player server. 
	 * @param port Port of the player server.
	 * @return Gui instance.
	 */
	public static Simulation getInstance (DeviceNode roboClient, Device device)
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
		if (instance == null) {
			instance = new Simulation();
		}
		return instance;
	}
	
	// TODO Currently only 'static' objects should be modified
	@Override
	protected void update ()
	{
		Set<Entry<String,Position>> set = objList.entrySet();
		Iterator<Entry<String, Position>> i = set.iterator();
		while(i.hasNext())
//		if(i.hasNext())
		{
			Map.Entry<String, Position> me = (Map.Entry<String, Position>)i.next();
			String key = (String)me.getKey();
			
			if (isDirtyList.get(key) != null && isDirtyList.get(key) == true)
			{
				isDirtyList.put(key, false);
				Position pos = (Position)me.getValue();
				PlayerPose pp = new PlayerPose(pos.getX(), pos.getY(), pos.getYaw());
				
//				while ( ! ((javaclient3.SimulationInterface) device).isDataReady() );// { // TODO debug it
				((javaclient3.SimulationInterface) device).set2DPose(key, pp);
//				try { Thread.sleep(50); } catch (InterruptedException e) { thread.interrupt(); }
			}
			else
			{
				((javaclient3.SimulationInterface) device).get2DPose (key);
				
//				try { Thread.sleep(50); } catch (InterruptedException e) { thread.interrupt(); }
				
				if (((javaclient3.SimulationInterface) device).isPose2DReady())
				{
					PlayerSimulationPose2dReq pose = ((javaclient3.SimulationInterface) device).getSimulationPose2D();
					PlayerPose pPose = pose.getPose();
					Position curPose = new Position(
//							pose.getPose().getPx(),
							pPose.getPx(),
//							pose.getPose().getPy(),
							pPose.getPy(),
//							pose.getPose().getPa());
							pPose.getPa());
					objList.put(key, curPose);
//					System.err.println(key + ": " + curPose.toString());
					
				}
			}
			/** Wait for simulation sync before updating a new object */
			try { Thread.sleep(50); } catch (InterruptedException e) { thread.interrupt(); }
		}
	}

	/**
	 * Shutdown Gui and clean up
	 */
	@Override
	public void shutdown () {
		super.shutdown();
		objList.clear();
		isDirtyList.clear();
	}
	/**
	 * Set a Gui object's position.
	 * The object will be either added or updated with respect to the internal data structure.
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @param value The new Position of that object.
	 */
	public synchronized void setPositionOf(String key, Position value)
	{
		if (key != null && value != null) {
			objList.put(key, new Position(value));
			// Mark dirty
			isDirtyList.put(key, true);
		}
	}
	/**
	 * Returns the last known object Position.
	 * @param key The Stage object id (usually set by the 'name' tag in the world file).
	 * @return Last known Position.
	 */
	// TODO test and make asynchronous
	// TODO not working yet
	public synchronized Position getPositionOf(String key) {
		return new Position(objList.get(key));
	}
	public synchronized void initPositionOf(String key) {
		objList.put(key, new Position());
		// Trigger a position read
		isDirtyList.put(key, false);
		// Wait for simulation
	}
	public int getObjListCount() {
		return objList.size();
	}
	public int getIsDirtyListCount() {
		return isDirtyList.size();
	}
}
