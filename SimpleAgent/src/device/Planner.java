package device;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner extends RobotDevice
{
	protected Position goal = null;
	protected Position globalGoal = null;

	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;
	private boolean isNewPose = false;
	private Position curPosition;
	private boolean isDone;
	private boolean isValidGoal;
	private boolean isCanceled;
	int wayPointCount;
	int wayPointIndex;
	
	boolean isDebug;

	public Planner(DeviceNode roboClient, Device device) {
		super(roboClient, device);
		
		goal = new Position();
		globalGoal = new Position();
		curPosition = new Position();
		
		this.setSleepTime(1000);
		
		// disable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(0);
	}
	// Only to be called @~10Hz
		protected void update () {
			// TODO check if position is on map
			if (((javaclient3.PlannerInterface) device).isDataReady()) {
				// request recent planner data
				ppd = ((javaclient3.PlannerInterface) device).getData ();
	//			System.out.println (ppd.getWaypoints_count());
				
				if (isCanceled == true) {
					ppd.setDone(new Integer(1).byteValue());
					ppd.setValid(new Integer(0).byteValue());
				} else {
//					// Check for a valid path
//					if (ppd.getValid() == new Integer(1).byteValue())
//						isValidGoal = true;
//					else
//						isValidGoal = false;
//
//					// Check if goal is achieved
//					if (ppd.getDone() == new Integer(1).byteValue())
//						isDone = true;
//					else
//						isDone = false;
				}
				
				wayPointCount = ppd.getWaypoints_count();
				wayPointIndex = ppd.getWaypoint_idx();
				
				// set position belief
				// has to be before over writing curPosition!
				if(isNewPose) {
					isNewPose = false;
					ppd.setPos(new PlayerPose(
							curPosition.getX(),
							curPosition.getY(),
							curPosition.getYaw()));
				} else {
					PlayerPose poseTemp = ppd.getPos();
					// Update current position belief
					curPosition.setX(poseTemp.getPx());
					curPosition.setY(poseTemp.getPy());
					curPosition.setYaw(poseTemp.getPa());
					ppd.setPos(poseTemp);
				}
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
			if(isDebug == true){
				System.err.print("WPCnt: "+this.wayPointCount);
				System.err.print(" WPIdx: "+this.wayPointIndex);
				System.err.print(" CurGoal: "+this.goal.toString());
				System.err.print(" CurPos: "+this.curPosition.toString());
				System.err.print(" IsDone: "+this.isDone);
				System.err.println(" IsValid: "+this.isValidGoal);
			}
			if (curPosition.isNearTo(goal)) {
				isDone = true;
			}
		}
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
	public synchronized void setPosition(Position position) {
		curPosition.setPosition(position);
		isNewPose = true;
	}
	public synchronized Position getPosition() {
		// Wait latest updates
//		while (isRunning() == true);
		return new Position(curPosition);
	}
	// TODO debug
	// taken from wavefront.cc
	public boolean isDone() {
//		if (wayPointCount > 0 && wayPointIndex < 0)
//		if (isCanceled == true || goal.isNearTo(globalGoal))
//			return true;
//		else
//			return false;
		return isDone;
	}
	public boolean isValidGoal() {
//		if (wayPointCount > 0)
//			return true;
//		else
//			return false;
		return isValidGoal;
	}
	public void cancel() {
		isCanceled  = true;
		isValidGoal = false;
		isDone = true;
		wayPointCount = 0;
		wayPointIndex = -1;
		globalGoal.setPosition(0,0,0);
		goal.setPosition(0,0,0);
		// disable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(0);
	}
	public boolean isDebug() {
		return isDebug;
	}
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
}
