package robot;

public interface IPioneer
{
	static enum StateType
	{
		LWALL_FOLLOWING,
		RWALL_FOLLOWING,
		COLLISION_AVOIDANCE,
		WALL_SEARCHING,
		SET_SPEED,
		STOPPED
	}
    	
	static enum viewDirectType
	{
		LEFT,
		RIGHT,
		FRONT,
		BACK,
		LEFTFRONT,
		RIGHTFRONT,
		LEFTREAR,
		RIGHTREAR,
		ALL
	}
	
	final double MAXSPEED = 0.4; /** Normal_advance_speed in meters per sec. */
	final double MINSPEED = 0.05; /** Minimal speed set */
	final double MINTURNRATE = 0.03; /** Minimal turnrate set */
	final double MAXTURNRATE = 40; /** Max wall following turnrate in deg per sec.  */
	/** Low values: Smoother trajectory but more restricted  */
	final double STOP_ROT  = 30; /** Stop rotation speed.  */
	/**
	 *  Low values increase maneuverability in narrow
	 *  edges, high values let the robot sometimes be
	 *  stuck.
	 */
	final double TRACK_ROT =  40; /** Goal tracking rotation speed in degrees per sec.  */
	final double YAW_TOLERANCE = 20;/** Yaw tolerance for ball tracking in deg  */
	final double DIST_TOLERANCE = 0.5;/** Distance tolerance before stopping in meters  */
	final double WALLFOLLOWDIST = 0.5; /** Preferred wall following distance in meters.  */
	final double STOP_WALLFOLLOWDIST = 0.2; /** Stop distance in meters.  */
	final double WALLLOSTDIST  = 0.5; /** Wall attractor in meters before loosing walls.  */
	final double SHAPE_DIST = 0.3; /** Min Radius from sensor for robot shape.  */
	/**
	 * Sonar configuration
	 */
	final int SONARCOUNT = 16; /** Maximum sonar count. */
	final double SONARMAX = 5.0; /** Maximum sonar range. */
	/**
	 *  Ranger configuration
	 */
	final double LMAXANGLE = 240; /** Laser max angle in degree  */
	final int BEAMCOUNT = 2; /** Number of laser beams taken for one average distance measurement  */
	final double DEGPROBEAM   = 0.3515625; /** 360./1024. in degree per laser beam  */
	final double LPMAX     = 5.0;  /** max laser range in meters  */
	final double MINRANGE  = 0.02; /** Minimal ranger range in meters. */
	final double COS45     = 0.83867056795; /** Cos(33);  */
	final double INV_COS45 = 1.19236329284; /** 1/COS45  */
	final double DIAGOFFSET  = 0.1;  /** Laser to sonar diagonal offset in meters.  */
	final double HORZOFFSET  = 0.15; /** Laser to sonar horizontal offset in meters.  */
	final double MOUNTOFFSET = 0.1;  /** Sonar vertical offset at back for laptop mount.  */
	final int LMIN  = 175;/** LEFT min angle.       */  final int LMAX  = 239; /** LEFT max angle.  */
	final int LFMIN = 140;/** LEFTFRONT min angle.  */  final int LFMAX = 175; /** LEFTFRONT max angle.  */
	final int FMIN  = 100;/** FRONT min angle.      */  final int FMAX  = 140; /** FRONT max angle.  */
	final int RFMIN = 65; /** RIGHTFRONT min angle. */  final int RFMAX = 100; /** RIGHTFRONT max angle.  */
	final int RMIN  = 0;  /** RIGHT min angle.      */  final int RMAX  = 65;  /** RIGHT max angle.  */

	/**
	 *  Debugging
	 */
	static boolean isDebugLaser = false;
	static boolean isDebugState = false;
	static boolean isDebugSonar = false;
	static boolean isDebugDistance = false;
	static boolean isDebugPosition = false;
}
