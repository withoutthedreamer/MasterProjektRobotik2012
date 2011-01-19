package robot;

import data.Position;
import device.Device;

public class GripperRobot extends Pioneer {
//	protected Gripper grip = null;
//	protected Planner plan = null;

	public GripperRobot (Device roboDevices) {
		super(roboDevices);
	}
//	public GripperRobot(String name, int port, int id) throws IllegalStateException {
//		
//		super(name, port, id);
//
//		grip  = new Gripper (roboClient, id);
//		// Take the next port
//		plan  = new Planner (name, port+1, this.id);
//	}

//	protected void shutdownDevices () {
//		super.shutdownDevices();
//		this.grip.thread.interrupt();
//		while (this.grip.thread.isAlive());
//		if (plan != null)
//			this.plan.shutdown();
//	}

	@Override
	protected void update () {
	// Robot is planner controlled
	}
	@Override
	public void setGoal(Position goal) {
		if (planner != null)
			planner.setGoal(goal);
	}
	@Override
	public Position getGoal() {
		if (planner != null) {
			return planner.getGoal();
		} else {
			return null;
		}
	}
	@Override
	public final void setPosition(Position position) {
		if (planner != null)
			planner.setPose(position);		
	}
	
	/// Return robot position
	@Override
	public final Position getPosition() {
//		return posi.getPosition();
		if (planner != null) {
			return planner.getPose();
		} else {
			return super.getPosition();
		}
	}
//	public void setPlanner(String name, int port) {
//		plan = new Planner (name, port, this.id);
//	}
}
