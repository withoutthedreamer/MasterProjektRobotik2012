package data;

import java.util.Date;

import simulator.Simulator;

// Blackboard note object
public class BbNote {

	protected Simulator simu = null;
	protected String key = "";
	protected Position pose = null;
	protected Position oldPose = null;

	protected Position goal = null;
	protected boolean isGoal = false;
	protected Trackable tracked = null;
	protected boolean completed = false;
	protected long lastChecked = 0;
	protected int timeout = 1000;
	protected double epsilon = 0.5; // meters
	
	public boolean isCompleted() {
		return completed;
	}

	public BbNote() {
//		lastChecked = new Date().getTime();
		// TODO test values
//		goal2 = new Position(-7,-7,0);
	}
	
	public void setPose( Position pose2 ) {
		this.pose = pose2;
	}
	public Position getPose() {
		return this.pose;
	}
	public Position getGoal() {
		return goal;
	}

	public void setGoal(Position goal) {
		this.goal = goal;
	}

	public void setTrackable (Trackable tracked2) {
		this.tracked = tracked2;
	}
	public Trackable getTrackable () {
		return this.tracked;
	}

	public void update() {
		// if there is a position, goal and something to bring you from,there..
		if (pose != null && goal != null && tracked != null) {
			// check if robot is already there
			boolean goalReached = goalReached();
			if ( goalReached ) {
				this.completed = true;
				// Set object in simulator
				if (simu != null && key != "") {
					simu.setObjectPos(key, new Position(-3, -5, 0));
					System.out.println("Setting " + key + " back");
				}
			} else {
				if ( timeout() ) {
					System.out.println("Current goal: " + tracked.getGoal().toString());
//					if ( tracked.getGoal().isEqualTo(goal)) {
					// Setting goal again
					tracked.setGoal(goal);
					System.out.println("Setting new goal: " + goal.toString());
					//				if( ! tracked.getGoal().isEqualTo(goal1)){
					//					tracked.setGoal(goal1);
//					}
				} else {
					// Do nothing
//					System.out.println("Goal is being processed: " + tracked.getGoal().toString());
				}
			}
			this.oldPose = tracked.getPosition();
		}
	}

	private boolean goalReached() {
		if ( pose != null && goal != null) {
			// get last robot position
			Position robotpos = this.tracked.getPosition();
			// Euclidean distance, Pythagoras
//			if (distance(robotpos, goal) < epsilon) {
			if (robotpos.distanceTo(goal) < epsilon) {
				System.out.println("Goal reached");
				return true;
			}
		}
		//		System.out.println("Goal not reached yet");
		return false;
	}

	private boolean timeout() {
		// get current time
		long now = new Date().getTime();

		// 1st time then timeout
		if (lastChecked == 0) {
			lastChecked = now;
			return true;
		}
		
		if ( (lastChecked + timeout) <= now) {
			lastChecked = now;
			// timeout
			// Check for pose change
			// get current position
			Position curPos = tracked.getPosition();
			if (curPos.isEqualTo(oldPose)) {
				// no progress done: timeout
				System.out.println("Timeout");
				return true;
			}
		}
		return false;
	}
//	private double distance(Position oldPose, Position newPose) {
//			// Euclidean distance, Pythagoras
//			return Math.sqrt(
//					Math.pow(
//							Math.abs(oldPose.getY()
//									-newPose.getY()),2)
//					+ Math.pow(
//							Math.abs(oldPose.getX()
//									-newPose.getX()),2));
//	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setSimu(Simulator simu) {
		this.simu = simu;
	}	
}
