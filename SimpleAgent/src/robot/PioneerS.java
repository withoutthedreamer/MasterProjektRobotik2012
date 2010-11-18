package robot;

import javaclient3.PlayerException;

final public class PioneerS extends Pioneer2dx {

	public PioneerS(String name, int port, int id) {
		super(name, port, id);
		try {
			this.sonar = new Sonar (this.playerclient, super.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerS: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}

	@Override
	public void shutdownDevices() {
		this.sonar.thread.interrupt();
		while(this.sonar.thread.isAlive());
	}
}
