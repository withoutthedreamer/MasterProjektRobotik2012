package device;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javaclient3.GripperInterface;

public class Gripper extends RobotDevice
{
    /** Logging support */
    Logger logger = Logger.getLogger (Gripper.class.getName ());
    
    /** Controls a lift on the gripper (if gripper equipped) */
    Actarray aa;
    /** 
     * Returns data from digital I/O ports (if equipped)
     * Current config (2011-02-22):
     * 11 11 11 11
     * 76 54 32 10
     * 
     * 7+6 Toggle bits (sensor feedback)(2).
     * 5+4 Paddle pressed bits, i.e. when object is gripped (2).
     * 3+2 Paddle photo diodes (2).
     * 1+0 Paddle positions: 1==0: down, 0==0: open. 
     */
    Dio dio;
	
	boolean timeout;

	/**
	 * Taken from the player IF documantation.
	 */
	public static enum stateType {
		OPEN,
		CLOSED,
		MOVING,
		UP,
		DOWN,
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
		updateDio();
		
		((GripperInterface) device).open();
		
		if (dio != null) {
			while (dio.getInput(0) != 0) {
				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			}
		} else
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void close () {
		((GripperInterface) device).close();

		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void lift ()
	{
		updateActarray();
		updateDio();

		if (dio != null)
	    	/** Some thing between the paddles ? */
	    	if (dio.getInput(3)==1 || dio.getInput(2)==1 ) {
	    		logger.info("Something is between the paddles");
	    	} else
	            logger.info("Nothing between the paddles");

	    if (aa != null) {
	    	/** Lift up */
	        aa.moveHome(0);
	    }
	   	   
//		if (dio != null) {
//			while (dio.getInput(1) != 0) {
//				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
//			}
//		} else
			try { Thread.sleep(4000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public void release ()
	{
		updateActarray();
		updateDio();

		/** Lift down */
	    if (aa != null) {
	        aa.moveTo(0, 0);
	    }
	   
	    if (dio != null) {
	    	while (dio.getInput(1) != 0) {
	    		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
	    	}
	    } else
	    	try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
    }
	
	void updateActarray()
	{				
		if (aa == null) {
			aa = (Actarray) getDeviceNode().getDevice(new Device(IDevice.DEVICE_ACTARRAY_CODE, null,-1,-1));

			if (aa == null) {
				logger.info("No Actarray device found");
				aa = new Actarray();
			}
		}
	}
	void updateDio()
	{
		if (dio == null) {
			dio = (Dio) getDeviceNode().getDevice(new Device(IDevice.DEVICE_DIO_CODE,null,-1,-1));

			if (dio == null) {
				logger.info("No Dio device found");
				dio = new Dio();
			}
		}
	}

	public stateType getState() {
	    stateType state = stateType.ERROR;
	    int pState = -1;

        if ( ((GripperInterface) device).isDataReady() ) {
//	    if (dio != null)
//        {   
	    	pState = ((GripperInterface) device).getData().getState();
//	    	if (dio.getInput(1) == 1)
//	    		state = stateType.MOVING;
//	    	else {
//	    		if (dio.getInput(0) == 1)
//	    			state = stateType.OPEN;
//	    		else
//	    			state = stateType.CLOSED;
//	    	}

            switch (pState) {
            case 1:  state = stateType.OPEN;  break;
            case 2:  state = stateType.CLOSED; break;
            case 3:  state = stateType.MOVING;  break;

            default: state = stateType.ERROR; break;
            }
        }
		return state;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return the aa
	 */
	public Actarray getAa() {
		return aa;
	}

	/**
	 * @return the dio
	 */
	public Dio getDio() {
		return dio;
	}
	public void liftWithObject() {
		if (dio != null) {

			open();
			
			timeout = false;
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					timeout = true;
				}

			}, 20000);

			while (dio.getInput(2)==0 && dio.getInput(3)==0 && timeout == false) {
				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			}

			/** Some object */
			if (timeout == false) {

				close();
				lift();
			}
			/** else: Nothing between paddles to lift*/
		}
	}
}
