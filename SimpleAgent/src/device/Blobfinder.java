package device;

import javaclient3.BlobfinderInterface;

import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import javaclient3.structures.blobfinder.PlayerBlobfinderData;
import data.BlobfinderBlob;
import device.external.IBlobfinderListener;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * A Blobfinder device.
 * 
 * @author sebastian
 */
public class Blobfinder extends RobotDevice
{
    /** Current blob count in view. */
	int count;

	/** Blob memory */
	CopyOnWriteArrayList<BlobfinderBlob> blobs;
	
	/** Callback listeners */
    CopyOnWriteArrayList<IBlobfinderListener> newBlobListeners;

    /**
     * Creates a Blobfinder device object.
     * @param roboClient The DeviceNode to which this device is connected.
     * @param device This device' information.
     */
	public Blobfinder(DeviceNode roboClient, Device device)
	{
		super(roboClient, device);
		setSleepTime(500);

		blobs = new CopyOnWriteArrayList<BlobfinderBlob>();
		newBlobListeners = new CopyOnWriteArrayList<IBlobfinderListener>();
	}
	
	/**
	 * Update the current blob count and blob information.
	 */
	@Override protected void update ()
	{
	    if (((BlobfinderInterface) getDevice()).isDataReady())
	    {
	        // TODO else case
	        
	        PlayerBlobfinderData data = ((BlobfinderInterface) getDevice()).getData();
	       
	        /** Get the current blob count in view */
	        count = data.getBlobs_count();

	        if (count > 0)
	        {
	            for (int i=0; i<count; i++)
	            {
	                PlayerBlobfinderBlob unblob = data.getBlobs()[i];
	                
	                /** Envelope for new blob */
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

	                if (i >= blobs.size())
	                {
	                    /** Add new discovered blobs to the list */
	                    blobs.add(inblob);
	                    
	                }
	                else
	                {
	                    /** Update blob information */
	                    blobs.set(i, inblob);
	                }
	                /** Notify listeners */
                    notifiyListeners(inblob);
	            }
	        }
	    }
	}
	/**
	 * @return All known blobs to this device since init.
	 */
	public CopyOnWriteArrayList<BlobfinderBlob> getBlobs ()
	{
		return blobs;
	}
	/**
	 * @return The current blob count in view.
	 */
	public int getCount ()
	{
		return count;
	}
	void notifiyListeners(BlobfinderBlob newBlob)
	{
	    if (newBlob != null)
	    {
	        Iterator<IBlobfinderListener> it = newBlobListeners.iterator();
	        while (it.hasNext()) { it.next().newBlobFound(newBlob); }
	    }
	}
	/**
	 * Add a new listener to this device.
	 * The callback is called whenever a new blob is found.
	 * @param cb The callback method.
	 * @return {@link CopyOnWriteArrayList#addIfAbsent(Object)}
	 */
	public boolean addBlobListener(IBlobfinderListener cb)
	{
	    return newBlobListeners.addIfAbsent(cb);
    }
	/**
	 * Remove a listener to this device.
	 * @param cb The callback method.
	 * @return {@link CopyOnWriteArrayList#remove(Object)}
	 */
    public boolean removeBlobListener(IBlobfinderListener cb)
    {
        return newBlobListeners.remove(cb);
    }
    /**
     * Clear internal structures.
     */
    @Override synchronized public void shutdown()
    {
        super.shutdown();
        newBlobListeners.clear();
        blobs.clear();
    }
}
