package robot;

import data.Position;
import device.RangerLaser;

public class PioneerL extends Pioneer {

	public PioneerL(String name, int port, int id) throws IllegalStateException {
		
		super(name, port, id);
		
		try {
			laser = new RangerLaser (roboClient, id);

		} catch (Exception e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
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

	protected void shutdownDevices() {
		super.shutdownDevices();
		this.laser.thread.interrupt();
		while (this.laser.thread.isAlive());
	}

}
