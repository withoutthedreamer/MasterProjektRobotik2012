package test.jadex;

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

public class TestNavAgent extends MicroAgent
{

	/** Services */
	HelloService hs = null;
	SendPositionService ps = null;
	ReceiveNewGoalService gs = null;
		
	@Override
	public void agentCreated()
	{
		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());

		assert(hs != null && gs != null && ps != null);
		
		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);

		Integer port = (Integer)getArgument("port");
		
		System.err.println("Got port: "+port);
		
		hs.send(getComponentIdentifier().toString(), "Hello");
		Logger.logActivity(false, "sent Hello", getComponentIdentifier().toString(), -1, null);
	}

	@Override
	public void executeBody()
	{
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
				ps.send(getComponentIdentifier().toString(), "dummy", new Position(-6,-6,-6));
				Logger.logActivity(false, "Send position", getComponentIdentifier().toString(), -1, null);

				waitFor(1000, this);
				return null;
			}
		};
		waitForTick(step);
	}
	@Override
	public void agentKilled() {
		
		hs.send(getComponentIdentifier().toString(), "Bye");
		Logger.logActivity(false, "sent Bye", getComponentIdentifier().toString(), -1, null);

	}
	
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }

	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player path", "dummy", "String", ""),
				new Argument("port", "dummy", "Integer", new Integer(6665)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a navigation agent.", null, args, null);
	}
}
