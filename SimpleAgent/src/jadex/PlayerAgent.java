package jadex;

import core.Logger;
import core.OSCommand;
import jadex.bridge.*;
import jadex.micro.*;

public class PlayerAgent extends MicroAgent {
	
	protected static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected OSCommand startPlayer = null;
	protected static String port = "6665";

	public void agentCreated()
	{
		Logger.logActivity(false, "running", this.toString(), -1, Thread.currentThread().getName());

		agentStarted();

		// Get the Gui argument, if any
		String[] command = {
			(String)getArgument("player path"),
			new String("-p ").concat( (String)getArgument("player port") ),
			(String)getArgument("player config")
		};
		startPlayer = new OSCommand(command);
			}
	protected void agentStarted () {};
		
	public void executeBody()
	{	
		agentBody();
	}
	protected void agentBody () {};
	
	public void agentKilled()
	{
		agentTerminated();
		
		startPlayer.terminate();
		Logger.logActivity(false, "Termination", this.toString(), Integer.parseInt(port), Thread.currentThread().getName());
	}
	protected void agentTerminated () {};
	
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
