package device;

import javaclient3.SonarInterface;
/// TODO test this
public class RangerSonar extends Ranger
{
	protected SonarInterface soni  = null;
	protected float[] sonRanges = null;

	public RangerSonar(RobotClient roboClient, Device device) {
		super(roboClient, device);
	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	@Override
	protected void update() {
		// Wait for the laser readings
		while ( ! ((javaclient3.SonarInterface) device).isDataReady() ) {
		}
		count = ((javaclient3.SonarInterface) device).getData().getRanges_count();
		if (count > 0) {
			sonRanges = ((javaclient3.SonarInterface) device).getData().getRanges();
		}
	}
	@Override
	public double[] getRanges () {
		double[] convRanges = null;
		if (count > 0) {
			convRanges = new double[count];
			// convert from float to double
			for (int i=0; i<count; i++) {
				convRanges[i] = (double) sonRanges[i];
			}
		}
		return convRanges;
	}
}
