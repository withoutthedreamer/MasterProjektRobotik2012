package device;

import core.Logger;
import data.Position;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.position2d.PlayerPosition2dData;

/**
 * Position in 2D device of a robot.
 * @author sebastian
 *
 */
public class Position2d extends Device implements Runnable{
	protected Position2DInterface posi  = null;
	protected Position pos = null;
	protected final int SLEEPTIME = 100;
	
	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	private double speed = 0.;
	private double turnrate = 0.;
	private Position setOdometry = null;
		
	/**
	 * Constructor creating 2 Position2d device.
	 * @param host Name of the host running the player server.
	 * @param id Robot id.
	 */
	// Host id
	public Position2d (PlayerClient host, int id) {
		super(id);
		try {
			this.posi = host.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			this.id = id;

			// Automatically start own thread in constructor
			this.thread.start();
			Logger.logActivity(false, "Running", this.toString(), id, thread.getName());

		} catch ( PlayerException e ) {
//			System.err.println ("    [ " + e.toString() + " ]");
			Logger.logActivity(true, "Connecting", this.toString(), id, thread.getName());
			throw new IllegalStateException();
		}
	}
	/**
	 * Updates the position device's settings at ~10 Hz
	 * Is only called by the run() method!
	 */
		// Wait for sonar readings
	protected void update() {
		if ( ! posi.isDataReady() ){
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		} else {
			// Request current position
			PlayerPosition2dData poseData = posi.getData();
				pos = new Position(poseData.getPos().getPx(),
						poseData.getPos().getPy(),
						poseData.getPos().getPa());
				
			// Update odometry if set externally
			if (setOdometry != null) {				
				posi.setOdometry(new PlayerPose(
						setOdometry .getX(),
						setOdometry.getY(),
						setOdometry.getYaw()));
				setOdometry = null;
			}
			// Set new speed
			this.posi.setSpeed(this.speed, this.turnrate);
		}
	}
	@Override
	public void run() {
		while ( ! thread.isInterrupted()) {
			this.update ();
		}
		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
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
		this.speed = speed;
	}
	/**
	 * 
	 * @param turnrate New robot turnrate (rad/s).
	 */
	public void setTurnrate (double turnrate) {
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
