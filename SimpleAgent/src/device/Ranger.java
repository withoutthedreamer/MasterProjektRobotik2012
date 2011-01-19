package device;


public class Ranger extends PlayerDevice
{

	protected double[] ranges	= null;
	protected int count;

	public Ranger (RobotClient roboClient, Device device) {
		super(roboClient, device);;
	}

	/**
	 * Will check for new ranges
	 * If not yet ready will put current thread to sleep
	 */
	@Override
	protected void update() {
		if ( ((javaclient3.RangerInterface) device).isDataReady() ) {
			count = ((javaclient3.RangerInterface) device).getData().getRanges_count();
			ranges = ((javaclient3.RangerInterface) device).getData().getRanges();
		}
	}

	public double[] getRanges () {
		return ranges;
	}

	public int getCount () {
		return count;
	}
}
