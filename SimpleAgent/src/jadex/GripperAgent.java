package jadex;

import robot.PioneerRG;
import data.Position;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class GripperAgent extends MicroAgent
{
	/** The message service. */
	protected MessageService ms;

	PioneerRG pionRG = null;

	public void agentCreated()
	{
//		System.out.println(getArgument("Starting up gripper agent.."));
		ms = new MessageService(getExternalAccess());
		addDirectService(ms);
		ms.tell("GripperAgent", "Starting up..");
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
	public void executeBody()
	{
		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				data.Position curPos = pionRG.getPosition();
				ms.tell("GripperAgent", curPos.toString());
//				ms.tell("GripperAgent", "test");

				waitFor(2000, this);
				
				return null;
			}
		};
		waitFor(2000,step);
	}
	public void agentKilled()
	{
		ms.tell("GripperAgent", "Shutting down..");
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
