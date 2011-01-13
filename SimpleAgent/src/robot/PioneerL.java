package robot;

import javaclient3.PlayerException;
import data.Position;
import device.RangerLaser;
import device.RobotClient;

public class PioneerL extends Pioneer {

	public PioneerL(String name, int port, int id) throws Exception {
		
		roboClient = new RobotClient(name, port, id);
		
		try {
			laser = new RangerLaser (roboClient, id);

		} catch (PlayerException e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
		roboClient.runThreaded();
	}

	@Override
	public void setGoal(Position goal) {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getGoal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdownDevices() {
		this.laser.thread.interrupt();
		while (this.laser.thread.isAlive());
	}

}
