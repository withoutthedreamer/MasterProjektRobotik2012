package device;

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
public class Position2d implements Runnable{
	protected Position2DInterface posi  = null;
	protected Position pos = null;
	protected final int SLEEPTIME = 100;
	
	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	private double speed = 0.;
	private double turnrate = 0.;
	private Position setOdometry = null;
	/**
	 * Robot id binding this device
	 */
	private int id = -1;
	
	/**
	 * Constructor creating 2 Position2d device.
	 * @param host Name of the host running the player server.
	 * @param id Robot id.
	 */
	// Host id
	public Position2d (PlayerClient host, int id) {
		try {
			this.posi = host.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			this.id = id;

			// Automatically start own thread in constructor
			this.thread.start();

			System.out.println("Running "
					+ this.toString()
					+ " of robot "
					+ this.id
					+ " in thread "
					+ this.thread.getName());

		} catch ( PlayerException e ) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
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
		while ( ! this.thread.isInterrupted()) {
			this.update ();
		}
		System.out.println("Shutdown of "
				+ this.toString()
				+ " of robot "
				+ id 
				+ " in thread "
				+ this.thread.getName());
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
