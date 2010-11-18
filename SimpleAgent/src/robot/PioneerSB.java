package robot;

import javaclient3.PlayerException;

final public class PioneerSB extends Pioneer2dx {

	public PioneerSB(String name, int port, int id) {
		super(name, port, id);
		try {
			super.sonar   = new Sonar (super.playerclient, super.id);
			super.blofi	  = new Blobfinder(super.playerclient, super.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerSB: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}
	public void shutdownDevices () {
		super.sonar.thread.interrupt();
		super.blofi.thread.interrupt();
	}
}
