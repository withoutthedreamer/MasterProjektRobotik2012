package jadex.agent;

import data.Board;
import jadex.bridge.*;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;

import jadex.micro.MicroAgentMetaInfo;


public class CollectAgent extends NavAgent
{
    /** Data */
    Board bb;

	@Override public void agentCreated()
	{
		super.agentCreated();
		
		bb = new Board();
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
                final ConsoleAgent ca = (ConsoleAgent)ia;
                ca.getReceiveNewGoalService().addChangeListener(new IChangeListener()
                {
                    public void changeOccurred(ChangeEvent event)
                    {
                        Object[] content = (Object[])event.getValue();
                        
                        String id = (String) content[1];
                        
                        // TODO compare to objects in board
                        if (id.equals("collectGoal") == true)
                        {
                            if (bb.getObject(id) == null)
                            {
                                bb.addObject(id, null);
                            }
                        }
                    }
                });
                return null;
            }
        });
                
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