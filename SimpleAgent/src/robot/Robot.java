package robot;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import robot.IPioneer.StateType;
import data.Position;
import device.Blobfinder;
import device.Device;
import device.Gripper;
import device.IDevice;
import device.Localize;
import device.Planner;
import device.Position2d;
import device.Ranger;
import device.RangerLaser;
import device.RangerSonar;
import device.Simulation;

public class Robot extends Device implements IRobot {

	// Standard devices
	// TODO dynamic array
	Position2d posi = null;
	Ranger laser = null;
	Ranger sonar = null;
	Planner planner = null;
	Localize localizer = null;
	Gripper gripper = null;
	Blobfinder bloFi = null;
	Simulation simu = null;
	
	double speed = -1.0;
	double turnrate = -1.0;
	Position position = null;
	Position goal = null;

	StateType currentState;
	
	String robotId = null;

	public Robot(){
		position = new Position();
		goal = new Position();
	}
	
	/**
	 * This constructor has to be overwritten in any subclasses!
	 */
	public Robot (Device roboDevices) {
		this();
		
		// Make the devices available
		connectDevices(roboDevices.getDeviceList());
	}
	
	public String getRobotId() {
		return robotId;
	}

	public void setRobotId(String name) {
		this.robotId = name;
	}

	/**
	 * Initiate standard variables to this robot for the devices
	 * Note that if there are duplicate devices in the list
	 * always the last one of the same device code will be chosen!
	 * @param deviceList 
	 */
	void connectDevices (ConcurrentLinkedQueue<Device> deviceList) {
		
		if (deviceList != null) {
			Iterator<Device> devIt = deviceList.iterator();

			if (devIt != null) {
				while (devIt.hasNext()) {
					Device dev = devIt.next();

					switch (dev.getName())
					{
					case IDevice.DEVICE_POSITION2D_CODE :
						posi = (Position2d) dev; break;

					case IDevice.DEVICE_RANGER_CODE : 
						if (dev.getDeviceNumber() == 0) {
							sonar = (Ranger) dev; break;
						} else {
							laser = (Ranger) dev; break;
						}

					case IDevice.DEVICE_SONAR_CODE : 
						sonar = (RangerSonar) dev; break;

					case IDevice.DEVICE_LASER_CODE : 
						laser = (RangerLaser) dev; break;

					case IDevice.DEVICE_PLANNER_CODE :
						planner = (Planner) dev; break;
							
					case IDevice.DEVICE_LOCALIZE_CODE :
						localizer = (Localize) dev; break;
							
					case IDevice.DEVICE_BLOBFINDER_CODE :
						bloFi = (Blobfinder) dev; break;
	
					case IDevice.DEVICE_GRIPPER_CODE : 
						gripper = (Gripper) dev; break;
						
					case IDevice.DEVICE_SIMULATION_CODE : 
						simu = (Simulation) dev; break; 

					default: break;
					}
				}
			}
		}
	}
		
	/**
	 * Command the motors
	 */
	protected final void execute() {
		if (posi != null) {
			posi.setSpeed(speed);
			posi.setTurnrate(turnrate);
		}
	}
	/**
	 * Sets a goal if the robot possesses a navigation device. 
	 * @param newGoal New @ref Position goal.
	 */
	@Override
	public void setGoal(Position newGoal)
	{
		if (planner != null)
			planner.setGoal(newGoal);
		else
			goal.setPosition(newGoal);
	}
	/**
	 * @return Current goal @ref Position or null.
	 */
	@Override
	public Position getGoal()
	{
		if (planner != null)
			return planner.getGoal();
		else
			return new Position(goal);
	}
	/**
	 * Sets the position if the robot possesses an position device.
	 * @param newPosition New @ref Position.
	 */
	@Override
	public void setPosition(Position newPosition)
	{
		if (localizer != null)
			localizer.setPosition(newPosition);
		else
			if (posi != null)
				posi.setPosition(newPosition);
			else
				position.setPosition(newPosition);
		
		/** Is the robot simulated ? */
		if (robotId != null && simu != null)
			simu.setPositionOf(robotId, newPosition);
	}

	/**
	 * @return Current robot @ref Position or null.
	 */
	@Override
	public Position getPosition()
	{
		if (planner != null)
			return planner.getPosition();
		else
			if (localizer != null)
				return localizer.getPosition();
			else
				if (posi != null)
					return posi.getPosition();
				else
					return new Position(position);
	}
	@Override
	public void shutdown()
	{
		planner.stop();
		super.shutdown();
	}
	/**
	 * Returns the current Planner.
	 * @return The @see Planner or 'null' if no one is available.
	 */
	public Planner getPlanner() {
		return planner;
	}
}
