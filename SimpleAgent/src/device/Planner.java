package device;

import data.Position;
import javaclient3.LocalizeInterface;
import javaclient3.MapInterface;
import javaclient3.PlannerInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.localize.PlayerLocalizeSetPose;
import javaclient3.structures.planner.PlayerPlannerData;

public class Planner implements Runnable {
	// Planner is running its own Player server
	protected PlayerClient playerclient = null;
	protected PlannerInterface plan = null;
	protected LocalizeInterface loci  = null;
	protected MapInterface mapi = null;
	protected PlayerLocalizeSetPose plsp = null;
	protected final int SLEEPTIME = 100;

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );

	// initial values for the covariance matrix (c&p example from Player)
	protected double cov[] = {
			250,
			250,
			(Math.PI / 6.0) * (Math.PI / 6.0) * 180 / Math.PI * 3600 * 180
					/ Math.PI * 3600 };
	// set the initial guessed pose for localization (AMCL)
	protected PlayerPose goal = null;
	protected PlayerPlannerData ppd = null;
	private boolean isNewGoal = false;

	
	// Host id
	public Planner (String host, int port, int id) {
		try {
			// Connect to the Player server and request access to Position
			this.playerclient  = new PlayerClient (host, port);
			this.mapi = this.playerclient.requestInterfaceMap(0, PlayerConstants.PLAYER_OPEN_MODE);
			this.loci = this.playerclient.requestInterfaceLocalize(0, PlayerConstants.PLAYER_OPEN_MODE);
			this.plan = this.playerclient.requestInterfacePlanner(0, PlayerConstants.PLAYER_OPEN_MODE);

						System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName()
					+ " of robot "
					+ id);
			
			// set the initial guessed pose for localization (AMCL)
			this.plsp = new PlayerLocalizeSetPose ();
			this.goal = new PlayerPose(-7,-7,0);

			// enable motion
			plan.setRobotMotion(1);

			// set the mean values to 0,0,0
			plsp.setMean (new PlayerPose ());
			plsp.setCov (cov);
			loci.setPose (plsp);
			
			// first goal 0 0 0
			plan.setGoal(this.goal);

			// Automatically start own thread in constructor
			this.thread.start();
		} catch ( PlayerException e ) {
			System.err.println ("Planner: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
	}
	public void setGoal (Position goal) {
		this.goal.setPx(goal.getX());
		this.goal.setPy(goal.getY());
		this.goal.setPa(goal.getYaw());
		isNewGoal = true;
	}
	// Only to be called @~10Hz
	protected void update () {
		// Wait for readings
//		while ( ! plan.isDataReady ()){
			try { Thread.sleep (this.SLEEPTIME); }
			catch (InterruptedException e) { this.thread.interrupt(); }
//		}
		// request recent planner data
//		this.ppd = plan.getData ();
//		System.out.println (ppd.getWaypoints_count());
		// update goal
//		if(isNewGoal) {
//			isNewGoal = false;
//			plan.setGoal(this.goal);
//			
//		}
	}

	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.update();
		}
	}
	public void shutdown() {
		this.playerclient.close();
		while (this.playerclient.isAlive());
		this.thread.interrupt();
		while (this.thread.isAlive());
		System.out.println("Shutdown of " + this.toString());		
	}
}
