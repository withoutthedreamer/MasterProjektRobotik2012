package robot;

import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.State;
import de.unihamburg.informatik.tams.project.communication.exploration.Exploration.RobotState;
import device.Device;

public abstract class PatrolRobot extends NavRobot {

	protected MapPosition position;

	protected RobotState state;

	public PatrolRobot(Device[] devList) {
		super(devList);
	}

	public boolean hasGripper() {
		return super.getGripper() != null;
	}

	public State getState() {
		State result = null;
		if(state == RobotState.NEEDS_NEW_GOAL || state == RobotState.ON_THE_WAY) {
			result = State.EXPLORING;
		} else if(state == RobotState.TRANSPORTING_BARREL) {
			result = State.TRANSPORTING;
		}
		return result;
	}

	public MapPosition getMapPosition() {
		return position;
	}

	public void transportBarrelTo(MapPosition currentPositionOfBarrel, MapPosition targetPositionOfBarrel) {
		// TODO Auto-generated method stub
		
	}

	public abstract void doStep();

}