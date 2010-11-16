package robot;

import javaclient3.BlobfinderInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;

public class Blobfinder {
	protected BlobfinderInterface  bfi  = null;
	protected int count;
	protected int[] color = null;
	protected BlobfinderBlob[] blob = null;

	public Blobfinder (PlayerClient host) {
		try {
			this.bfi = host.requestInterfaceBlobfinder(0, PlayerConstants.PLAYER_OPEN_MODE);
			// Static array size
			// TODO dynamic array size
			this.blob = new BlobfinderBlob[3];
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
		this.count = this.bfi.getData ().getBlobs_count ();
		
		if (this.count > 0) {
			for (int i = 0; i < this.count; i++) {
				PlayerBlobfinderBlob unblob = this.bfi.getData ().getBlobs ()[i];

				this.blob[i].setX(unblob.getX());
				this.blob[i].setY(unblob.getY());

				this.blob[i].setLeft(unblob.getLeft());
				this.blob[i].setRight(unblob.getRight());

				this.blob[i].setTop(unblob.getTop());
				this.blob[i].setBottom(unblob.getBottom());

				this.blob[i].setArea(unblob.getArea());
				this.blob[i].setColor(unblob.getColor());
			}
		}
	}
	
	public BlobfinderBlob[] getBlobs () {
		return this.blob;
	}
	public int getCount () {
		return this.count;
	}
}
