package device;

public class Gripper extends PlayerDevice {

	protected final int SLEEPTIME = 1000;

	@SuppressWarnings("unused")
	private int goalState = 1;
	private int curState = 0;
	
	protected static enum stateType {
		OPEN,
		CLOSE,
		ERROR
	}

	public Gripper(RobotClient roboClient, Device device) {
		super(roboClient, device);
	}
	protected void update () {
		if ( ! ((javaclient3.GripperInterface) device).isDataReady() ) {			
		} else {
			curState = ((javaclient3.GripperInterface) device).getData().getState();
//			grip.setGripper(goalState);
		}
	}
	public void stop () {
		// stop
		this.goalState = 3;
	}
	public void open () {
		// open
		this.goalState = 1;
	}
	public void close () {
		// close
		this.goalState = 2;
	}
	public void lift () {
		this.goalState = 4;
	}
	public void release () {
		this.goalState = 5;
	}
	// Taken from player gripper IF doc
	public stateType getState() {
		stateType state = stateType.ERROR;
		
		switch (curState) {
			case 1:  state = stateType.OPEN;  break;
			case 2:  state = stateType.CLOSE; break;
			default: state = stateType.ERROR; break;
		}
		return state;
	}
}
