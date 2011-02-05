package jadex;

import core.ProjectLogger;
import core.OSCommand;
import jadex.bridge.*;
import jadex.micro.*;

public class PlayerAgent extends MicroAgent {
	
	protected static String playerPath="/usr/local/bin/player";
	protected OSCommand startPlayer = null;
	protected static int port = 6665;

	public void agentCreated()
	{
		ProjectLogger.logActivity(false, "running", this.toString(), port, Thread.currentThread().getName());
		
		port = ((Integer)getArgument("player port")).intValue();

		if ((Boolean)getArgument("requires player") == true) {
			// Get the Gui argument, if any
			String[] command = {
					((String)getArgument("player path")),
					new String("-p ").concat( String.valueOf(port) ),
					((String)getArgument("player config"))
			};

			startPlayer = new OSCommand(command);
		}
	}

//	protected void agentStarted () {};

	public void executeBody()
	{	
		// TODO no blocking
//		agentBody();
//		if (startPlayer != null) {
//			startPlayer.waitFor();
//		}
	}
	
	public void agentKilled()
	{
		if (startPlayer != null) {
			startPlayer.terminate();
		}
		ProjectLogger.logActivity(false, "Termination", this.toString(), port, Thread.currentThread().getName());
	}
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player path", "dummy", "String", playerPath),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", null, args, null);
	}
}
