package robot;

import data.Position;
import device.RangerSonar;

public class PioneerS extends Pioneer {

	public PioneerS(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		sonar = new RangerSonar (roboClient, id);
	}

	protected void shutdownDevices() {
		super.shutdownDevices();
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
