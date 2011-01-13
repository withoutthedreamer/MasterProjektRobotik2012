package device;

import data.Position;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;

/**
 * Client API to the robot server.
 * Whatever the server is, this class is the basic interface
 * onto all other robot devices will be hooked.
 * @author sebastian
 *
 */
public class RobotClient {

	// Required to every player robot
	protected PlayerClient playerclient = null;
	protected Position2d posi = null;
	protected int id = -1;
	
	protected double speed = 0.;
	protected double turnrate = 0.;
	protected Position odometry = null;
	
	/**
	 * Constructor for a RobotClient.
	 * @param name The host name where the server is to connect to.
	 * @param port The port of the server listening for a client.
	 * @param clientId Robot id.
	 * @throws Exception
	 */
	public RobotClient (String name, int port, int clientId) throws Exception
	{
		try {
			id = clientId;

			// Connect to the Player server and request access to Position
			playerclient  = new PlayerClient (name, port);
			System.out.println("Running playerclient of "
					+ this.toString()
					+ " of robot "
					+ id
					+ " in  "
					+ playerclient.getName());

			// Always needs a position device
			posi = new Position2d(playerclient, id);

		} catch (PlayerException e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
	}
	/**
	 * Shutdown robot client and clean up
	 */
	public void shutdown () {
		// Cleaning up
		this.posi.thread.interrupt();
		while (this.posi.thread.isAlive());
		this.playerclient.close();
		while (this.playerclient.isAlive());
		System.out.println("Shutdown playerclient of robot "
				+ id
				+ " in "
				+ playerclient.getName());
	}
	/**
	 * 
	 * @return PlayerClient API
	 */
	public PlayerClient getClient() {
		return playerclient;
	}
	/**
	 * Start PlayerClient thread.
	 * Has to be called in object constructor!
     * Otherwise program will block forever
		 * This call has to be after all device requests!
	 */
	public void runThreaded() {
		playerclient.runThreaded (-1, -1);
	}
	/**
	 * Returns robot speed.
	 * @return Last known speed of the robot (m/s).
	 */
	public double getSpeed() {
		return posi.getSpeed();
	}
	/**
	 * Set new speed of the robot (m/s).
	 * @param speed New speed of the robot.
	 */
	public void setSpeed(double speed) {
		posi.setSpeed(speed);
	}
	/**
	 * Returns robot turnrate.
	 * @return Last known robot turnrate (rad/s).
	 */
	public double getTurnrate() {
		return posi.getTurnrate();
	}
	/**
	 * Set new turnrate of the robot (rad/s).
	 * @param turnrate New turnrate of the robot.
	 */
	public void setTurnrate(double turnrate) {
		posi.setTurnrate(turnrate);
	}
	/**
	 * Returns robot last known Position.
	 * The Position is based on the robots odometry device.
	 * @return @ref Position of current odometry values.
	 */
	public Position getOdometry() {
		return posi.getPosition();
	}
	/**
	 * Set new odometry values of the robot odometry device.
	 * @param odometry New @ref Position of odometry device.
	 */
	public void setOdometry(Position odometry) {
		posi.setPosition(odometry);
	}	
}
