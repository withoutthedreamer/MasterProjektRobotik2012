package robot;

import javaclient3.PlayerException;

final public class PioneerS extends Pioneer2dx {

	public PioneerS(String name, int port, int id) {
		super(name, port, id);
		try {
			super.sonar = new Sonar (this.playerclient);
		} catch (PlayerException e) {
			System.err.println ("PioneerS: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		super.playerclient.runThreaded (-1, -1);
	}
}
