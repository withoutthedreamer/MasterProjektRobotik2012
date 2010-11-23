package robot;

import data.Position;
import device.Gripper;
import device.LaserUrg;
import device.Planner;
import javaclient3.PlayerException;

final public class PioneerLG extends Pioneer {
	protected Gripper grip = null;
	protected Planner plan = null;

	public PioneerLG(String name, int port, int id) {
		super(name, port, id);
		try {
			this.laser = new LaserUrg (this.playerclient, this.id);
			this.grip  = new Gripper (this.playerclient, this.id);
//			this.plan  = new Planner (name, 6685, this.id);

		} catch (PlayerException e) {
			System.err.println ("PioneerLG: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}

	public void shutdownDevices () {
		this.laser.thread.interrupt();
		while (this.laser.thread.isAlive());
		this.grip.thread.interrupt();
		while (this.grip.thread.isAlive());
		if (plan != null)
			this.plan.shutdown();
	}

	protected void update () {
	// Robot is planner controlled
	}
	public void setGoal(Position goal) {
		if (plan != null)
			this.plan.setGoal(goal);
	}

	public void setPose(Position position) {
		if (plan != null)
			this.plan.setPose(position);		
	}
	
	public void setPlanner(String name, int port) {
		this.plan = new Planner (name, port, this.id);
	}

	@Override
	public Position getGoal() {
		return this.plan.getGoal();
	}
}
