package robot;

import sensor.Gripper;
import sensor.LaserUrg;
import javaclient3.PlayerException;

final public class PioneerLG extends Pioneer {
	protected Gripper grip = null;

	public PioneerLG(String name, int port, int id) {
		super(name, port, id);
		try {
			this.laser = new LaserUrg (this.playerclient, super.id);
			this.grip   = new Gripper (this.playerclient, super.id);
			this.grip.close();

		} catch (PlayerException e) {
			System.err.println ("PioneerLG: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}

	public void shutdownDevices () {
		this.laser.thread.interrupt();
		while (this.laser.thread.isAlive());
		this.grip.thread.interrupt();
		while (this.laser.thread.isAlive());
	}
}
