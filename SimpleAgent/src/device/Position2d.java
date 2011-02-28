package device;

import data.Position;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.position2d.PlayerPosition2dData;
import javaclient3.Position2DInterface;

/**
 * 2D Position device of a robot.
 * @author sebastian
 */
public class Position2d extends RobotDevice
{
	Position pos;
	
	double speed = 0.;
	double turnrate = 0.;
	Position setOdometry;
	boolean isNewSpeed = false;
	
	/**
	 * Creates a Position2d object.
	 * @param roboClient The @see DeviceNode this device belongs to.
	 * @param device This device's information.
	 */
	public Position2d(DeviceNode roboClient, Device device) {
		super(roboClient, device);
		pos = new Position();
	}
	/**
	 * Updates the position device's settings at ~10 Hz
	 * Is only called by the run() method!
	 */
	protected void update()
	{
		/** Check for sonar readings ready */
		if ( ((Position2DInterface) device).isDataReady() )
		{
			/** Update odometry if updated externally */
			if (setOdometry != null)
			{				
				((Position2DInterface) device).setOdometry(new PlayerPose(
						setOdometry.getX(),
						setOdometry.getY(),
						setOdometry.getYaw()));
				setOdometry = null;
			}
			else
			{
				/** Request current position */
				PlayerPosition2dData poseData = ((Position2DInterface) device).getData();
				if (poseData != null)
				{
					PlayerPose pose = poseData.getPos();
					if (pose != null)
					{
						pos.setX(pose.getPx());
						pos.setY(pose.getPy());
						pos.setYaw(pose.getPa());
					}
				}
			}
			/** Set new speed */
			if (isNewSpeed == true)
			{
				isNewSpeed = false;
				((Position2DInterface) device).setSpeed(speed, turnrate);
			}
		}
	}
	/**
	 * 
	 * @return Last known robot position.
	 */
	public Position getPosition()
	{
		return new Position(pos);
	}
	/**
	 * 
	 * @param pos New robot @ref Position for odometry device.
	 */
	public void setPosition (Position pos)
	{
		if (pos != null)
		{
			setOdometry = new Position(pos);
		}
	}
	/**
	 * 
	 * @return Robot speed (m/s).
	 */
	public double getSpeed()
	{
		return speed;
	}
	/**
	 * Sets the motors planar speed.
	 * @param speed New robot speed (m/s).
	 */
	public void setSpeed (double speed)
	{
   	    isNewSpeed = true;
		this.speed = speed;
	}
	/**
	 * Sets the motors turnrate.
	 * @param turnrate New robot turnrate (rad/s).
	 */
	public void setTurnrate (double turnrate)
	{
		isNewSpeed = true;
		this.turnrate = turnrate;
	}
	/**
	 * 
	 * @return Last known robot turnrate (rad/s).
	 */
	public double getTurnrate()
	{
		return turnrate;
	}
	/**
	 * Enable the robots motors.
	 */
	public void enableMotor()
	{
        ((Position2DInterface) device).setMotorPower(1);
	}
	/**
	 * Disable the robots motors.
	 */
	public void disableMotor()
	{
        ((Position2DInterface) device).setMotorPower(0);
	}
}
