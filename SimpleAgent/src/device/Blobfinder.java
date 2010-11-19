package device;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;

import java.util.Vector;

import data.BlobfinderBlob;

public class Blobfinder implements Runnable {
	protected BlobfinderInterface  bfi  = null;
	protected int count;
	protected int[] color = null;
	protected Vector<BlobfinderBlob> blob = null;;
	protected final int SLEEPTIME = 100;
	
	public final static int RED   = 0xFF0000;
	public final static int GREEN = 0x00FF00;
	public final static int BLUE  = 0x0000FF;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );
	
	public Blobfinder (PlayerClient host, int id) {
		try {
			this.bfi = host.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);
			this.blob = new Vector<BlobfinderBlob>();
			
			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);

		} catch ( PlayerException e ) {
			System.err.println ("Blobfinder: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	// Only to be called @~10Hz
	public void updateBlobs () {
		// Wait for the laser readings
		while (!this.bfi.isDataReady()) {
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
		}
		this.count = this.bfi.getData().getBlobs_count();
		
		if (this.count > 0) {
			for (int i = 0; i<this.count; i++) {
				PlayerBlobfinderBlob unblob = this.bfi.getData().getBlobs()[i];
				// envelope for new blob
				BlobfinderBlob inblob = new BlobfinderBlob(
						unblob.getColor(),
						unblob.getArea(),
						unblob.getX(),
						unblob.getY(),
						unblob.getLeft(),
						unblob.getRight(),
						unblob.getTop(),
						unblob.getBottom(),
						unblob.getRange()	);
				
				if (i >= this.blob.size()) {
					this.blob.add(inblob);
				} else {
					this.blob.set(i, inblob);
				}
			}
		}
	}
	
	public Vector<BlobfinderBlob> getBlobs () {
		return this.blob;
	}
	public int getCount () {
		return this.count;
	}
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.updateBlobs ();
		}
		System.out.println("Shutdown of " + this.toString());
	}
}
