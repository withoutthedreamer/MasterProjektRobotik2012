package data;

public class BoardObject {
	
	Goal goal;
	Position position;
	double distanceGoal;
	String topic;
	long timestamp;
	long timeout = 3600000;
	boolean isDone = false;
	
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

	public String getTopic() {
		return topic;
	}

	public void setTopic(String name) {
		updateAccessTime();
		this.topic = name;
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

    /**
     * @return the isDone
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * @param isDone the isDone to set
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
}
