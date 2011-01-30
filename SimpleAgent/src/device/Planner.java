package device;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner extends RobotDevice
{
	protected Position goal = null;
	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;
	private boolean isNewPose = false;
	private Position curPosition;
	private boolean isDone;
	private boolean isValidGoal;
	private boolean isCanceled;

	public Planner(DeviceNode roboClient, Device device) {
		super(roboClient, device);
		
		goal = new Position();
		curPosition = new Position();
		
		// enable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(1);
	}
	// Only to be called @~10Hz
		protected void update () {
			// TODO check if position is on map
			if (((javaclient3.PlannerInterface) device).isDataReady()) {
				// request recent planner data
				ppd = ((javaclient3.PlannerInterface) device).getData ();
	//			System.out.println (ppd.getWaypoints_count());

				if (isCanceled == true) {
					ppd.setDone((byte)1);
					ppd.setValid((byte)0);
				} else {
					// Check for a valid path
					if (ppd.getValid() > 0)
						isValidGoal = true;
					else
						isValidGoal = false;

					// Check if goal is achieved
					if (ppd.getDone() > 0)
						isDone = true;
					else
						isDone = false;
				}
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
		}
		public synchronized void setGoal (Position newGoal) {
		goal = newGoal;
		isNewGoal = true;
		isValidGoal = false;
		isDone = false;
		isCanceled = false;
	}
	public synchronized Position getGoal() {
		return goal;
	}
	public synchronized void setPosition(Position position) {
		curPosition = position;
		isNewPose = true;
	}
	public synchronized Position getPosition() {
		// Wait latest updates
//		while (isRunning() == true);
		return curPosition;
	}
	// TODO debug
	public boolean isDone() {
		return isDone;
	}
	public boolean isValidGoal() {
//		while (isRunning() == true);
		return isValidGoal;
	}
	public void removeGoal() {
		isCanceled  = true;
		isValidGoal = false;
		isDone = true;
	}
}
