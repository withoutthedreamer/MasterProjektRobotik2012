package jadex;

import core.OSCommand;
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

	protected String[] playerCmd={"/usr/local/bin/player","-p 6685","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected OSCommand startPlanner = null;

	public void agentCreated()
	{
//		System.out.println(getArgument("Starting up gripper agent.."));
//		ms = new MessageService(getExternalAccess());
//		addDirectService(ms);
//		ms.tell("GripperAgent", "Starting up..");

		startPlanner = new OSCommand(playerCmd);

		try {
			pionRG = new PioneerRG("localhost", 6666, 1);
			pionRG.setPlanner("localhost", 6685);
			// Planner
			pionRG.setPosition(new Position(-28, 3, 90));
		} catch (Exception e) {
			if(startPlanner.isRunning() == true) {
				startPlanner.terminate();
			}
			e.printStackTrace();
//			killAgent();
		}
	}
	public void executeBody()
	{
		if (pionRG == null) {
			killAgent();
		}
		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				data.Position curPos = pionRG.getPosition();
//				ms.tell("GripperAgent", curPos.toString());
//				ms.tell("GripperAgent", "test");

				waitFor(2000, this);
				return null;
			}
		};
		waitForTick(step);
	}
	public void agentKilled()
	{
//		ms.tell("GripperAgent", "Shutting down..");
		if (pionRG != null) {
			pionRG.shutdown();
		}
		if (startPlanner.isRunning() == true) {
			startPlanner.terminate();
		}
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the gripper agent.", 
				null, new IArgument[]{
				new Argument("welcome text", "This parameter is the argument given to the agent.", "String", "Hello world, this is a Jadex micro agent."),	
		}, null);
	}
}
