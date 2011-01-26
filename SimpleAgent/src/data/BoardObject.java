package data;

public class BoardObject {
	
	Goal goal;
	double distanceGoal;
	
	public BoardObject(){}
	
	public BoardObject (Goal newGoal) {
		goal = newGoal;
	}

	public double getDistanceGoal() {
		return distanceGoal;
	}

	public void setDistanceGoal(int distanceGoal) {
		this.distanceGoal = distanceGoal;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

}
