package robot;

import data.Position;
import device.Gripper;
import device.Ranger;
import device.RobotClient;

import javaclient3.PlayerException;

final public class PioneerRR extends Pioneer {
	
	Gripper grip = null;

	public PioneerRR(String name, int port, int id) throws Exception {
		super(name, port, id);
		
//		this.id = id;
//
//		roboClient = new RobotClient (name, port, id);
//
//		// Automatically start own thread in constructor
//		thread.start();
//
//		System.out.println("Running "
//				+ this.toString()
//				+ " of robot "
//				+ this.id
//				+ " in thread "
//				+ this.thread.getName());

		try {
			this.laser    = new Ranger (roboClient, super.id, 1);
			this.sonar 	  = new Ranger (roboClient, super.id, 0);
			this.grip	  = new Gripper(roboClient, super.id);
			
			System.out.println ("Gripper state: " + grip.getState() );
			
		} catch (PlayerException e) {
			System.err.println (this.toString()
					+ " of robot "
					+ id
					+ ": > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			throw new IllegalStateException();
		}
//		roboClient.runThreaded();
	}

	@Override
	protected void shutdownDevices() {
		this.laser.thread.interrupt();
		while(this.laser.thread.isAlive());
		this.sonar.thread.interrupt();
		while(this.sonar.thread.isAlive());
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
