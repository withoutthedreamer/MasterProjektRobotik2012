package jadex.agent;

import core.Logger;
import jadex.bridge.*;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.HelloService;
import jadex.service.ReceiveNewGoalService;
import jadex.service.SendPositionService;
import data.Position;
import device.DeviceNode;
import robot.GripperRobot;

public class GripperAgent extends MicroAgent
{
//	protected final static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};

	/* Services */
	HelloService hs = null;
	SendPositionService ps = null;
	ReceiveNewGoalService gs = null;
	
	DeviceNode devices = null;
	GripperRobot gripper = null;
	Position curPos = null;
	
	
	
	//TODO start planner
	@Override
	public void agentCreated()
	{
		super.agentCreated();
		hs = new HelloService(getExternalAccess());
		addDirectService(hs);
		
		hs.send(getComponentIdentifier().toString(), getComponentIdentifier().toString() + " started");
//		try {
		int port = 6665;
			// Get the device node
			devices = new DeviceNode(new Object[] {"localhost",port, "localhost",port+1});
			devices.runThreaded();
			
			gripper = new GripperRobot(devices);
			gripper.runThreaded();
			gripper.setPosition(new Position(-6, -5, Math.toRadians(90)));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
			hs = new HelloService(getExternalAccess());
			ps = new SendPositionService(getExternalAccess());
			gs = new ReceiveNewGoalService(getExternalAccess());
			
			addDirectService(hs);
			addDirectService(ps);
			addDirectService(gs);
			
			hs.send(getComponentIdentifier().toString(), "Hello");
			Logger.logActivity(false, "Hello", getComponentIdentifier().toString(), -1, null);
	}
	
	@Override
	public void executeBody()
	{
		super.executeBody();
		if (gripper == null) {
			killAgent();
		}

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
						
						Logger.logActivity(false, "Receiving "+buf.toString(), getComponentIdentifier().toString(), -1, null);
					}
				});
				return null;
			}
		});
		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				curPos = gripper.getPosition();
				ps.send(getComponentIdentifier().toString(), curPos);
				Logger.logActivity(false, "Send position", getComponentIdentifier().toString(), -1, null);

				waitFor(1000, this);
				return null;
			}
		};
		waitForTick(step);
	}
	@Override
	public void agentKilled() {
		super.agentKilled();
		
		gripper.shutdown();
		devices.shutdown();
		
		hs.send(getComponentIdentifier().toString(), "Bye");

	}
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }

	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player path", "dummy", "String", ""),
				new Argument("player port", "dummy", "Integer", new Integer(6665)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", null, args, null);
	}
}
