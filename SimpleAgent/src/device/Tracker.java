package device;

import java.util.Vector;

import data.Position;
import data.SimuObject;
import robot.external.IRobot;

//import javaclient3.PlayerException;

/**
 * Keeps position of simulator and outside objects in sync.
 * Singleton.
 * @author sebastian
 *
 */
public class Tracker extends Device {

	protected Simulation simu = null;
	protected Vector<SimuObject> simuObjs = null;
	protected static Tracker instance = null;
	
	// Singleton
	protected Tracker(Simulation simu)
	{
		if (simu != null) {
			this.simu = simu;
			simuObjs = new Vector<SimuObject>();
			instance = this;
		}
	}
	
	/**
	 * Returns a Singleton instance of the tracker.
	 * @param simu Simulator that contains objects to be updated.
	 * @return Instance of the tracker
	 */
	public static Tracker getInstance (Simulation simu)
	{
		if (instance == null) {
			instance = new Tracker(simu);
		}
		return instance;
	}
	
	protected void update ()
	{
		if (simuObjs == null) { return; }
		int count = simuObjs.size();
		for (int i=0; i<count; i++) {
			// update objects position
			String   id  = simuObjs.get(i).getId();
			Position pos = simuObjs.get(i).getObject().getPosition();
			
			// update the simulator
			simu.setPositionOf(id, pos);
		}
		
	}

	public void addObject(String id, IRobot obj) {
		this.simuObjs.add(new SimuObject(id, obj));
	}

}
