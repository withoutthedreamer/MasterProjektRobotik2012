package jadex.test;

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
	}

	@Override
	public void executeBody() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
//				ConsoleAgent ca = (ConsoleAgent)ia;
				getTestService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString());
//						ta.append(buf.toString());
					}
				});
				return null;
			}
		});
	}

	@Override
	public void agentKilled() {
		ts.send(getComponentIdentifier().toString(), new Position(-1,-1,0));
	}

	public TestService getTestService()
	{
		return ts;
	}
}
