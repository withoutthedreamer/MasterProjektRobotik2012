package robot;

import data.Position;
import device.RangerSonar;

public class PioneerS extends Pioneer {

	public PioneerS(String name, int port, int id) throws IllegalStateException {
		
		super(name, port, id);
		
		try {
			sonar = new RangerSonar (roboClient, id);
		} catch (Exception e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
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
