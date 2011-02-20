package device;

import javaclient3.BlobfinderInterface;

import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import data.BlobfinderBlob;

import java.util.concurrent.*;

public class Blobfinder extends RobotDevice
{
	protected int count = 0;
	protected int[] color = null;
//	protected Vector<BlobfinderBlob> blobs = null;
	protected CopyOnWriteArrayList<BlobfinderBlob> blobs = null;
	
	public Blobfinder(DeviceNode roboClient, Device device) {
		super(roboClient, device);
//		blobs = new Vector<BlobfinderBlob>();
		blobs = new CopyOnWriteArrayList<BlobfinderBlob>();
	}
	// Only to be called @~10Hz
	@Override protected void update ()
	{
	    if (((BlobfinderInterface) device).isDataReady()) {
	        // TODO else case
	        count = ((BlobfinderInterface) device).getData().getBlobs_count();

	        if (count > 0) {
	            for (int i = 0; i<count; i++) {
	                PlayerBlobfinderBlob unblob = ((BlobfinderInterface) device).getData().getBlobs()[i];
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

	                if (i >= blobs.size()) {
	                    blobs.add(inblob);
	                } else {
	                    blobs.set(i, inblob);
	                }
	            }
	        }
	    }
	}
	
	public CopyOnWriteArrayList<BlobfinderBlob> getBlobs () {
		return blobs;
	}
	public int getCount () {
		return count;
	}
}
