package jadex;

import core.Logger;
import core.OSCommand;
import jadex.bridge.*;
import jadex.micro.*;

public class KillPlayerAgent extends MicroAgent {
	
	protected static String[] killCmd={"/usr/bin/killall","player"};
	protected OSCommand stopPlayer = null;

	public void agentCreated()
	{
		Logger.logActivity(false, "running", this.toString(), -1, Thread.currentThread().getName());

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
		Logger.logActivity(false, "Termination", this.toString(), -1, Thread.currentThread().getName());
	}
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		Argument[] args = {
				new Argument("killall path", "dummy", "String"),
				new Argument("process name", "dummy", "String")};

		args[0].setDefaultValue(killCmd[0]);
		args[2].setDefaultValue(killCmd[1]);
		
		return new MicroAgentMetaInfo("This agent kills all 'player' instances on this host and exits.",
				null, new IArgument[]{args[0], args[1], args[2]}, null);
	}
}