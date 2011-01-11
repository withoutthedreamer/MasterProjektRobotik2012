package robot;

import data.Position;
import device.RangerLaser;
import device.RangerSonar;
import javaclient3.PlayerException;

final public class PioneerSL extends Pioneer {

	public PioneerSL(String name, int port, int id) throws Exception {
		super(name, port, id);
		try {
			this.laser    = new RangerLaser (this.playerclient, super.id);
			this.sonar 	  = new RangerSonar (this.playerclient, super.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerSL: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
//			System.exit (1);
			throw new IllegalStateException();
		}
		this.playerclient.runThreaded (-1, -1);
	}

	@Override
	public void shutdownDevices() {
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
