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
		
		plan.runThreaded();
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
		if (grip != null) {
			grip.thread.interrupt();
			while (grip.thread.isAlive());
		}
		if (plan != null) {
			plan.shutdown();
		}
	}

	protected void update () {
		// Robot is planner controlled
	}
	public void setGoal(Position goal) {
		if (plan != null)
			plan.setGoal(goal);
	}

	@Override
	public Position getGoal() {
		return plan.getGoal();
	}

	public void setPosition(Position position) {
		if (plan != null)
			plan.setPose(position);
		if (posi != null)
			posi.setPosition(position);
	}

	/// Return robot position
	public Position getPosition() {
		//			return this.plan.getPose(); // TODO why not working
//		return this.posi.getPosition();
		return plan.getPose();
	}

//	public void setPlanner(String name, int port) {
//		plan = new Planner (name, port, id);
//	}
}
