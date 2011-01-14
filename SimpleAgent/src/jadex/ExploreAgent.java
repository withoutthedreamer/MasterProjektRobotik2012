package jadex;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgentMetaInfo;
import robot.*;

public class ExploreAgent extends PlayerAgent {
	
	protected final static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected static String port = "6666";

	PioneerRsB pion = null;

	@Override
	protected void agentStarted () {
		try {
			pion = new PioneerRsB("localhost", Integer.parseInt(port), 1);
			pion.runThreaded();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	@Override
	protected void agentBody () {
		if (pion == null) {
			killAgent();
		}

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
