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
