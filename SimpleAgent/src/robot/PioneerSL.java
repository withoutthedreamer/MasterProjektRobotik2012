package robot;

import javaclient3.PlayerException;

final public class PioneerSL extends Pioneer2dx {

	public PioneerSL(String name, int port, int id) {
		super(name, port, id);
		try {
			this.laser    = new LaserUrg (this.playerclient);
			this.sonar 	  = new Sonar (this.playerclient);
		} catch (PlayerException e) {
			System.err.println ("PioneerSL: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		this.playerclient.runThreaded (-1, -1);
	}
}
