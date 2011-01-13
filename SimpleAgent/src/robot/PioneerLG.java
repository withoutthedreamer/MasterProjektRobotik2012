package robot;

import data.Position;
import device.Gripper;
import device.Planner;

public class PioneerLG extends PioneerL {
	protected Gripper grip = null;
	protected Planner plan = null;

	public PioneerLG(String name, int port, int id) throws IllegalStateException {
		
		super(name, port, id);
		
		try {
			grip  = new Gripper (roboClient, id);
			// Take the next port
			plan  = new Planner (name, port+1, this.id);

		} catch (Exception e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
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

	@Override
	public Position getGoal() {
		return plan.getGoal();
	}

	public final void setPosition(Position position) {
		if (plan != null)
			plan.setPose(position);		
	}
	
	/// Return robot position
	public final Position getPosition() {
//		return this.plan.getPose(); // TODO why not working
		return posi.getPosition();
	}

	public void setPlanner(String name, int port) {
		plan = new Planner (name, port, this.id);
	}
}
