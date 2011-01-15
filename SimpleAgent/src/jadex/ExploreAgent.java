package jadex;

import jadex.bridge.*;
import jadex.micro.MicroAgentMetaInfo;
import robot.*;

public class ExploreAgent extends PlayerAgent {
	
	PioneerRsB pion = null;

	@Override
	public void agentCreated()
	{
		super.agentCreated();
		try {
			pion = new PioneerRsB("localhost", port, 1);
			pion.runThreaded();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	@Override
	public void executeBody()
	{
		super.executeBody();
		if (pion == null) {
			killAgent();
		}
		// TODO no blocking
//		while (pion.isRunning() == true);
	}
	@Override
	public void agentKilled() {
		
		super.agentKilled();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player path", "dummy", "String", playerPath),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", null, args, null);
	}
}
