package robot;

import data.Position;
import device.Gripper;
import device.Planner;

public class PioneerRG extends PioneerRla {
	protected Planner plan = null;
	protected Gripper grip = null;

	public PioneerRG(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		grip = new Gripper (roboClient, id);
		plan = new Planner (name, (port+1), id);
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
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

//	public void setPlanner(String name, int port) {
//		plan = new Planner (name, port, id);
//	}
}
