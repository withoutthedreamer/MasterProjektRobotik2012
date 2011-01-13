package device;

import core.Logger;
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
public class RobotClient extends Device {

	// Required to every player robot
	protected PlayerClient playerclient = null;
//	protected Position2d posi = null;
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
	public RobotClient (String name, int port, int clientId) throws IllegalStateException
	{
		super(clientId);
		try {
			id = clientId;

			// Connect to the Player server and request access to Position
			playerclient  = new PlayerClient (name, port);
			Logger.logActivity(false, "Running", this.toString(), id, null);

			// Always needs a position device
//			posi = new Position2d(playerclient, id);

		} catch (PlayerException e) {
//			System.err.println ("    [ " + e.toString() + " ]");
			Logger.logActivity(true, "Connecting", this.toString(), id, null);
//			throw new IllegalStateException();
		}
	}
	/**
	 * Shutdown robot client and clean up
	 */
	public void shutdown () {
		// Cleaning up
//		this.posi.thread.interrupt();
//		while (this.posi.thread.isAlive());
		this.playerclient.close();
		while (this.playerclient.isAlive());
		Logger.logActivity(false, "Shutdown", this.toString(), id, null);
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
}
