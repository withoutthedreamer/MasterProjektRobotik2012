package robot;

import data.Position;
import device.Ranger;

public class PioneerRso extends Pioneer {

	public PioneerRso(String name, int port, int id) throws IllegalStateException {
		
		super(name, port, id);
		
		try {
			sonar = new Ranger(roboClient, this.id, 0);

		} catch (Exception e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
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
