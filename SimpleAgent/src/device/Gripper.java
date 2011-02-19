package device;

import java.util.logging.Logger;

import javaclient3.GripperInterface;

public class Gripper extends RobotDevice
{
    /** Logging support */
    Logger logger = Logger.getLogger (Gripper.class.getName ());

	/**
	 * Taken from the player IF documantation.
	 */
	public static enum stateType {
		OPEN,
		CLOSED,
		MOVING,
		ERROR
	}

	public Gripper(DeviceNode deviceNode, Device device) {
		super(deviceNode, device);
	}
	
	@Override protected void update () {}

	public void stop () {
		((GripperInterface) device).stop();
	}
	public void open () {
		((GripperInterface) device).open();

		try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void close () {
		((GripperInterface) device).close();

		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void lift ()
	{
	    Dio dio = getDio();
	    
	    if (dio != null)
	        if (dio.getInput(4)==1 && dio.getInput(5)==1 )
	            logger.config("Got Dio input on index 4 and 5");
	        else
	            logger.config("Dio input failed on index 4 or 5");
	    
	    /** Close paddles */
	    close();
	    
	    Actarray aa = getActarray();
	    if (aa != null)
	        aa.moveHome(0);
	}
	
	public void release ()
	{
	    Actarray aa = getActarray();
	    
	    if (aa != null)
	        aa.moveTo(0, 0);
	    
	    /** Open paddles */
	    open();
	}
	
	Actarray getActarray()
	{
	    Actarray aa = (Actarray) getDeviceNode().getDevice(new Device(IDevice.DEVICE_ACTARRAY_CODE, null,-1,-1));

	    if (aa == null) {
	        logger.info("No Actarray device found");
	    }
	
	    return aa;
	}
	Dio getDio()
	{
	    Dio dio = (Dio) getDeviceNode().getDevice(new Device(IDevice.DEVICE_DIO_CODE,null,-1,-1));
	    
        if (dio == null) {
            logger.info("No Dio device found");
        }
        
        return dio;
	}

	public stateType getState() {
	    stateType state = stateType.ERROR;
	    int pState = -1;

        if ( ((GripperInterface) device).isDataReady() )
        {   
            pState = ((GripperInterface) device).getData().getState();

            switch (pState) {
            case 1:  state = stateType.OPEN;  break;
            case 2:  state = stateType.CLOSED; break;
            case 3:  state = stateType.MOVING;  break;

            default: state = stateType.ERROR; break;
            }
        }
		return state;
	}
}
