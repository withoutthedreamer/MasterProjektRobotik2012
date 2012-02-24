package robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import data.Position;
import de.unihamburg.informatik.tams.project.communication.Barrel;
import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.exploration.Grid;
import de.unihamburg.informatik.tams.project.communication.exploration.GridPosition;
import de.unihamburg.informatik.tams.project.communication.network.CommunicationFactory;
import device.Device;
import device.Localize;
import device.external.IGripperListener;
import device.external.ILocalizeListener;
import device.external.IPlannerListener;

public class AntRobot extends PatrolRobot {

	private Random rand = new Random();
	
	private Grid grid;
	GridPosition prevGpos;
	GridPosition gpos;
	GridPosition goal;
	ArrayList<GridPosition> positions;
	Localize localize;
	
	@Override
	public void doStep() {
		if(ownPosition.equals(new Position(0,0,0))) {
			ownPosition = this.getPosition();
		}
		System.out.println("Robotstate: "+state);
		System.out.println("Planner aktiv " + planner.isActive());
		System.out.println("Planner aktuelles Ziel " + planner.getGoal());
		System.out.println("Planner aktuelles Ziel valid " + planner.isValidGoal());
		System.out.println("Eigene Position " + ownPosition);
		System.out.println();
		if(state == RobotState.NEEDS_NEW_GOAL) {
			position = new MapPosition((int)ownPosition.getX(), (int)ownPosition.getY());
			prevGpos = gpos;
			gpos = grid.getOwnPosition(position);
			// TODO wird ersetzt durch neighbours Methode des Grids
			positions = new ArrayList<GridPosition>();
		
			positions.add(new GridPosition(gpos.getxPosition()-1, gpos.getyPosition())); // north
			positions.add(new GridPosition(gpos.getxPosition(), gpos.getyPosition()-1)); // west
			positions.add(new GridPosition(gpos.getxPosition()+1, gpos.getyPosition())); // south
			positions.add(new GridPosition(gpos.getxPosition(), gpos.getyPosition()+1)); // east
			
			if(!ownPosition.equals(new Position(0,0,0))) {
				goal = choose(positions, grid);
			}
			
			if (goal != null) {
				planner.addIsDoneListener(new IPlannerListener() {
					@Override
					public void callWhenIsDone() {
						state = RobotState.NEEDS_NEW_GOAL;
						// TODO wieder aktivieren, wenn es keine Nullpointerexception mehr gibt
//						grid.setOwnPosition(goal);
						planner.removeIsDoneListener(this);
					}

					@Override
					public void callWhenAbort() {
						/** Set the goal again. */
						//robot.setGoal(robot.getGoal());
						logger.info("Aborted");
					}

					@Override
					public void callWhenNotValid() {
						state = RobotState.NEEDS_NEW_GOAL;
						logger.info("No valid path");
					}
				});
				// TODO Fix wenn Grid richtig funktioniert
				Position goalPos = new Position(goal.getxPosition(), 
																				goal.getyPosition(), 
																				calculateGoalYawn(goal.getxPosition(), goal.getyPosition()));
				this.setGoal(goalPos);
				grid.setRobotOnWayTo(this, goal);
				if (prevGpos == null) {
					grid.increaseToken(grid.getToken(gpos) + 1, gpos);
				} else {
					grid.increaseToken(
							Math.max(grid.getToken(prevGpos), grid.getToken(gpos)) + 1, gpos);
				}
				state = RobotState.ON_THE_WAY;
			}
		}

		checkForNewBarrels();
	}	
	
	public AntRobot(Device[] roboDevList) {
		super(roboDevList);
		state = RobotState.NEEDS_NEW_GOAL;
		getGripper().close(new IGripperListener(){
			@Override
			public void whenOpened() {}
			@Override
			public void whenClosed() {
				gripperState = RobotState.GRIPPER_CLOSE;
			}
			@Override
			public void whenLifted() {}
			@Override
			public void whenReleased() {}
			@Override
			public void whenClosedLifted() {}
			@Override
			public void whenReleasedOpened() {}
			@Override
			public void whenError() {}
			});
		planner = getPlanner();
		barrelPositions = Collections.synchronizedList(new ArrayList<double[]>());
		knownBarrels = new ArrayList<Barrel>();
		localizer = getLocalizer();
		localizer.addListener(new ILocalizeListener()
        {
            public void newPositionAvailable(Position newPose)
            {
                ownPosition = newPose;
                System.err.println("New position: "+newPose.toString());
            }
        });
		CommunicationFactory cf = new CommunicationFactory();
		try {
			map = cf.getSlaveMap(server, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grid = map.getGrid();
	}
	
	@Override
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	public void setPositions(ArrayList<GridPosition> positions) {
		this.positions = positions;
	}

	public GridPosition choose(ArrayList<GridPosition> positions, Grid grid) {
		List<GridPosition> result = new ArrayList<GridPosition>();
		for(GridPosition gpos : positions) {
			if(grid.isRobotOnWayToToken(gpos)) {
				continue;
			} else if(result.size() == 0) {
				result.add(gpos);
			} else if(grid.getToken(gpos) == grid.getToken(result.get(0))) {
				result.add(gpos);
			} else if(grid.getToken(gpos) < grid.getToken(result.get(0))) {
				result = new ArrayList<GridPosition>();
				result.add(gpos);
			}
		}
		if (result.size() != 0) {
			return result.get(rand.nextInt(result.size()));
		} else {
			return null;
		}
	}
}
