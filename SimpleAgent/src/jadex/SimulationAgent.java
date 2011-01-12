package jadex;

import core.OSCommand;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class SimulationAgent extends MicroAgent {

	/** The message service. */
	protected MessageService ms;

	protected final String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg"};
	protected OSCommand startSimu = null;

	public void agentCreated()
	{
		ms = new MessageService(getExternalAccess());
		addDirectService(ms);
		ms.tell("SimulationAgent", "Starting up..");

		// Get the Gui argument, if any
		String[] path = {(String)getArgument("command path"),null};
		startSimu = new OSCommand(path);

    }
	public void executeBody()
	{
	}
	public void agentKilled()
	{
		ms.tell("SimulationAgent", "Shutting down..");
		startSimu.terminate();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the Explorer agent.", 
				null, new IArgument[]{
				new Argument("command path", "This parameter is the argument given to the agent.", "String", 
					"/usr/local/bin/player /Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg"),	
		}, null);
	}
}
