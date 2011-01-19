package device;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner extends PlayerDevice
{
	// TODO move to localize IF
	// initial values for the covariance matrix (c&p example from playernav)
	//protected double cov[] = { 0.5*0.5, 0.5*0.5, (Math.PI/6.0)*(Math.PI/6.0), 0, 0, 0 };

	protected Position goal = null;
	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;
	private boolean isNewPose = false;
	private Position curPosition;

	public Planner(RobotClient roboClient, Device device) {
		super(roboClient, device);
		
		goal = new Position(6,6,6);
		curPosition = new Position(6,6,6);
		
		// enable motion
		((javaclient3.PlannerInterface) this.device).setRobotMotion(1);
	}
	public void setGoal (Position newGoal) {
		goal = newGoal;
		isNewGoal = true;
	}
	public Position getGoal() {
		return goal;
	}
	// Only to be called @~10Hz
	protected void update () {
		// TODO check if position is on map
		if (((javaclient3.PlannerInterface) device).isDataReady()) {
			// request recent planner data
			ppd = ((javaclient3.PlannerInterface) device).getData ();
//			System.out.println (ppd.getWaypoints_count());

			// set position belief
			// has to be before over writing curPosition!
			if(isNewPose) {
				isNewPose = false;
//				loci.setPose (locPose);	
				ppd.setPos(new PlayerPose(
						curPosition.getX(),
						curPosition.getY(),
						curPosition.getYaw()));
			}
			// Update current position belief
			curPosition.setX(ppd.getPos().getPx());
			curPosition.setY(ppd.getPos().getPy());
			curPosition.setYaw(ppd.getPos().getPa());
		}
		// update goal
		if(isNewGoal) {
			isNewGoal = false;
			((javaclient3.PlannerInterface) device).setGoal(new PlayerPose(
					goal.getX(),
					goal.getY(),
					goal.getYaw()));
		}
	}

	public synchronized void setPose(Position position) {
		curPosition = position;
		isNewPose = true;
	}
	public synchronized Position getPose() {
		return curPosition;
	}
}
