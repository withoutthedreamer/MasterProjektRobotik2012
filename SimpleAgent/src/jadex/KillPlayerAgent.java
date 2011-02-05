package jadex;

import java.util.logging.Logger;

import core.ProjectLogger;
import core.OSCommand;
import jadex.bridge.*;
import jadex.micro.*;

public class KillPlayerAgent extends MicroAgent {
	
	// Logging support
    private static Logger logger = Logger.getLogger (ProjectLogger.class.getName ());

	protected OSCommand stopPlayer = null;

	public void agentCreated()
	{
//		ProjectLogger.logActivity(false, "running", this.toString(), -1, Thread.currentThread().getName());
		logger.info("Running "+getComponentIdentifier().toString());

		// Get the Gui argument, if any
		String[] command = {
				(String)getArgument("killall path"),
				(String)getArgument("process name")
		};
		stopPlayer = new OSCommand(command);
	}

	public void executeBody() {
		stopPlayer.waitFor();
		killAgent();
	}
	
	public void agentKilled()
	{		
		stopPlayer.terminate();
//		ProjectLogger.logActivity(false, "Termination", this.toString(), -1, Thread.currentThread().getName());
		logger.info("Termination "+getComponentIdentifier().toString());
	}
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		Argument[] args = {
				new Argument("killall path", "dummy", "String", "/usr/bin/killall"),
				new Argument("process name", "dummy", "String", "player")};
		
		return new MicroAgentMetaInfo("This agent kills all 'player' instances on this host and exits.", null, args, null);
	}
}