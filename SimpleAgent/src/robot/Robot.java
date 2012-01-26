package robot;

import java.util.logging.Logger;

import robot.external.IRobot;

import data.Position;
import device.Blobfinder;
import device.Device;
import device.Localize;
import device.Planner;
import device.Position2d;
import device.Ranger;
import device.RangerSonar;
import device.RangerLaser;
import device.Simulation;
import device.external.IDevice;
import device.external.IGripper;

/**
 * The Robot class represents a robot that can move in two dimensions,
 * that is in x and y direction. It will dynamically use if possible any devices
 * provided by the device list given. A robot can have a current location and
 * a goal position (in global coordinate system).
 * 
 * @author sebastian
 */
public class Robot extends Device implements IRobot
{
    /** Logging support */
    Logger logger = Logger.getLogger (Robot.class.getName ());

    /**
	 *  List of devices this robot can use if any of them are present.
	 *  They will be detected in a dynamical way during runtime.
	 */
	// TODO dynamic array
	Position2d posi;
	Ranger ranger1;
	RangerLaser rLaser;
	Ranger ranger0;
	RangerSonar rSonar;
	Planner planner;
	Localize localizer;
	IGripper gripper;
	Blobfinder bloFi;
	Simulation simu;
	
	double speed = 0.0;
	double turnrate = 0.0;
	
	Position position;
	Position goal;
	
	String robotId = null;

	/**
	 * Creates a Robot with initialized position and goal.
	 */
	public Robot()
	{
		position = new Position();
		goal = new Position();
	}
	
	/**
	 * @deprecated Use {@link Robot#Robot(Device[])} instead.
	 * This constructor has to be overwritten in any subclasses!
	 * It will parse all devices from the given device and connects
	 * it to the internal device list.
	 */
	public Robot (Device deviceNode)
	{
		this();
		
		/** Make the devices available */
		connectDevices( deviceNode.getDeviceListArray() );
	}
	/**
	 * Creates a robot and provides the given devices to it.
	 * @param robotDevList The provided devices.
	 */
	public Robot (Device[] robotDevList)
	{
	    this();
	    
	    connectDevices(robotDevList);
	    
	    if (getPosi() == null)
	    {
	        String log = "No position device found "+this;
	        logger.severe(log);
	        throw new IllegalStateException(log);
	    }
	    if (getSonar() == null)
	    {
	        logger.info("No sonar ranger device found "+this);
	    }
	    if (getLaser() == null)
	    {
	        logger.info("No laser ranger device found "+this);
	    }
	    if (getSimu() == null)
	    {
	        logger.info("No simulation device found "+this);
	    }
	}
	/**
	 * @return The robot's Id string.
	 */
	public String getRobotId()
	{
		return robotId;
	}
	/**
	 * Sets this robot's string identifier.
	 * @param name The robot identifier.
	 */
	public void setRobotId(String name)
	{
		robotId = name;
	}

	/**
	 * Initiate standard variables to this robot for the devices
	 * Note that if there are duplicate devices in the list
	 * always the last one of the same device code will be chosen!
	 * @param deviceList The device list to search for applicable devices.
	 */
	void connectDevices (Device[] deviceList)
	{	
	    if (deviceList != null && deviceList.length > 0)
	    {
	        for (int i=0; i<deviceList.length; i++)
	        {
	            Device dev = deviceList[i];

	            switch (dev.getId())
	            {
    	            case IDevice.DEVICE_POSITION2D_CODE :
    	                posi = (Position2d) dev; break;
    
    	            case IDevice.DEVICE_RANGER_CODE : 
    	            {
    	                /** getCount() might be 0 at this time, so try sonar first */
    	                if (ranger0 == null && rSonar == null)
    	                {
    	                    ranger0 = (Ranger) dev; break;
    	                }
    	                /** Do we have a sonar? */
    	                if (ranger0 != null || rSonar != null)
    	                {
    	                    ranger1 = (Ranger) dev; break;
    	                }
    	            }
    	            case IDevice.DEVICE_SONAR_CODE :
    	            {
    	            	/** Do we have a ranger already? */
    	            	if (ranger0 == null)
    	            		rSonar = (RangerSonar) dev; break;
    	            }
    	            /** Laser is a legacy device and deprecated */
    	            case IDevice.DEVICE_LASER_CODE :
    	            {
    	            	/** Do we have a ranger already? */
    	            	if (ranger1 == null)
    	            		rLaser = (RangerLaser) dev; break;
    	            }
    
    	            case IDevice.DEVICE_PLANNER_CODE :
    	                planner = (Planner) dev; break;
    
    	            case IDevice.DEVICE_LOCALIZE_CODE :
    	                localizer = (Localize) dev; break;
    
    	            case IDevice.DEVICE_BLOBFINDER_CODE :
    	                bloFi = (Blobfinder) dev; break;
    
    	            case IDevice.DEVICE_GRIPPER_CODE : 
    	                gripper = (IGripper) dev; break;
    
    	            case IDevice.DEVICE_SIMULATION_CODE : 
    	                simu = (Simulation) dev; break; 
    
    	            default: break;
	            }
	        }
	    }
	}
		
