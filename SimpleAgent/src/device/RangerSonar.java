package device;

import javaclient3.SonarInterface;
/// TODO test this
public class RangerSonar extends Ranger {

	protected SonarInterface soni  = null;
	protected float[] sonRanges = null;

	public RangerSonar(RobotClient roboClient, Device device) {
		super(roboClient, device);
	}
//		try {
//			soni = roboClient.getClient().requestInterfaceSonar(0, PlayerConstants.PLAYER_OPEN_MODE);
//
//			// Automatically start own thread in constructor
////			this.thread.start();
////			Logger.logActivity(false, "Running", this.toString(), id, thread.getName());
//
//		} catch ( PlayerException e ) {
////			System.err.println ("    [ " + e.toString() + " ]");
//			Logger.logDeviceActivity(true, "Connecting", this);
//			throw new IllegalStateException();
//		}
//	}
//	public RangerSonar(RobotClient roboClient, Device device) {
//		this(roboClient,device.getHost());
//		host = device.getHost();
//		name = device.getName();
//		deviceNumber = device.getDeviceNumber();
//		port = device.getPort();
//	}
	// Will check for new ranges
	// If not yet ready will put current thread to sleep
	protected void update() {
		// Wait for the laser readings
		while ( ! ((javaclient3.SonarInterface) device).isDataReady() ) {
//			try { Thread.sleep (SLEEPTIME); }
//			catch (InterruptedException e) { thread.interrupt(); }
		}
		count = ((javaclient3.SonarInterface) device).getData().getRanges_count();
		if (count > 0) {
			sonRanges = ((javaclient3.SonarInterface) device).getData().getRanges();
		}
	}

//	@Override
//	public void run() {
//		while ( ! this.thread.isInterrupted()) {
//			this.update();
//		}
//		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
//	}

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
