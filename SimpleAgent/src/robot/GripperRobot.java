package robot;

import data.Position;
import device.Device;

public class GripperRobot extends Pioneer {

	public GripperRobot (Device roboDevices) {
		super(roboDevices);
	}

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
		if (localizer != null)
			localizer.setPosition(position);	
//		if (planner != null)
//			planner.setPose(position);
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
}
