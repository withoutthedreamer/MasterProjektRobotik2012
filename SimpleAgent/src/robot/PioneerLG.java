package robot;

import javaclient3.PlayerException;

final public class PioneerLG extends Pioneer2dx {
	protected Gripper grip = null;

	public PioneerLG(String name, int port, int id) {
		super(name, port, id);
		try {
			super.laser = new LaserUrg (this.playerclient, super.id);
			this.grip   = new Gripper (this.playerclient, super.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerLG: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}

	public void shutdownDevices () {
		super.laser.thread.interrupt();
		this.grip.thread.interrupt();
	}
}
