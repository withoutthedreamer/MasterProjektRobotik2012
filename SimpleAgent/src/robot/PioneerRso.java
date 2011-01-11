package robot;

import javaclient3.PlayerException;
import data.Position;
import device.Ranger;

public class PioneerRso extends Pioneer {

	public PioneerRso(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			sonar = new Ranger(this.playerclient, this.id, 0);

		} catch (PlayerException e) {
			System.err.println ("PioneerL: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new IllegalStateException();
		}
		super.playerclient.runThreaded (-1, -1);
	}

	public void shutdownDevices () {
		sonar.thread.interrupt();
		while (sonar.thread.isAlive());
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
