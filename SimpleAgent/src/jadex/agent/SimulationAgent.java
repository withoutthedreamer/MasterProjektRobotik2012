package jadex.agent;

import jadex.PlayerAgent;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgentMetaInfo;

/**
 * Generic agent class for PlayerStage requirements
 * @author sebastian
 *
 */
public class SimulationAgent extends PlayerAgent {
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(true)),
				new Argument("player path", "dummy", "String", playerPath),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", null, args, null);
	}
}
