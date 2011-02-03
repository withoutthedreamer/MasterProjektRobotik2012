package jadex.agent;

import core.Logger;
import data.Board;
import data.BoardObject;
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
	
	/** Global blackboard */
	Board board;
	
	@Override
	public void agentCreated() {
		board = new Board();

		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());

		assert(hs != null && gs != null && ps != null);

		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		
		hs.send(getComponentIdentifier().toString(), "dummy", "Hello");
		Logger.logActivity(false, "sent Hello", getComponentIdentifier().toString(), -1, null);
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
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(content[2].toString());
						
						Logger.logActivity(false, "Receiving "+buf.toString(), getComponentIdentifier().toString(), -1, null);
						
						if (board.getObject((String)content[1]) == null) {
							board.addObject((String)content[1], new BoardObject());
						}
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
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(content[2]);
						
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
				gs.send(getComponentIdentifier().toString(), "r0", new Position(-6,-6,0));
				return null;
			}
		});
		
//		final IComponentStep step = new IComponentStep()
//		{
//			public Object execute(IInternalAccess ia)
//			{
//				waitFor(1000,this);
//				return null;
//			}
//		};
//		waitForTick(step);
	}

	@Override
	public void agentKilled() {
		board.clear();

		hs.send(getComponentIdentifier().toString(), "dummy", "Bye");
		Logger.logActivity(false, "sent Bye", getComponentIdentifier().toString(), -1, null);
	}

	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	
	void goToAll(Position goalPos) {
		// TODO implement
		
	}
	void goToRobot(Position goalPos, String robotName) {
		gs.send(getComponentIdentifier().toString(), robotName, new Position(goalPos));
	}

}