	/**
	 * Sets a goal if the robot possesses a navigation device. 
	 * @param newGoal New {@link Position} goal.
	 */
	@Override public void setGoal(Position newGoal)
	{
		if (planner != null)
		{
			planner.setGoal(newGoal);
		}
		else
		{
			goal.setPosition(newGoal);
		}
	}
	/**
	 * @return Current goal {@link Position} or null.
	 */
	@Override public Position getGoal()
	{
		if (planner != null)
		{
			return planner.getGoal();
		}
		else
		{
			return new Position(goal);
		}
	}
	/**
	 * Sets the position if the robot possesses an position device.
	 * @param newPosition New {@link Position}.
	 */
	@Override public void setPosition(Position newPosition)
	{
		if (localizer != null)
		{
			localizer.setPosition(newPosition);
		}
		else
		{
			if (posi != null)
			{
				posi.setPosition(newPosition);
			}
		}

		position.setPosition(newPosition);
		
		/** Is the robot simulated ? */
		if (robotId != null && simu != null)
		{
			/** Set the position of the robot simulation */
			simu.setPositionOf(robotId, newPosition);
		}
	}

	/**
	 * @return Current robot {@link Position} or null.
	 */
	@Override public Position getPosition()
	{		
		if (localizer != null)
		{
			position.setPosition( localizer.getPosition() );
		}
		else
		{
			if (posi != null)
			{
				position.setPosition( posi.getPosition() );
			}
		}

		if (position.equals(new Position(0,0,0)) && planner != null)
		{
			position.setPosition( planner.getPosition() );
		}
		
//		return new Position(position);
		return position;
	}
	@Override public void shutdown()
	{
		if (planner != null)
		{
			planner.stop();
		}
		
		super.shutdown();
	}
	/**
	 * Returns the current Planner.
	 * @return The {@link Planner} or 'null' if no one is available.
	 */
	public Planner getPlanner()
	{
		return planner;
	}
	/**
	 * @return the current {@link Localize} {@link Device} or 'null' if no such device.
	 */
	public Localize getLocalizer()
	{
		return localizer;
	}
	/**
	 * @return The {@link Gripper} {@link Device} or 'null' if no such device.
	 */
	public IGripper getGripper()
	{
	    return gripper;
	}

    /**
     * @return The current position.
     */
    public Position2d getPosi()
    {
        return posi;
    }

    /**
     * @return The laser.
     */
    public Ranger getLaser()
    {
        if (rLaser != null)
        	return rLaser;
        else
        	return ranger1;
    }

    /**
     * @return The sonar.
     */
    public Ranger getSonar()
    {
    	if (rSonar != null)
    		return rSonar;
    	else
    		return ranger0;
    }

    /**
     * @return The blobfinder.
     */
    public Blobfinder getBloFi()
    {
        return bloFi;
    }

    /**
     * @return The simulation.
     */
    public Simulation getSimu()
    {
        return simu;
    }

    /**
     * @return The speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * @return The turnrate.
     */
    public double getTurnrate()
    {
        return turnrate;
    }

    /**
     * @param speed The speed to set
     */
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    /**
     * @param turnrate The turnrate to set
     */
    public void setTurnrate(double turnrate)
    {
        this.turnrate = turnrate;
    }

    @Override public String toString()
    {
        return ""+getClass().getName()+" "+getRobotId();
    }
}
