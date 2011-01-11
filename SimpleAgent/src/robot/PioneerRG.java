package robot;

import javaclient3.PlayerException;
import data.Position;
import device.Gripper;
import device.Planner;
import device.Ranger;


public class PioneerRG extends Pioneer {
	protected Planner plan = null;
	
	public PioneerRG(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			laser = new Ranger(this.playerclient, this.id, 1);
			grip  = new Gripper (this.playerclient, this.id);

		} catch (PlayerException e) {
			System.err.println ("PioneerRG: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new Exception();
		}
		super.playerclient.runThreaded (-1, -1);
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
	}

	/// Return robot position
	public final Position getPosition() {
		//			return this.plan.getPose(); // TODO why not working
		return this.posi.getPosition();
	}

	public void setPlanner(String name, int port) {
		this.plan = new Planner (name, port, this.id);
	}
}
