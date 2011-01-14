package jadex;

import jadex.bridge.*;
import jadex.micro.MicroAgentMetaInfo;
import data.Position;
import robot.PioneerRG;

public class GripperAgent extends PlayerAgent
{
	protected final static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected static String port = "6668";

	PioneerRG pion = null;
	Position curPos = null;
	
	@Override
	protected void agentStarted () {
		try {
			pion = new PioneerRG("localhost", Integer.parseInt(port), 1);
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
		return new MicroAgentMetaInfo("This agent starts up the Explorer agent.", 
				null, new IArgument[]{
				new Argument("player path", "This parameter is the argument given to the agent.", "String", 
						playerCmd[0]),	
				new Argument("player port", "This parameter is the argument given to the agent.", "int", 
						port),	
				new Argument("player config", "This parameter is the argument given to the agent.", "String",
						playerCmd[1])
		}, null);
	}
}
