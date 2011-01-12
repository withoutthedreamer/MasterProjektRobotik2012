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
	protected data.Position curPos = null;

	protected final String[] playerCmdStd={"/usr/local/bin/player","-p 6685","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};
	protected OSCommand startPlanner = null;

	public void agentCreated()
	{
//		System.out.println(getArgument("Starting up gripper agent.."));
		ms = new MessageService(getExternalAccess());
		addDirectService(ms);
		ms.tell("GripperAgent", "Starting up..");

		// Get the Gui argument, if any
		String[] path = {(String)getArgument("command path"),null};
		startPlanner = new OSCommand(path);

		try {
			pionRG = new PioneerRG("localhost", 6666, 1);
			pionRG.setPlanner("localhost", 6685);
			// Planner
			pionRG.setPosition(new Position(-28, 3, 90));
		} catch (Exception e) {
			startPlanner.terminate();
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
				curPos = pionRG.getPosition();
				ms.tell("GripperAgent", curPos.toString());
//				ms.tell("GripperAgent", "test");

				waitFor(1000, this);
				return null;
			}
		};
		waitForTick(step);
	}
	public void agentKilled()
	{
		ms.tell("GripperAgent", "Shutting down..");
		if (pionRG != null) {
			pionRG.shutdown();
		}
		startPlanner.terminate();
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the gripper agent.", 
				null, new IArgument[]{
				new Argument("command path", "This parameter is the argument given to the agent.", "String", 
						"/usr/local/bin/player -p 6685 /Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"),	
		}, null);
	}
}
