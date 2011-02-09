package device;

import java.util.logging.Logger;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner extends RobotDevice
{

    // Logging support
    private Logger logger = Logger.getLogger (Planner.class.getName ());

    protected Position goal = null;
	protected Position globalGoal = null;

	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;
//	private boolean isNewPose = false;
	private Position curPosition;
	private boolean isDone;
	private boolean isValidGoal;
	private boolean isCanceled;
	int wayPointCount;
	int wayPointIndex;
	
	public Planner(DeviceNode roboClient, Device device) {
		super(roboClient, device);
		
		goal = new Position();
		globalGoal = new Position();
		curPosition = new Position();
		
		setSleepTime(500);
		
		// disable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(0);
	}
	// Only to be called @~10Hz
		protected void update () {
			// TODO check if position is on map
			if (((javaclient3.PlannerInterface) device).isDataReady()) {
				// request recent planner data
				ppd = ((javaclient3.PlannerInterface) device).getData ();
				
				if (isCanceled == true) {
					ppd.setDone(new Integer(1).byteValue());
					ppd.setValid(new Integer(0).byteValue());
				} else {
					// Check for a valid path
					if (ppd.getValid() == new Integer(1).byteValue())
						isValidGoal = true;
					else
						isValidGoal = false;

					// Check if goal is achieved
					if (ppd.getDone() == new Integer(1).byteValue())
						// Check if really at the goal position
						if (globalGoal.isNearTo(goal))
							isDone = true;
						else
							setGoal(globalGoal);
					else
						isDone = false;
				}
				
				wayPointCount = ppd.getWaypoints_count();
				wayPointIndex = ppd.getWaypoint_idx();
				
//				// set position belief
//				// has to be before over writing curPosition!
//				if(isNewPose) {
//					isNewPose = false;
//					ppd.setPos(new PlayerPose(
//							curPosition.getX(),
//							curPosition.getY(),
//							curPosition.getYaw()));
//				} else {
					PlayerPose poseTemp = ppd.getPos();
					// Update current position belief
					if (poseTemp != null) {
						curPosition.setX(poseTemp.getPx());
						curPosition.setY(poseTemp.getPy());
						curPosition.setYaw(poseTemp.getPa());
					}
//				}
			}
			// update goal
			if(isNewGoal) {
				isNewGoal = false;
				((javaclient3.PlannerInterface) device).setGoal(new PlayerPose(
						goal.getX(),
						goal.getY(),
						goal.getYaw()));
			} else { // Get current goal
				if (((javaclient3.PlannerInterface) device).isReadyWaypointData() == true) {
					PlayerPose poseTemp = ((javaclient3.PlannerInterface) device).getData().getGoal();
					goal.setX(poseTemp.getPx());
					goal.setY(poseTemp.getPy());
					goal.setYaw(poseTemp.getPa());

				}
			}
			logger.fine(
					"WPCnt: "+this.wayPointCount
					+" WPIdx: "+this.wayPointIndex
					+" CurGoal: "+this.goal.toString()
					+" CurPos: "+this.curPosition.toString()
					+" IsDone: "+this.isDone
					+" IsValid: "+this.isValidGoal);
			
		}
		// TODO check if valid goal
		// TODO callback when there
		public synchronized void setGoal (Position newGoal) {
		// New Positions and copy
		goal = new Position(newGoal);
		globalGoal = new Position(newGoal);
		isNewGoal = true;
		isValidGoal = false;
		isDone = false;
		isCanceled = false;
		// enable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(1);
	}
	public synchronized Position getGoal() {
		return goal;
	}
	/**
	 * @depreciated Use @Localize#setPosition instead.
	 * @param position
	 */
	public synchronized void setPosition(Position position) {
//		curPosition.setPosition(position);
//		isNewPose = true;
	}
	public synchronized Position getPosition() {
		return new Position(curPosition);
	}
	public boolean isDone() {
		return isDone;
	}
	public boolean isValidGoal() {
		return isValidGoal;
	}
	public int getWayPointCount() {
		return wayPointCount;
	}
	public int getWayPointIndex() {
		return wayPointIndex;
	}
	/**
	 * Stops approaching the current goal (if any).
	 * Does not remove goal from goal stack but stops robot motion.
	 */
	public void stop() {
		isCanceled  = true;
		isValidGoal = false;
		isDone = true;
		// disable motion
		((javaclient3.PlannerInterface) device).setRobotMotion(0);
	}
	/**
	 * Returns the current cost to current goal position.
	 * @return The cost value.
	 */
	public double getCost() {
		try { Thread.sleep(getSleepTime()*2); } catch (InterruptedException e) { /* e.printStackTrace();*/ }

		if (isDone() == true || isValidGoal() == false){
			return -1.0;
		} else {
			logger.info("wayPointCount: "+wayPointCount+" wayPointIndex: "+wayPointIndex+" distance: "+globalGoal.distanceTo(curPosition));
			return (1 + wayPointCount - wayPointIndex) * globalGoal.distanceTo(curPosition);
		}
	}
	/**
	 * Returns the cost if the goal would be the given position.
	 * The planner does not actually approach the given goal but
	 * resumes the previous set one (if any).
	 * @param toPosition @see Position to calculate the cost.
	 * @return The cost value.
	 */
	public double getCost(Position toPosition) {
		Position oldGoal = globalGoal;
		double cost;
		stop();
		
		setGoal(toPosition);
		cost = getCost();
		stop();
		
		setGoal(oldGoal);
		
		return cost;
	}
	public Logger getLogger() {
		return logger;
	}
}
