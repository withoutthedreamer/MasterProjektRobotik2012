/**
 * 
 */
package device;

import data.BlobfinderBlob;

/**
 * @author sebastian
 *
 */
public interface IBlobfinderListener
{
    public void newBlobFound( BlobfinderBlob newBlob );
}
