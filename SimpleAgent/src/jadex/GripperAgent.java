package jadex;

import jadex.bridge.*;
import jadex.micro.MicroAgentMetaInfo;
import data.Position;
import robot.PioneerRG;

public class GripperAgent extends PlayerAgent
{
	protected final static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};

	PioneerRG pion = null;
	Position curPos = null;
	
	@Override
	protected void agentStarted () {
		try {
			pion = new PioneerRG("localhost", port, 1);
			pion.runThreaded();
			pion.setPosition(new Position(-28, 3, 90));
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	@Override
	protected void agentBody () {
		if (pion == null) {
			killAgent();
		}

		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				curPos = pion.getPosition();

				waitFor(1000, this);
				return null;
			}
		};
		waitForTick(step);
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		Argument[] args = {
				new Argument("player path", "dummy", "String"),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String")};
		
		args[0].setDefaultValue(playerCmd[0]);
		args[2].setDefaultValue(playerCmd[1]);
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", 
				null, new IArgument[]{args[0], args[1], args[2]}, null);
	}
}
