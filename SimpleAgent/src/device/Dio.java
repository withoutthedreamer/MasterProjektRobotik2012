/**
 * 
 */
package device;

import java.util.logging.Logger;

import javaclient3.DIOInterface;
import javaclient3.structures.dio.PlayerDioData;

/**
 * @author sebastian
 *
 */
public class Dio extends RobotDevice {

    /** Logging support */
    Logger logger = Logger.getLogger (Dio.class.getName ());

   /**
     * 
     */
    public Dio() { super(); }

    /**
     * @param roboClient
     * @param device
     */
    public Dio(DeviceNode roboClient, Device device) {
        super(roboClient, device);
    }
    @Override protected void update () {}
    
    public int getInput (int index)
    {
//        if ( ((DIOInterface) getDevice() ).isDataReady() == true )
        {
            PlayerDioData data = ((DIOInterface) getDevice() ).getData();
            
            if (data != null)
            {
                int count = data.getCount();
                int digIn = data.getDigin();
                logger.info("Got digin count: "+count+" with bits: "+Integer.toBinaryString(digIn));
                                
                if (0 <= index && index < count) {
                	int bit = digIn & (1<<index);
                	if (bit > 0)
                		return 1;
                	else
                		return 0;
                }
            }
        }
        
        return -1;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

}
