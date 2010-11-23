package data;

import java.util.Date;

// Blackboard note object
public class BbNote {

	protected Position pose = null;
	protected Position oldPose = null;

	protected Position goal1 = null;
	protected Position goal2 = null;
	protected boolean isGoal1 = false;
	protected boolean isGoal2 = false;
	protected Trackable tracked = null;
	protected boolean completed = false;
	protected long lastChecked = 0;
	protected int timeout = 1000;
	protected double epsilon = 1.;
	
	public boolean isCompleted() {
		return completed;
	}

	public BbNote() {
		lastChecked = new Date().getTime();
		// TODO test values
		goal2 = new Position(-7,-7,0);
	}
	
	public void setPose( Position pose2 ) {
		this.pose = pose2;
	}
	public Position getPose() {
		return this.pose;
	}
	public Position getGoal() {
		return goal1;
	}

	public void setGoal(Position goal) {
		this.goal1 = goal;
	}

	public void setTrackable (Trackable tracked2) {
		this.tracked = tracked2;
	}
	public Trackable getTrackable () {
		return this.tracked;
	}

	public void update() {
		// if there is a position, goal and something to bring you from,there..
		if (pose != null && goal1 != null && tracked != null) {
			if (timeout() && ! goalReached() ) {
				tracked.setGoal(goal1);
			}
		}
	}

	private boolean timeout() {
		long now = new Date().getTime();
		
		if ( (this.lastChecked + timeout) >= now &&
				! posChanged() ) {
			return true;
		} else {
			this.lastChecked = new Date().getTime();
			return false;
		}
	}

	private boolean posChanged() {
		if(distance(oldPose, tracked.getPosition()) >= epsilon) {
			return true;
		}
		return false;
	}

	private double distance(Position oldPose, Position newPose) {
		// Euclidean distance, Pythagoras
		return Math.sqrt(
				Math.pow(
						Math.abs(oldPose.getY()
								-newPose.getY()),2)
				+ Math.pow(
						Math.abs(oldPose.getX()
								-newPose.getX()),2));
	}

	private boolean goalReached() {
		if ( pose != null && goal1 != null) {
			// get last robot position
			Position robotpos = this.tracked.getPosition();
			// Euclidean distance, Pythagoras
			if (distance(robotpos, goal1) < epsilon) {
//				this.completed = true;
				// set goal for basket
				this.pose = this.goal2;
				return true;
			}
		}
		return false;
	}

}
