package robot;

import data.Position;
import device.RangerLaser;

public class PioneerSL extends PioneerS {

	public PioneerSL(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		laser = new RangerLaser (roboClient, id);
	}

	protected void shutdownDevices() {
		super.shutdownDevices();
		this.laser.thread.interrupt();
		while(this.laser.thread.isAlive());
		this.sonar.thread.interrupt();
		while(this.sonar.thread.isAlive());
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
}
