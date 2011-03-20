package device;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javaclient3.GripperInterface;

/**
 * A Gripper device represents a mechanical actuator consisting of
 * typically two paddles that can open/close and lift/release to manipulate some objects.
 * @author sebastian
 */
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
    /**
     * Copied from JavaClient:
     * Data: state (PLAYER_GRIPPER_DATA_STATE)
     * The gripper interface returns 3 values that represent the current
     * state of the gripper; the format is given below.  Note that the exact
     * interpretation of this data may vary depending on the details of your
     * gripper and how it is connected to your robot (e.g., General I/O vs. User
     * I/O for the Pioneer gripper).
     * The following list defines how the data can be interpreted for some
     * Pioneer robots and Stage:
     * - state (unsigned byte)
     *   - bit 0: Paddles open
     *   - bit 1: Paddles closed
     *   - bit 2: Paddles moving
     *   - bit 3: Paddles error
     *   - bit 4: Lift is up
     *   - bit 5: Lift is down
     *   - bit 6: Lift is moving
     *   - bit 7: Lift error
     * - beams (unsigned byte)
     *   - bit 0: Gripper limit reached
     *   - bit 1: Lift limit reached
     *   - bit 2: Outer beam obstructed
     *   - bit 3: Inner beam obstructed
     *   - bit 4: Left paddle open
     *   - bit 5: Right paddle open
     */
    Dio dio;
	
	boolean timeout;

	/**
	 * Taken from the player IF documentation.
	 */
	public static enum stateType
	{
		OPEN,
		CLOSED,
		MOVING,
		UP,
		DOWN,
		ERROR
	}
	
	/**
	 * Creates a Gripper object.
	 * @param deviceNode The device node containing this device.
	 * @param device This device' properties.
	 */
	public Gripper(DeviceNode deviceNode, Device device)
	{
		super(deviceNode, device);
	}
	
	/**
	 * Stops the gripper current motion (if any).
	 */
	public void stop ()
	{
		((GripperInterface) getDevice()).stop();
	}
	/**
	 * Opens the gripper's paddles.
	 * If available sensors are used to determine when the paddles are open.
	 */
	public void open ()
	{
		updateDio();
		
		((GripperInterface) getDevice()).open();
		
		if (getDio() != null) {
			while (getDio().getInput(0) != 0) {
				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			}
		} else
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	/**
	 * Close the gripper's paddles.
	 */
	public void close ()
	{
		((GripperInterface) getDevice()).close();

		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	/**
	 * Lift the gripper's paddles (if supported).
	 */
	public void lift ()
	{
		updateActarray();
		updateDio();

		if (getDio() != null)
	    	/** Some thing between the paddles ? */
	    	if (getDio().getInput(3)==1 || getDio().getInput(2)==1 ) {
	    		logger.info("Something is between the paddles");
	    	} else
	            logger.info("Nothing between the paddles");

	    if (getAa() != null) {
	    	/** Lift up */
	        getAa().moveHome(0);
	    }
	   	   
	    try { Thread.sleep(4000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	/**
     * Sets up the gripper to only close and lift paddles when an object is sensed between the paddles.
     */
    public void liftWithObject()
    {
        updateDio();
    
    	if (getDio() != null) {
    
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
    
    		// TODO debug
    		while (getDio().getInput(2)==0 && getDio().getInput(3)==0 && timeout == false)
    		{
    			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
    		}
    
    		/** Some object */
    		if (timeout == false)
    		{
    			close();
    			lift();
    		}
    		/** else: Nothing between paddles to lift*/
    	}
    	else
    	{
    	    /** No Dio */
    	    release();
    	    open();
    	    close();
    	    lift();
    	}
    	// TODO notify
    }
    public void releaseOpen()
    {
        release();
        open();
        // TODO notify
    }
    public void closeLift()
    {
        close();
        lift();
    }

    /**
	 * Release the gripper's paddles (if supported).
	 * Use sensors to determine if paddles are released (if available).
	 */
	public void release ()
	{
		updateActarray();
		updateDio();

		/** Lift down */
	    if (getAa() != null)
	    {
	        getAa().moveTo(0, 0);
	    }
	   
	    if (getDio() != null)
	    {
	    	while (getDio().getInput(1) != 0)
	    	{
	    		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
	    	}
	    } else
	    	try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
    }
	/**
	 * During runtime checks if an actarray device is available, typically used to lift/release the paddles.
	 */
	void updateActarray()
	{				
		if (getAa() == null)
		{
			setAa( (Actarray) getDeviceNode().getDevice(new Device(IDevice.DEVICE_ACTARRAY_CODE, null,-1,-1)) );

			if (getAa() == null)
			{
				logger.info("No Actarray device found");
			}
		}
	}
	/**
	 * During runtime checks if an dio device is available, typically used to sensor gripper states.
	 * Normally that is supported by some photo diodes.
	 */
	void updateDio()
	{
		if (getDio() == null)
		{
			setDio( (Dio) getDeviceNode().getDevice(new Device(IDevice.DEVICE_DIO_CODE,null,-1,-1)) );

			if (getDio() == null)
			{
				logger.info("No Dio device found");
			}
		}
	}
	/**
	 * @return The current state the gripper is in.
	 */
	public stateType getState()
	{
	    stateType state = stateType.ERROR;
	    int pState = -1;

        if ( ((GripperInterface) getDevice()).isDataReady() )
        {
	    	pState = ((GripperInterface) getDevice()).getData().getState();

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
	/**
     * @param aa the aa to set
     */
    protected void setAa(Actarray aa) {
        this.aa = aa;
    }

    /**
     * @param dio the dio to set
     */
    protected void setDio(Dio dio) {
        this.dio = dio;
    }
}
