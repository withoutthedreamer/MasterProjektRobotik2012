package jadex;

import robot.PioneerRG;
import robot.PioneerRsB;
import data.Position;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class Uhh1Agent extends MicroAgent {
	PioneerRsB pionRsB = null;
	PioneerRG pionRG = null;

	public void agentCreated(){}

	public void executeBody()
	{
		System.out.println(getArgument("Starting up environment.."));
		try {
			pionRsB = new PioneerRsB("localhost", 6665, 0);
			pionRG = new PioneerRG("localhost", 6666, 1);
			pionRG.setPlanner("localhost", 6685);
			// Planner
			pionRG.setPosition(new Position(-28, 3, 90));
			pionRsB.setGoal(new Position(0,0,0));
		} catch (Exception e) { e.printStackTrace(); }
	}
	public void agentKilled()
	{
		pionRsB.shutdown();
		pionRG.shutdown();
		// TODO killall player
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the Player environment.", 
				null, new IArgument[]{
				new Argument("welcome text", "This parameter is the argument given to the agent.", "String", "Hello world, this is a Jadex micro agent."),	
		}, null);
	}
}
