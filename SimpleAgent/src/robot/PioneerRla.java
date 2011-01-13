package robot;

import data.Position;
import device.Ranger;

public class PioneerRla extends Pioneer {
	
	public PioneerRla(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		laser = new Ranger(roboClient, id, 1);
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
		laser.thread.interrupt();
		while (laser.thread.isAlive());
	}

	/// Return robot position
	public Position getPosition() {
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
