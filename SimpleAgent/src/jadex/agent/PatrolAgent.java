package jadex.agent;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import robot.AntRobot;
import robot.PatrolRobot;
import data.Position;
import device.external.ILocalizeListener;
import device.external.IPlannerListener;


public class PatrolAgent extends NavAgent {
	
	PatrolRobot robot;
	
	public void agentCreated() {
		super.agentCreated();
		
		robot = new AntRobot(super.getDeviceNode().getDeviceListArray());
		robot.setRobotId("r"+(Integer)getArgument("robId"));
	}
	
	@Override public void executeBody() {
		addListenerToReceiveNewGoalService();
		addListenerToPlanner();
		addListenerToLocalizer();
		addListenerToSendPositionService();
		robot.doStep();
	}
	
	@Override public void agentKilled() {
		
  }
	
	private void addListenerToReceiveNewGoalService() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getReceiveNewGoalService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						
						Position goal = (Position)content[2];
						robot.setGoal(goal);
					}
				});
				return null;
			}
		});
	}
	
	private void addListenerToPlanner() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if (robot.getPlanner() != null) /** Does it have a planner? */
				{
					robot.getPlanner().addIsDoneListener(new IPlannerListener()
					{
						@Override public void callWhenIsDone()
						{
							gr.send(""+getComponentIdentifier(), ""+robot,robot.getPlanner().getGoal());

							logger.fine(""+getComponentIdentifier()+" "+robot+" reached goal "+robot.getPlanner().getGoal());
						}

						@Override public void callWhenAbort() {
              /** Set the goal again. */
							//robot.setGoal(robot.getGoal());
							logger.info("Aborted");
						}

						@Override public void callWhenNotValid() {
              logger.info("No valid path");
						}
					});
				}
				return null;
			}
		});
	}

	private void addListenerToLocalizer() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if (robot.getLocalizer() != null) { /** Does it have a localizer? */ 
				    /**
				     * Register a localize callback.
				     * When it is called send the new position.
				     */
					robot.getLocalizer().addListener(new ILocalizeListener() {
						@Override public void newPositionAvailable(Position newPose) {
							sendPosition(newPose);
						}
					});
				} else {
				    /**
			         * Read position periodically
			         */
			        final IComponentStep step = new IComponentStep() {
			            public Object execute(IInternalAccess ia) {
			                Position curPose = robot.getPosition();
			                sendPosition(curPose);
			                logger.finest("Sending new pose "+curPose+" for "+robot);
			               
			                waitFor(1000,this);
			                return null;
			            }
			        };
			        waitForTick(step);
				}
				return null;
			}
		});
	}

	private void addListenerToSendPositionService() {
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getSendPositionService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						String id = (String)content[1];
						
						/** Sending position on request */
						if (id.equals("request"))
						{
							sendPosition(robot.getPosition());
						}
					}
				});
				return null;
			}
		});
	}
}
