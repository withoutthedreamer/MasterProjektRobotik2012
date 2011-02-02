package test.jadex;

import core.Logger;
import data.Position;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.*;

public class ServiceAgent extends MicroAgent {
	
	protected TestService ts = null;

	@Override
	public void agentCreated() {
		ts = new TestService(getExternalAccess());
		addDirectService(ts);
		
		ts.send(getComponentIdentifier().toString(), getComponentIdentifier().toString() + " started");
		Logger.logActivity(false, "Sending", getComponentIdentifier().toString(), -1, null);
		
	}

	@Override
	public void executeBody() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getTestService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString());
						
						Logger.logActivity(false, "Receiving", getComponentIdentifier().toString(), -1, null);
					}
				});
				return null;
			}
		});
	}

	@Override
	public void agentKilled() {
		ts.send(getComponentIdentifier().toString(), new Position(-1,-1,0));
	
		Logger.logActivity(false, "Sending", getComponentIdentifier().toString(), -1, null);
	}

	public TestService getTestService()
	{
		return ts;
	}
}
