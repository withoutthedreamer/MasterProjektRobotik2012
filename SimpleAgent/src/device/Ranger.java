package device;

import javaclient3.RangerInterface;
import javaclient3.structures.ranger.PlayerRangerData;

/**
 * A Ranger device.
 * It has a number of ranges that return the distance to the next obstacle.
 * 
 * @author sebastian
 */
public class Ranger extends RobotDevice
{
	double[] ranges;
	int count;

	/**
	 * Creates a Ranger device.
	 * @param roboClient The device node containing the ranger.
	 * @param device The device properties.
	 */
	public Ranger (DeviceNode roboClient, Device device)
	{
		super(roboClient, device);;
	}

	/**
	 * Will check for new ranges
	 * If not yet ready will put current thread to sleep
	 */
	@Override protected void update()
	{
		if ( ((javaclient3.RangerInterface) getDevice()).isDataReady() )
		{
		    PlayerRangerData rData = ((RangerInterface) getDevice()).getData();
			
		    count = rData.getRanges_count();
			ranges = rData.getRanges();
		}
	}

	public double[] getRanges ()
	{
		return ranges;
	}

	public int getCount ()
	{
		return count;
	}
}
