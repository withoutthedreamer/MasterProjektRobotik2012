package device;

import core.Logger;
import data.Position;
import javaclient3.PlannerInterface;
//import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner extends Device {
	// Planner is running its own Player server
//	protected PlayerClient playerclient = null;
	protected PlannerInterface plan = null;
//	protected LocalizeInterface loci  = null;
//	protected MapInterface mapi = null;
//	protected PlayerLocalizeSetPose locPose = null;
	protected final int SLEEPTIME = 100;

	// Every class of this type has it's own thread
//	public Thread thread = new Thread ( this );

	// initial values for the covariance matrix (c&p example from playernav)
//	protected double cov[] = { 0.5*0.5, 0.5*0.5, (Math.PI/6.0)*(Math.PI/6.0), 0, 0, 0 };

	// set the initial guessed pose for localization (AMCL)
//	protected PlayerPose goal = null;
	protected Position goal = null;
//	protected PlayerPose pose = null;
	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;
	private boolean isNewPose = false;
	private Position curPosition;

	// Host id
//	public Planner (String host, int port, int id) {
	public Planner (RobotClient roboClient) {
		try {
			// Connect to the Player server and request access to Position
//			playerclient  = new PlayerClient (host, port);

//			mapi = playerclient.requestInterfaceMap(0, PlayerConstants.PLAYER_OPEN_MODE);
//			loci = playerclient.requestInterfaceLocalize(0, PlayerConstants.PLAYER_OPEN_MODE);
			plan = roboClient.getClient().requestInterfacePlanner(0, PlayerConstants.PLAYER_OPEN_MODE);

			// set the initial guessed pose for localization (AMCL)
//			locPose = new PlayerLocalizeSetPose ();
//			
//			pose = new PlayerPose();
//			goal = new PlayerPose();
			goal = new Position(6,6,6);
			curPosition = new Position(6,6,6);

			// set the first mean values
//			locPose.setMean (pose);
//			locPose.setCov (cov);
			
			// enable motion
			plan.setRobotMotion(1);

			// Add itself to the device list
//			deviceList.put("planner", this);

		} catch ( PlayerException e ) {
//			System.err.println ("    [ " + e.toString() + " ]");
			Logger.logDeviceActivity(true, "Connecting", this);
			throw new IllegalStateException();
		}
	}
	public Planner(RobotClient roboClient, Device device) {
		this(roboClient);
		host = device.getHost();
		name = device.getName();
		deviceNumber = device.getDeviceNumber();
		port = device.getPort();
	}
	public void setGoal (Position newGoal) {
//		goal.setPx(newGoal.getX());
//		goal.setPy(newGoal.getY());
//		goal.setPa(newGoal.getYaw());
		goal = newGoal;
		isNewGoal = true;
	}
	public Position getGoal() {
//		return new Position(
//				goal.getPx(),
//				goal.getPy(),
//				goal.getPa());
		return goal;
	}
	// Only to be called @~10Hz
	protected void update () {
//		if (loci.isDataReady()) {
//			if ( loci.getData() != null ) {
//				if ( loci.getData().getHypoths_count() > 0 ) {
//					System.out.println("Hypothesis #: " + this.loci.getData().getHypoths_count());
//					pose = loci.getData().getHypoths()[0].getMean();
////					printPos(pose);
//				}
//			}
//			if (loci.getParticleData() != null) {
//				System.out.println("Particle #: " + this.loci.getParticleData().getParticles_count());
//			}
//		}
		// TODO check if position is on map
		if (plan.isDataReady()) {
			// request recent planner data
			ppd = plan.getData ();
//			System.out.println (ppd.getWaypoints_count());

			// set position belief
			// has to be before over writing curPosition!
			if(isNewPose) {
				isNewPose = false;
//				loci.setPose (locPose);	
				ppd.setPos(new PlayerPose(
						curPosition.getX(),
						curPosition.getY(),
						curPosition.getYaw()));
			}
			// Update current position belief
			curPosition.setX(ppd.getPos().getPx());
			curPosition.setY(ppd.getPos().getPy());
			curPosition.setYaw(ppd.getPos().getPa());
		}
		// update goal
		if(isNewGoal) {
			isNewGoal = false;
			plan.setGoal(new PlayerPose(
					goal.getX(),
					goal.getY(),
					goal.getYaw()));
		}
		
//		try { Thread.sleep (this.SLEEPTIME); }
//		catch (InterruptedException e) { thread.interrupt(); }

	}

//	private static void printPos(PlayerPose pose2) {
//		System.out.printf("Current pos belief: (%5.2f,%5.2f,%5.2f)",
//			pose2.getPx(),
//			pose2.getPy(),
//			pose2.getPa() );
//	}
	
//	public void runThreaded() {
//		playerclient.runThreaded (-1, -1);
//		thread.start();
//		Logger.logActivity(false, "Running", this.toString(), this.id, thread.getName());
//	}

//	@Override
//	public void run() {
//		while ( ! thread.isInterrupted()) {
//			update();
//		}
//	}
//	public void shutdown() {
//		playerclient.close();
//		while (playerclient.isAlive());
//		thread.interrupt();
//		while (this.thread.isAlive());
//		Logger.logActivity(false, "Shutdown", this.toString(), id, thread.getName());
//	}
	public synchronized void setPose(Position position) {
//		this.pose.setPx(position.getX());
//		this.pose.setPy(position.getY());
//		this.pose.setPa(position.getY());
		curPosition = position;
		isNewPose = true;
	}
	public Position getPose() {
//		return new Position(pose.getPx(),pose.getPy(),pose.getPa());
		return curPosition;
	}
}
