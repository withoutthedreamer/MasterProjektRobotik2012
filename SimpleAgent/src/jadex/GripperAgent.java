package jadex;

import robot.PioneerRG;
import data.Position;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class GripperAgent extends MicroAgent
{
	PioneerRG pionRG = null;

	public void agentCreated(){}

	public void executeBody()
	{
		System.out.println(getArgument("Starting up gripper agent.."));
		try {
			pionRG = new PioneerRG("localhost", 6666, 1);
			pionRG.setPlanner("localhost", 6685);
			// Planner
			pionRG.setPosition(new Position(-28, 3, 90));
		} catch (Exception e) {
			e.printStackTrace();
			killAgent();
		}
	}
	public void agentKilled()
	{
		pionRG.shutdown();
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the gripper agent.", 
				null, new IArgument[]{
				new Argument("welcome text", "This parameter is the argument given to the agent.", "String", "Hello world, this is a Jadex micro agent."),	
		}, null);
	}
}
