package jadex;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgentMetaInfo;

/**
 * Generic agent class for PlayerStage requirements
 * @author sebastian
 *
 */
public class SimulationAgent extends PlayerAgent {

	protected static String[] playerCmd = {"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg"};
	
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
