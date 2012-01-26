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
import device.external.IPlannerListener;

public abstract class PatrolRobot extends NavRobot {

	protected MapPosition position;

	protected RobotState state;

	protected RobotState plannerState;

	protected RobotState gripperState;

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
		if (state == RobotState.NEEDS_NEW_GOAL
				|| state == RobotState.ON_THE_WAY) {
			result = State.EXPLORING;
		} else if (state == RobotState.TRANSPORTING_BARREL) {
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

	public void transportBarrelTo(MapPosition currentPositionOfBarrel,
			MapPosition targetPositionOfBarrel) {
			// state setzen
			state = RobotState.TRANSPORTING_BARREL;

			// Position der Barrel und des Ziels erzeugen
			Position barrelPos = new Position(
				currentPositionOfBarrel.getxPosition(),
				currentPositionOfBarrel.getyPosition(), 0);
			Position targetPos = new Position(
				targetPositionOfBarrel.getxPosition(),
				targetPositionOfBarrel.getyPosition(), 0);

			// IGripperListener für den Gripper
			IGripperListener gl = new IGripperListener() {
			public void whenOpened() {
				gripperState = RobotState.GRIPPER_OPEN;
				gripper.removeIsDoneListener(this);
			}
			public void whenClosed() {}
			public void whenLifted() {}
			public void whenReleased() {}
			public void whenClosedLifted() {
				gripperState = RobotState.GRIPPER_CLOSE;
				gripper.removeIsDoneListener(this);
			}
			public void whenReleasedOpened() {
				gripperState = RobotState.GRIPPER_OPEN;
				gripper.removeIsDoneListener(this);
			}
			public void whenError() {}
		};
		// Gripper öffnen
		gripperState = RobotState.OPENING_GRIPPER;
		getGripper().open(gl);
		// Warten bis Gripper offen
		while (gripperState != RobotState.GRIPPER_OPEN) {
			try {
				Thread.sleep(500); // 500ms warten bis gripperState erneut
									// abgefragt wird
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Zur Barrel fahren
		plannerState = RobotState.DRIVING_TO_BARREL;
		planner.addIsDoneListener(new IPlannerListener() {
			@Override
			public void callWhenIsDone() {
				plannerState = RobotState.BARREL_REACHED;
			}
			@Override
			public void callWhenAbort() {}
			@Override
			public void callWhenNotValid() {}
		});
		planner.setGoal(barrelPos);
		// Warten bis Barrel erreicht wurde
		while (plannerState != RobotState.BARREL_REACHED) {
			try {
				Thread.sleep(500); // 500ms warten bis plannerState erneut
									// abgefragt wird
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Gripper schliessen
		gripperState = RobotState.CLOSING_GRIPPER;
		getGripper().closeLift(gl);
		// Warten bis Gripper geschlossen
		while (gripperState != RobotState.GRIPPER_CLOSE) {
			try {
				Thread.sleep(500); // 500ms warten bis gripperState erneut
									// abgefragt wird
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Barrel zum Ziel fahren
		plannerState = RobotState.TRANSPORTING_BARREL;
		planner.addIsDoneListener(new IPlannerListener() {
			@Override
			public void callWhenIsDone() {
				plannerState = RobotState.BARREL_TARGET_REACHED;
			}
			@Override
			public void callWhenAbort() {}
			@Override
			public void callWhenNotValid() {
				logger.info("No valid path");
			}
		});
		planner.setGoal(targetPos);
		// Warten bis Ziel erreicht wurde
		while (plannerState != RobotState.BARREL_TARGET_REACHED) {
			try {
				Thread.sleep(500); // 500ms warten bis plannerState erneut
									// abgefragt wird
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Barrel absetzen
		gripperState = RobotState.OPENING_GRIPPER;
		getGripper().releaseOpen(gl);
		// Warten bis Gripper offen
		while (gripperState != RobotState.GRIPPER_OPEN) {
			try {
				Thread.sleep(500); // 500ms warten bis gripperState erneut
									// abgefragt wird
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Etwas Abstand von der Barrel nehmen
		Position newPos = new Position(); // TODO Abstand zur Barrel als Position berechnen!
		planner.addIsDoneListener(new IPlannerListener() {
			@Override
			public void callWhenIsDone() {
				state = RobotState.NEEDS_NEW_GOAL;
			}
			@Override
			public void callWhenAbort() {}
			@Override
			public void callWhenNotValid() {
				logger.info("No valid path");
				state = RobotState.NEEDS_NEW_GOAL;
			}
		});
		planner.setGoal(newPos);
	}

	public abstract void doStep();
	
	// TODO Aus den relativen Position des Barrels müssen die Weltkoordinaten berechnet werden.
	// Barrelobject muss aus dem Informationen im Barrelarray gebaut werden. Angaben in cm. Könnte schwierig  sein
	private Position convertBarrelCoordToWorldCoord(Barrel barrel) {
		return null;
	}
	
	// TODO implementieren
	private void checkForNewBarrels() {
		if(barrelPositions != null) {
			
		}
	}

}
