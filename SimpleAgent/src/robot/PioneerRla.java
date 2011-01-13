package robot;

import javaclient3.PlayerException;
import data.Position;
import device.Ranger;

public class PioneerRla extends Pioneer {
	
	public PioneerRla(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			laser = new Ranger(roboClient, id, 1);
//			Pioneer.isDebugDistance = true;

		} catch (PlayerException e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new IllegalStateException();
		}
//		super.playerclient.runThreaded (-1, -1);
	}

	public void shutdownDevices () {
		laser.thread.interrupt();
		while (laser.thread.isAlive());
	}

	/// Return robot position
	public final Position getPosition() {
		return null;
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
//	public void update() {
//		// do nothing
//	}
}
