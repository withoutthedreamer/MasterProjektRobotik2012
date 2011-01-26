package data;

public class Goal
{
	boolean isObsolete;
	Position position;
	double cost;

	public Goal() {}
	
	public Goal(Position newPosition) {
		position = newPosition;
		isObsolete = false;
	}
	
	public void setPosition (Position newPosition) {
		position = newPosition;
		isObsolete = false;
	}

	public Position getPosition() {
		return position;
	}
	public void setIsObsolete(){
		isObsolete = true;
		position = null;
	}
	
	public boolean getIsObsolete(){
		return isObsolete;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
}
