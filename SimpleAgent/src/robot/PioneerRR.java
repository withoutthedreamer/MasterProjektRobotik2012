package robot;

import data.Position;
import device.Gripper;
import device.Ranger;

final public class PioneerRR extends PioneerRso {
	
	Gripper grip = null;

	public PioneerRR(String name, int port, int id) throws IllegalStateException {
		
		super(name, port, id);
		
		try {
			laser = new Ranger (roboClient, super.id, 1);
			grip = new Gripper(roboClient, super.id);
			
			System.out.println ("Gripper state: " + grip.getState() );
			
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
		this.laser.thread.interrupt();
		while(this.laser.thread.isAlive());
		this.grip.thread.interrupt();
		while(this.grip.thread.isAlive());
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
