package robot;

import java.util.ArrayList;

import data.Position;
import de.unihamburg.informatik.tams.project.communication.Barrel;
import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.State;
import de.unihamburg.informatik.tams.project.communication.exploration.Exploration.RobotState;
import device.Device;
import device.Planner;
import device.external.IGripperListener;

public abstract class PatrolRobot extends NavRobot {

	protected MapPosition position;

	protected RobotState state;

	protected Planner planner;
	
	protected ArrayList<double[]> barrelPositions;
	
	protected ArrayList<Barrel> knownBarrels;

	protected data.Position ownPosition = this.getPosition();

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
	
	public void setState(RobotState state) {
		this.state = state;
	}

	public MapPosition getMapPosition() {
		return position;
	}

	public void transportBarrelTo(MapPosition currentPositionOfBarrel, MapPosition targetPositionOfBarrel) {
		Position barrelPos = new Position(currentPositionOfBarrel.getxPosition(), currentPositionOfBarrel.getyPosition(), 0);
		IGripperListener gl = new IGripperListener() {
			public void whenOpened(){}
			public void whenClosed(){}
			public void whenLifted(){}
			public void whenReleased(){}
			public void whenClosedLifted(){}
			public void whenReleasedOpened(){}
			public void whenError(){}
		};
		getGripper().open(gl);
		getPlanner().setGoal(barrelPos);
	}

	public abstract void doStep();
	
	// TODO Aus den relativen Position des Barrels müssen die Weltkoordinaten berechnet werden.
	// Barrelobject muss aus dem Informationen im Barrelarray gebaut werden. Angaben in cm. Könnte schwierig  sein
	private Position convertBarrelCoordToWorldCoord(Barrel barrel) {
		return null;
	}
	
	private void checkForNewBarrels() {
		if(barrelPositions != null) {
			
		}
	}

}