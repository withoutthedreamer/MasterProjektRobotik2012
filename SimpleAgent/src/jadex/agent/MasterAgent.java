package jadex.agent;

import core.Logger;
import data.Position;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.*;
import jadex.service.*;

public class MasterAgent extends MicroAgent {
	
	/* Services */
	HelloService hs = null;
	SendPositionService ps = null;
	ReceiveNewGoalService gs = null;

	@Override
	public void agentCreated() {
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
	public void executeBody() {
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
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString());
						
						Logger.logActivity(false, "Receiving "+buf.toString(), getComponentIdentifier().toString(), -1, null);
					}
				});
				return null;
			}
		});
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getSendPositionService().addChangeListener(new IChangeListener()
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
		
		waitFor(5000, new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				gs.send(getComponentIdentifier().toString(), new Position(-6,-6,0));
				return null;
			}
		});
		
		waitForTick(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				return null;
			}
		});

	}

	@Override
	public void agentKilled() {
		hs.send(getComponentIdentifier().toString(), "Bye");
		Logger.logActivity(false, "Bye", getComponentIdentifier().toString(), -1, null);
	}

	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
}
