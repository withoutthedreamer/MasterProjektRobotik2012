package device;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.position2d.PlayerPosition2dData;

/**
 * Position in 2D device of a robot.
 * @author sebastian
 *
 */
public class Position2d extends PlayerDevice
{
	Position pos = null;
	
	private double speed = 0.;
	private double turnrate = 0.;
	private Position setOdometry = null;
	private boolean isNewSpeed = false;
		
	public Position2d(RobotClient roboClient, Device device) {
		super(roboClient, device);
	}
	/**
	 * Updates the position device's settings at ~10 Hz
	 * Is only called by the run() method!
	 */
	protected void update() {
		// Check for sonar readings ready
		if ( ! ((javaclient3.Position2DInterface) device).isDataReady() ){
		} else {
			// Request current position
			PlayerPosition2dData poseData = ((javaclient3.Position2DInterface) device).getData();
				pos = new Position(poseData.getPos().getPx(),
						poseData.getPos().getPy(),
						poseData.getPos().getPa());
				
			// Update odometry if updated externally
			if (setOdometry != null) {				
				((javaclient3.Position2DInterface) device).setOdometry(new PlayerPose(
						setOdometry.getX(),
						setOdometry.getY(),
						setOdometry.getYaw()));
				setOdometry = null;
			}
			// Set new speed
			if (isNewSpeed == true) {
				isNewSpeed = false;
				((javaclient3.Position2DInterface) device).setSpeed(speed, turnrate);
			}
		}
	}
	/**
	 * 
	 * @return Last known robot position.
	 */
	public Position getPosition() {
		return this.pos;
	}
	/**
	 * 
	 * @param pos New robot @ref Position for odometry device.
	 */
	public void setPosition (Position pos) {
//		this.pos = pos;
		setOdometry = pos;
	}
	/**
	 * 
	 * @return Robot speed (m/s).
	 */
	public double getSpeed() {
		return speed;
	}
	/**
	 * 
	 * @param speed New robot speed (m/s).
	 */
	public void setSpeed (double speed) {
		isNewSpeed = true;
		this.speed = speed;
	}
	/**
	 * 
	 * @param turnrate New robot turnrate (rad/s).
	 */
	public void setTurnrate (double turnrate) {
		isNewSpeed = true;
		this.turnrate = turnrate;
	}
	/**
	 * 
	 * @return Last known robot turnrate (rad/s).
	 */
	public double getTurnrate() {
		return turnrate;
	}
}
