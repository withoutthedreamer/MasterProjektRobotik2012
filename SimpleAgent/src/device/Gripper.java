package device;

public class Gripper extends RobotDevice {

	/**
	 * Taken from the player IF documantation.
	 */
	public static enum stateType {
		OPEN,
		CLOSED,
		MOVING,
		ERROR
	}
	stateType state;

	public Gripper(DeviceNode roboClient, Device device) {
		super(roboClient, device);
		
		state = stateType.ERROR;
	}
	
	protected void update () {
		int pState = -1;

		if ( ((javaclient3.GripperInterface) device).isDataReady() )
		{	
			pState = ((javaclient3.GripperInterface) device).getData().getState();

			switch (pState) {
			case 1:  state = stateType.OPEN;  break;
			case 2:  state = stateType.CLOSED; break;
			case 3:  state = stateType.MOVING;  break;
			case 4:  state = stateType.ERROR; break;

			default: state = stateType.ERROR; break;
			}
		}
	}

	public void stop () {
		((javaclient3.GripperInterface) device).stop();
	}
	public void open () {
		((javaclient3.GripperInterface) device).open();
//		while (getState() != stateType.OPEN)
			try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void close () {
		((javaclient3.GripperInterface) device).close();
//		while (getState() != stateType.CLOSED)
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public void lift () {
		((javaclient3.GripperInterface) device).store();
	}
	public void release () {
		((javaclient3.GripperInterface) device).retrieve();
	}

	public stateType getState() {
		return state;
	}
}
