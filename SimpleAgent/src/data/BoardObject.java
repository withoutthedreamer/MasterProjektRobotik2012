package data;

public class BoardObject {
	
	Goal goal;
	Position position;
	double distanceGoal;
	String name;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

}
