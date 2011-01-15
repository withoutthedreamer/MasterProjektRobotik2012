package data;

/**
 * Represents a 2D position in the simulator
 * @author sebastian
 */
public class Position {

	protected double x  = 0.;
	protected double y  = 0.;
	protected double yaw= 0.;
	protected final static double epsilonPos = 0.01;
	protected final static double epsilonRot = Math.toRadians(0.1);

	/**
	 * Constructor creates a Position
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param yaw Orientation in radians.
	 */
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
	public boolean isEqualTo(Position pose){
		if (pose != null) {
			if ((Math.abs(x   - pose.getX())   < epsilonPos) &&
				(Math.abs(y   - pose.getY())   < epsilonPos) &&
				(Math.abs(yaw - pose.getYaw()) < epsilonRot)   ) {
				return true;
			}
		}
		return false;
	}

	public double distanceTo(Position position) {
		if (position != null) {
			// Euclidean distance, Pythagoras
			return Math.sqrt(
					Math.pow(
						Math.abs(y-position.getY()),2)
					+ Math.pow(
						Math.abs(x-position.getX()),2));
		} else {
			return -1;
		}
	}
}
