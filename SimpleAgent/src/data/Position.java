package data;

// Represents a 2D position in the simulator
public class Position {

	protected double x  = 0.;
	protected double y  = 0.;
	protected double yaw= 0.;
	
	public Position(double x, double y, double yaw) {
		this.x  = x;
		this.y  = y;
		this.yaw= yaw;
	}
	
	public void setPosition (Position pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.yaw = pos.getYaw();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	public String toString() {
		return String.format("(%5.2f,%5.2f,%3.0f)",this.x, this.y, Math.toDegrees(this.yaw));
	}
}
