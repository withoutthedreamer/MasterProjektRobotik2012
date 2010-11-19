package robot;

import device.Blobfinder;
import device.Sonar;
import javaclient3.PlayerException;

final public class PioneerSB extends Pioneer {

	public PioneerSB(String name, int port, int id) {
		super(name, port, id);
		try {
			this.sonar   = new Sonar (this.playerclient, this.id);
			this.blofi	  = new Blobfinder(this.playerclient, this.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerSB: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		this.playerclient.runThreaded (-1, -1);
	}
	public void shutdownDevices () {
		this.sonar.thread.interrupt();
		while(this.sonar.thread.isAlive());
		this.blofi.thread.interrupt();
		while(this.blofi.thread.isAlive());
	}
}
