package robot;

import data.Position;
import device.Gripper;
import device.Ranger;

import javaclient3.PlayerException;

final public class PioneerRR extends Pioneer {

	public PioneerRR(String name, int port, int id) {
		super(name, port, id);
		try {
			this.laser    = new Ranger (this.playerclient, super.id, 1);
			this.sonar 	  = new Ranger (this.playerclient, super.id, 0);
			this.grip	  = new Gripper(this.playerclient, super.id);
//			grip.lift();
			System.err.println ("Gripper state: " + grip.getState() );
			
		} catch (PlayerException e) {
			System.err.println ("PioneerRR: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		this.playerclient.runThreaded (-1, -1);
	}

	@Override
	public void shutdownDevices() {
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
