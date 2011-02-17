package jadex.agent;

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
import data.Position;
import device.DeviceNode;
import device.ILocalizeListener;
import device.IPlannerListener;
import robot.NavRobot;
import robot.Robot;

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
	NavRobot robot = null;
		
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

		Integer port = (Integer)getArgument("port");
		/** Get the device node */
		deviceNode = new DeviceNode(new Object[] {"localhost",port, "localhost",port+1});
		deviceNode.runThreaded();

		robot = new NavRobot(deviceNode);
		robot.setRobotId((String)getArgument("name"));
		robot.setPosition(new Position((Double)getArgument("X"), (Double)getArgument("Y"), Math.toRadians((Double)getArgument("Yaw"))));
		
		sendHello();
	}
	
	void sendHello() {
		hs.send(""+getComponentIdentifier(), robot.getRobotId(), Robot.class.getName());
		logger.info(""+getComponentIdentifier()+" sending hello");
	}

	void sendPosition(Position newPose) {
		ps.send(getComponentIdentifier().toString(), robot.getRobotId(), newPose);

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
	
	@Override public void agentKilled() {
		
		robot.shutdown();
		deviceNode.shutdown();
		
		hs.send(getComponentIdentifier().toString(), robot.getRobotId(), "Bye");
		logger.info("Bye "+getComponentIdentifier());
	}
	
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	public GoalReachedService getGoalReachedService() { return gr; }

	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("port", "Player", "Integer", new Integer(6665)),
				new Argument("name", "Robot", "String", "r0"),
				new Argument("X", "Meter", "Double", new Double(-21.0)),
				new Argument("Y", "Meter", "Double", new Double(4.0)),
				new Argument("Yaw", "Degree", "Double", new Double(0))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a navigation agent.", null, args, null);
	}
	public Logger getLogger() {
		return logger;
	}
}
