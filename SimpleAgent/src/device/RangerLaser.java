package device;

import javaclient3.LaserInterface;

public class RangerLaser extends Ranger
{
	protected LaserInterface las = null;
	protected float[] lasRanges = null;
		
	public RangerLaser (DeviceNode roboClient, Device device) {
		super(roboClient, device);
	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	@Override
	protected void update() {
		// Wait for the laser readings
		if ( ((javaclient3.LaserInterface) device).isDataReady() )
		{
			count = ((javaclient3.LaserInterface) device).getData().getRanges_count();
			if (count > 0) {
				lasRanges = ((javaclient3.LaserInterface) device).getData().getRanges();
			}
		}
	}
	@Override
	public double[] getRanges () {
		double[] convRanges = null;
		if (count > 0) {
			convRanges = new double[count];
			// convert from float to double
			for (int i=0; i<count; i++) {
				convRanges[i] = (double) lasRanges[i];
			}
		}
		return convRanges;
	}
}
