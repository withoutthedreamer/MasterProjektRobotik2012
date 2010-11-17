package robot;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;

import java.util.Vector;

public class Blobfinder {
	protected BlobfinderInterface  bfi  = null;
	protected int count;
	protected int[] color = null;
	protected Vector<BlobfinderBlob> blob = null;;

	public Blobfinder (PlayerClient host) {
		try {
			this.bfi = host.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);
			this.blob = new Vector<BlobfinderBlob>();
		} catch ( PlayerException e ) {
			System.err.println ("Blobfinder: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	// Only to be called @~10Hz
	public void updateBlobs () {
		// Wait for the laser readings
		while (!this.bfi.isDataReady());
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
}
