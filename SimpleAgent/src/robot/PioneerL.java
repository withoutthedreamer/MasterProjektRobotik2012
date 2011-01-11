package robot;

import javaclient3.PlayerException;
import data.Position;
import device.RangerLaser;

public class PioneerL extends Pioneer {

	public PioneerL() {
		// TODO Auto-generated constructor stub
	}

	public PioneerL(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			this.laser = new RangerLaser (this.playerclient, this.id);

		} catch (PlayerException e) {
			System.err.println ("PioneerL: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new Exception();
		}
		super.playerclient.runThreaded (-1, -1);
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
