package jadex.agent;

import data.Board;
import data.BoardObject;
import data.Goal;
import data.Position;
import device.IPlannerListener;
import jadex.bridge.*;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;

import jadex.micro.MicroAgentMetaInfo;


public class CollectAgent extends NavAgent
{
    /** Data */
    Board bb;
    String curGoalKey = null;
    /** Where to store objects */
    Position depotPose;

	@Override public void agentCreated()
	{
		super.agentCreated();
		
		bb = new Board();
		depotPose = getRobot().getPosition();
	}
	
	@Override public void executeBody()
	{
	    super.executeBody();

	    /**
	     * Register new goal event callback
	     */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                getReceiveNewGoalService().addChangeListener(new IChangeListener()
                {
                    public void changeOccurred(ChangeEvent event)
                    {
                        Object[] content = (Object[])event.getValue();
                        
                        String id = (String) content[1];
                        Position newGoal = (Position) content[2];
                        
                        // TODO compare to objects in board
                        if (id.equals("collectGoal") == true)
                        {
                            //TODO check goal radius here
                            curGoalKey = ""+newGoal;
                            if (bb.getObject(curGoalKey) == null)
                            {
                                BoardObject newBo = new BoardObject();
                                newBo.setTopic(id);
                                newBo.setPosition(newGoal);
                                Goal boGoal = new Goal();
                                boGoal.setPosition(depotPose);
                                newBo.setGoal(boGoal);
                                bb.addObject(curGoalKey, newBo);
                            }
                        }
                    }
                });
                return null;
            }
        });
                
        /**
         *  Register planner callback
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                if (getRobot().getPlanner() != null) /** Does it have a planner? */
                {
                    getRobot().getPlanner().addIsDoneListener(new IPlannerListener()
                    {
                        @Override public void callWhenIsDone()
                        {
                            bb.getObject(curGoalKey).setDone(true);
//                            bb.getTopicList("collectGoal")[0];
                        }
                    });
                }
                return null;
            }
        });
        
        /**
         * Send goals periodically 
         */
        final IComponentStep step = new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                if (curGoalKey != null)
                {
                    BoardObject curOb = bb.getObject(curGoalKey);
                    if (curOb != null)
                    {
                        if (curOb.isDone() == false)
                        {
                            getRobot().setGoal(curOb.getPosition());
                        }
                    }
                }
                
                waitFor(5000,this);
                return null;
            }
        };
        waitForTick(step);
	}
	
	@Override public void agentKilled()
	{
	    super.agentKilled();
	    
	    bb.clear();
	}
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
                new Argument("host", "Player", "String", "localhost"),
				new Argument("port", "Player", "Integer", new Integer(6665)),
                new Argument("index", "Robot index", "Integer", new Integer(0)),
                new Argument("devIndex", "Device index", "Integer", new Integer(0)),
				new Argument("X", "Meter", "Double", new Double(0.0)),
				new Argument("Y", "Meter", "Double", new Double(0.0)),
				new Argument("Angle", "Degree", "Double", new Double(0.0)),
                new Argument("laser", "Laser ranger", "Boolean", new Boolean(true)),
                new Argument("simulation", "Simulation device", "Boolean", new Boolean(true))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a collect agent.", null, args, null);
	}
}