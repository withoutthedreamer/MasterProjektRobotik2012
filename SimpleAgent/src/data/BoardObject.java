package data;

public class BoardObject {
	
	Goal goal;
	Position position;
	double distanceGoal;
	String name;
	long timestamp;
	long timeout = 3600000;
	
	public BoardObject(){
		updateAccessTime();
	}
	
	public BoardObject (Goal newGoal) {
		this();
		goal = newGoal;
	}

	public double getDistanceGoal() {
		return distanceGoal;
	}

	public void setDistanceGoal(int distanceGoal) {
		updateAccessTime();
		this.distanceGoal = distanceGoal;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		updateAccessTime();
		this.goal = goal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		updateAccessTime();
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		updateAccessTime();
		this.position = position;
	}
	void updateAccessTime() {
		timestamp = System.currentTimeMillis();		
	}

	/**
	 * @return The timestamp when this @see BoardObject was last accessed (write).
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
