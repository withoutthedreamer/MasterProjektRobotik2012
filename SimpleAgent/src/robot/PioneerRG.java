package robot;

import javaclient3.PlayerException;
import data.Position;
import device.Gripper;
import device.Planner;
import device.Ranger;


public class PioneerRG extends Pioneer {
	protected Planner plan = null;
	protected Gripper grip = null;
	
	public PioneerRG(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			laser = new Ranger(roboClient, id, 1);
			grip  = new Gripper (roboClient, id);

		} catch (PlayerException e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
		roboClient.runThreaded();
	}

	public void shutdownDevices () {
		laser.thread.interrupt();
		while (laser.thread.isAlive());
		grip.thread.interrupt();
		while (grip.thread.isAlive());
		if (plan != null)
			plan.shutdown();
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
		return this.plan.getGoal();
	}

	public final void setPosition(Position position) {
		if (plan != null)
			this.plan.setPose(position);
//		if (posi != null)
//			this.posi.setPosition(position);
	}

	/// Return robot position
	public final Position getPosition() {
		//			return this.plan.getPose(); // TODO why not working
//		return this.posi.getPosition();
		return this.plan.getPose();
	}

	public void setPlanner(String name, int port) {
		this.plan = new Planner (name, port, this.id);
	}
}
