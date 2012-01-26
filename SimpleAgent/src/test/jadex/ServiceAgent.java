package test.jadex;

import java.util.logging.Logger;

import core.ProjectLogger;
import data.Position;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.*;

public class ServiceAgent extends MicroAgent {
	
	// Logging support
    private static Logger logger = Logger.getLogger (ProjectLogger.class.getName ());

	protected TestService ts = null;

	@Override
	public void agentCreated() {
		ts = new TestService(getExternalAccess());
		addDirectService(ts);
		
		ts.send(getComponentIdentifier().toString(), getComponentIdentifier().toString() + " started");
//		ProjectLogger.logActivity(false, "Sending", getComponentIdentifier().toString(), -1, null);
		logger.info("Sending "+getComponentIdentifier().toString());
		
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
						
//						ProjectLogger.logActivity(false, "Receiving", getComponentIdentifier().toString(), -1, null);
						logger.info("Receiving "+getComponentIdentifier().toString());
					}
				});
				return null;
			}
		});
	}

	@Override
	public void agentKilled() {
		ts.send(getComponentIdentifier().toString(), new Position(-1,-1,0));
	
//		ProjectLogger.logActivity(false, "Sending", getComponentIdentifier().toString(), -1, null);
		logger.info("Sending "+getComponentIdentifier().toString());
	}

	public TestService getTestService()
	{
		return ts;
	}
}
