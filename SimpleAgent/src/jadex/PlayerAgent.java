package jadex;

import core.Logger;
import core.OSCommand;
import jadex.bridge.*;
import jadex.micro.*;

public class PlayerAgent extends MicroAgent {
	
	protected static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected OSCommand startPlayer = null;
	protected static int port = 6665;

	public void agentCreated()
	{
		Logger.logActivity(false, "running", this.toString(), port, Thread.currentThread().getName());

		agentStarted();

		// Get the Gui argument, if any
		String[] command = {
			((String)getArgument("player path")),
			new String("-p ").concat( String.valueOf(((Integer)getArgument("player port")).intValue()) ),
			((String)getArgument("player config"))
		};
		if (command[0] != "") {
			startPlayer = new OSCommand(command);
		}
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
		Logger.logActivity(false, "Termination", this.toString(), port, Thread.currentThread().getName());
	}
	protected void agentTerminated () {};
	
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
