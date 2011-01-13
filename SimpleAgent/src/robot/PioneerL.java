package robot;

import data.Position;
import device.RangerLaser;

public class PioneerL extends Pioneer {

	public PioneerL(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		laser = new RangerLaser (roboClient, id);
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
