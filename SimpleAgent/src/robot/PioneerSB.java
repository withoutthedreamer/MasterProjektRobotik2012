package robot;

import javaclient3.PlayerException;

final public class PioneerSB extends Pioneer2dx {

	public PioneerSB(String name, int port, int id) {
		super(name, port, id);
		try {
			super.sonar   = new Sonar (this.playerclient);
			super.blofi	  = new Blobfinder(this.playerclient);
		} catch (PlayerException e) {
			System.err.println ("PioneerSB: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}
}
