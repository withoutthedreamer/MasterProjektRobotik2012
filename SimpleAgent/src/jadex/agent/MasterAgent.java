package jadex.agent;

import java.util.logging.Logger;

import data.Board;
import data.BoardObject;
import data.Position;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.*;
import jadex.service.*;

public class MasterAgent extends MicroAgent
{
	/** Logging support */
    static Logger logger = Logger.getLogger (MasterAgent.class.getName ());
	
	/** Services */
	HelloService hs;
	SendPositionService ps;
	ReceiveNewGoalService gs;
	GoalReachedService gr;
	
	/** Blackboard */
	Board board;
	
	@Override public void agentCreated()
	{
		board = new Board();

		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());
		gr = new GoalReachedService(getExternalAccess());

		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		addDirectService(gr);
		
		hs.send(""+getComponentIdentifier(), "", "Hello");

		logger.info(""+getComponentIdentifier()+" sending hello ");
	}

	@Override public void executeBody()
	{
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
						
						logger.info(""+getComponentIdentifier()+" receiving "+buf);
						
						String id = (String)content[1];
						String name = (String)content[2];
						
						if (board.getObject(id) == null) {
							BoardObject bo = new BoardObject();
							bo.setName(name);
							
							board.addObject(id, bo);
							logger.info(""+getComponentIdentifier()+" adding to board: "+id);
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
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(content[2]);
						
						String id = (String)content[1];
						Position p = (Position)content[2];
						
						BoardObject bo = board.getObject(id); 
						
						if (bo != null && p != null) {
							bo.setPosition(p);
						}

						logger.finer(""+getComponentIdentifier()+" receiving "+buf);
					}
				});
				return null;
			}
		});
		
		/**
		 *  Register to goal reached service
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getGoalReachedService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(" "+content[2].toString());
						
						BoardObject bo = board.getObject((String)content[1]); 
						if (bo != null) {
							Position pose = (Position)content[2];
							if (pose != null) {
								if (bo.getGoal() != null)
									bo.getGoal().setPosition(new Position(0,0,0));
							}
						}
						logger.info(""+getComponentIdentifier()+" receiving goal reached "+buf);
						
					}
				});
				return null;
			}
		});
		
//		/**
//		 * Request all robot agents.
//		 * Do it periodically.
//		 */
//		final IComponentStep step = new IComponentStep()
//		{
//			public Object execute(IInternalAccess ia)
//			{
//				pingAllAgents();
//				
//				waitFor(30000,this);
//				return null;
//			}
//		};
//		waitForTick(step);
//		
//		/** Send a 1st goal */
//		waitFor(5000, new IComponentStep()
//		{
//			public Object execute(IInternalAccess ia)
//			{
//				gs.send(getComponentIdentifier().toString(), "all", new Position(-6.5,-1.5,0));
//				return null;
//			}
//		});
		
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
	public void pingAllAgents()
	{
		getHelloService().send(""+getComponentIdentifier(), "", "ping");

		logger.info(""+getComponentIdentifier()+" pinging all agents");
	}
	@Override public void agentKilled()
	{
		board.clear();

		hs.send(""+getComponentIdentifier(), "", "Bye");

		logger.info(""+getComponentIdentifier()+" sending bye");
	}

	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	public GoalReachedService getGoalReachedService() { return gr; }
	
	void goToAll(Position goalPos) {
		// TODO implement
		
	}
	void goToRobot(Position goalPos, String robotName) {
		gs.send(""+getComponentIdentifier(), robotName, new Position(goalPos));
	}
	protected Board getBoard() {
		return board;
	}

	public Logger getLogger() {
		return logger;
	}
}
