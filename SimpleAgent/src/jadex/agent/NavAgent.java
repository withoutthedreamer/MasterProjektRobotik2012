package jadex.agent;

import java.util.logging.Logger;

import core.ProjectLogger;
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
import device.IPlannerListener;
import robot.NavRobot;

public class NavAgent extends MicroAgent
{
	// Logging support
    private static Logger logger = Logger.getLogger (ProjectLogger.class.getName ());

	/** Services */
	HelloService hs;
	SendPositionService ps;
	ReceiveNewGoalService gs;
	GoalReachedService gr;
	
	DeviceNode deviceNode = null;
	NavRobot robot = null;
	Position curPos = null;
	Position lastPos = null;
	
	
	@Override
	public void agentCreated()
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
		// Get the device node
		deviceNode = new DeviceNode(new Object[] {"localhost",port, "localhost",port+1});
		deviceNode.runThreaded();

		robot = new NavRobot(deviceNode);
		robot.setRobotId((String)getArgument("robot name"));
		robot.setPosition(new Position((Double)getArgument("X"), (Double)getArgument("Y"), (Double)getArgument("Yaw")));
				
		hs.send(getComponentIdentifier().toString(), robot.getRobotId(), ": Hello");
		logger.info("Sent Hello "+getComponentIdentifier().toString());
	}

	@Override
	public void executeBody()
	{
		/** Agent is worthless if underlying robot or devices fail */
		if (robot == null || deviceNode == null) {
			killAgent();
		}
		
		/** Register planner callback */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				robot.getPlanner().addIsDoneListener(new IPlannerListener()
				{
					@Override public void callWhenIsDone() {
						gr.send(getComponentIdentifier().toString(), robot.getRobotId(),robot.getPlanner().getGoal());
						logger.finest((String)getArgument("robot name")+" reached goal "+robot.getPlanner().getGoal().toString());
					}
				});
				return null;
			}
		});
		
		/** Register new goal event callback */
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
						
						// Check if it is my goal
						if ( ((String)content[1]).equals((String)getArgument("robot name")) ||
							 ((String)content[1]).equals("all") )
						{
							robot.setGoal((Position)content[2]);
							logger.info((String)getArgument("robot name")+" received new goal "+((Position)content[2]).toString());
						}
					}
				});
				return null;
			}
		});
		
		/** Set up periodical position broadcast */
		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				curPos = robot.getPosition();
				if(curPos.equals(lastPos) == false) {
					ps.send(getComponentIdentifier().toString(), robot.getRobotId(), curPos);

					logger.finer("Sending position "+getComponentIdentifier().toString());
					lastPos = curPos;
				}

				waitFor(1000, this);
				return null;
			}
		};
		waitForTick(step);
	}
	@Override
	public void agentKilled() {
		
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
				new Argument("port", "dummy", "Integer", new Integer(6665)),
				new Argument("robot name", "dummy", "String", "r0"),
				new Argument("X", "dummy", "Double", new Double(-6)),
				new Argument("Y", "dummy", "Double", new Double(-5)),
				new Argument("Yaw", "dummy", "Double", new Double(Math.toRadians(90)))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a navigation agent.", null, args, null);
	}
}
