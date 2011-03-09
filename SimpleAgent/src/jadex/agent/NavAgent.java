package jadex.agent;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import jadex.bridge.*;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.GoalReachedService;
import jadex.service.HelloService;
import jadex.service.ReceiveNewGoalService;
import jadex.service.SendPositionService;
import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.ILocalizeListener;
import device.IPlannerListener;
import robot.NavRobot;
import robot.Pioneer;

public class NavAgent extends MicroAgent
{
	/** Logging support */
    static Logger logger = Logger.getLogger (NavAgent.class.getName ());

	/** Services */
	HelloService hs;
	SendPositionService ps;
	ReceiveNewGoalService gs;
	GoalReachedService gr;
	
	DeviceNode deviceNode = null;
	Pioneer robot = null;
		
	@Override public void agentCreated()
	{
		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());
		gr = new GoalReachedService(getExternalAccess());

		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		addDirectService(gr);

		String host = (String)getArgument("host");
		Integer port = (Integer)getArgument("port");
        Integer robotIdx = (Integer)getArgument("index");
        Boolean hasLaser = (Boolean)getArgument("laser");

        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,0) ); // TODO why playerclient blocks if not present?
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1) );
        devList.add( new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,0) );
        devList.add( new Device(IDevice.DEVICE_LOCALIZE_CODE,host,port+1,0) );

        if (hasLaser == true)
            devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,robotIdx+1));

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        
        /** Get the device node */
        setDeviceNode( new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()])));
		deviceNode.runThreaded();

		robot = new NavRobot(deviceNode.getDeviceListArray());
        getRobot().setRobotId("r"+robotIdx);
		
		/**
		 *  Check if a particular position is set
		 */
		Position setPose = new Position(
                (Double)getArgument("X"),
                (Double)getArgument("Y"),
                (Double)getArgument("Angle"));
		
		if ( setPose.equals(new Position(0,0,0)) == false )
		    robot.setPosition(setPose);		    

		sendHello();
	}
	
	void sendHello()
	{
		hs.send(""+getComponentIdentifier(), robot.getRobotId(), robot.getClass().getName());
		logger.fine(""+getComponentIdentifier()+" sending hello");
	}

	void sendPosition(Position newPose)
	{
		ps.send(""+getComponentIdentifier(), robot.getRobotId(), newPose);
		logger.finest(""+getComponentIdentifier()+" sending position "+newPose);
	}
	
	@Override public void executeBody()
	{
		/** Agent is worthless if underlying robot or devices fail */
		if (robot == null || deviceNode == null) {
			killAgent();
		}
		
		/**
		 *  Register planner callback
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if (robot.getPlanner() != null) /** Does it have a planner? */
				{
					robot.getPlanner().addIsDoneListener(new IPlannerListener()
					{
						@Override public void callWhenIsDone() {
							gr.send(""+getComponentIdentifier(), robot.getRobotId(),robot.getPlanner().getGoal());

							logger.info(""+getComponentIdentifier()+(String)getArgument("name")+" reached goal "+robot.getPlanner().getGoal());
						}
					});
				}
				return null;
			}
		});
		
		/**
		 *  Register localizer callback
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if (robot.getLocalizer() != null) /** Does it have a localizer? */
				{
					robot.getLocalizer().addListener(new ILocalizeListener()
					{
						@Override public void newPositionAvailable(Position newPose) {
							sendPosition(newPose);
						}
					});
				}
				return null;
			}
		});

		/**
		 *  Register new goal event callback
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getReceiveNewGoalService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString());
						
						logger.finer("Receiving "+buf.toString()+" "+getComponentIdentifier().toString());
						
						/** Check if it is this robot's goal */
						if ( ((String)content[1]).equals((String)getArgument("name")) ||
							 ((String)content[1]).equals("all") )
						{
							robot.setGoal((Position)content[2]);
							logger.finest((String)getArgument("name")+" received new goal "+((Position)content[2]).toString());
						}
					}
				});
				return null;
			}
		});
	
		/**
		 *  Register to HelloService
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getHelloService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(" ").append(content[2].toString());
												
						/** Check for reply request */
						if (((String)content[2]).equalsIgnoreCase("ping")) {

							logger.finer(""+getComponentIdentifier()+" receiving "+buf);

							sendHello();
						}
					}
				});
				return null;
			}
		});
	
		/**
		 *  Register to Position update service
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getSendPositionService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						
						/** Sending position on request */
						if (((String)content[1]).equals("request"))
							sendPosition(robot.getPosition());
					}
				});
				return null;
			}
		});
	}
	
	@Override public void agentKilled()
	{
	    robot.stop();
		robot.shutdown();
		deviceNode.shutdown();
		
		hs.send(""+getComponentIdentifier(), robot.getRobotId(), "Bye");
		logger.fine("Bye "+getComponentIdentifier());
	}
	
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	public GoalReachedService getGoalReachedService() { return gr; }

	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
                new Argument("host", "Player", "String", "localhost"),
				new Argument("port", "Player", "Integer", new Integer(6665)),
                new Argument("index", "Robot index", "Integer", new Integer(0)),
				new Argument("X", "Meter", "Double", new Double(0.0)),
				new Argument("Y", "Meter", "Double", new Double(0.0)),
				new Argument("Angle", "Degree", "Double", new Double(0.0)),
                new Argument("laser", "Laser ranger", "Boolean", new Boolean(true))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a navigation agent.", null, args, null);
	}
	public Logger getLogger() {
		return logger;
	}

    /**
     * @return the robot
     */
    protected Pioneer getRobot() {
        return robot;
    }
    protected void setRobot(Pioneer newRobot) {
    	robot = newRobot;
    }

	/**
	 * @return the deviceNode
	 */
	protected DeviceNode getDeviceNode() {
		return deviceNode;
	}

	/**
	 * @param deviceNode the deviceNode to set
	 */
	protected void setDeviceNode(DeviceNode deviceNode) {
		this.deviceNode = deviceNode;
	}
}
