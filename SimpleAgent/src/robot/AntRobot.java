package robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import data.Position;
import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.exploration.Exploration;
import de.unihamburg.informatik.tams.project.communication.exploration.Grid;
import de.unihamburg.informatik.tams.project.communication.exploration.GridPosition;
import device.Device;
import device.Planner;
import device.external.IPlannerListener;

public class AntRobot extends PatrolRobot implements Exploration {

	private Random rand = new Random();
	
	private data.Position ownPosition = this.getPosition();
	
	private Grid grid;
	GridPosition prevGpos;
	GridPosition gpos;
	GridPosition goal;
	List<GridPosition> positions;
	Planner planner;
	
	@Override
	public void doStep() {
		if(state == RobotState.NEEDS_NEW_GOAL) {
			ownPosition = this.getPosition();
			position = new MapPosition((int)ownPosition.getX(), (int)ownPosition.getY());
			prevGpos = gpos;
			gpos = grid.getOwnPosition(position);

			positions = new ArrayList<GridPosition>();
		
			positions.add(new GridPosition(gpos.getxPosition()-1, gpos.getyPosition())); // north
			positions.add(new GridPosition(gpos.getxPosition(), gpos.getyPosition()-1)); // west
			positions.add(new GridPosition(gpos.getxPosition()+1, gpos.getyPosition())); // south
			positions.add(new GridPosition(gpos.getxPosition(), gpos.getyPosition()+1)); // east
			
			goal = choose();
			grid.setRobotOnWayTo(this, goal);
			planner.setGoal(new Position(goal.getxPosition(), goal.getyPosition(), 0));
			grid.increaseToken(Math.max(grid.getToken(prevGpos), grid.getToken(gpos))+1, gpos);
			state = RobotState.ON_THE_WAY;
			
			// TODO Does this work? Does planner have a reference to state?
			planner.addIsDoneListener(new IPlannerListener() {
				@Override public void callWhenIsDone() {
					state = RobotState.NEEDS_NEW_GOAL;
				}

				@Override public void callWhenAbort() {
					/** Set the goal again. */
					//robot.setGoal(robot.getGoal());
					logger.info("Aborted");
				}

				@Override public void callWhenNotValid() {
					logger.info("No valid path");
				}
			});
		}
	}	
	
	public AntRobot(Device[] roboDevList) {
		super(roboDevList);
		state = RobotState.NEEDS_NEW_GOAL;
		planner = getPlanner();
		doStep();
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	private GridPosition choose() {
		List<GridPosition> result = new ArrayList<GridPosition>();
		for(GridPosition gpos : positions) {
			if(result.size() == 0) {
				result.add(gpos);
			} else if(!grid.isRobotOnWayToToken(gpos) && grid.getToken(gpos) == grid.getToken(result.get(0))) {
				result.add(gpos);
			} else if(!grid.isRobotOnWayToToken(gpos) && grid.getToken(gpos) < grid.getToken(result.get(0))) {
				result = new ArrayList<GridPosition>();
				result.add(gpos);
			}
		}
		return result.get(rand.nextInt(result.size()));
	}
}
