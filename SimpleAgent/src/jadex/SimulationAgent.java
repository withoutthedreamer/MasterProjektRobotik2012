package jadex;

import java.io.IOException;

import robot.PioneerRsB;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class SimulationAgent extends MicroAgent {

	/** The message service. */
	protected MessageService ms;

	PioneerRsB pionRsB = null;

	public void agentCreated()
	{
//		System.out.println(getArgument("Starting up explore agent.."));
		ms = new MessageService(getExternalAccess());
		addDirectService(ms);
		ms.tell("SimulationAgent", "Starting up..");

    }
	public void executeBody()
	{
		try {
			String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/uhh1.cfg"};

//			ms.tell("SimulationAgent", OSCommand.run("/usr/local/bin/player ~/robotcolla/SimpleAgent/player/uhh1"));
			System.out.print("SimulationAgent" + OSCommand.run(playerCmd));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void agentKilled()
	{
		ms.tell("SimulationAgent", "Shutting down..");
//		pionRsB.shutdown();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the Explorer agent.", 
				null, new IArgument[]{
				new Argument("Command line options", "This parameter is the argument given to the agent.", "String", "player uhh1.cfg"),	
		}, null);
	}
}
