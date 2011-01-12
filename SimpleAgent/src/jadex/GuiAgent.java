package jadex;

import java.util.Vector;

import simulator.Simulator;
import core.OSCommand;
import data.Position;
import data.SimuObject;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class GuiAgent extends MicroAgent {

	protected final String[] start={"/usr/local/bin/player","-p 6600","/Users/sebastian/robotcolla/SimpleAgent/player/uhhsimu1.cfg"};
	protected OSCommand player = null;
	// API to the simulator (gui)
	protected Simulator simu = null;
	protected Vector<SimuObject> simuObjs = null;
	// Keeps simulation in sync with receiving position updates
//	protected Tracker tracker = null;
	// Max count of robots in gui
	protected final static int robotCount = 3;

	public void agentCreated()
	{
		// Get the Gui argument, if any
		String[] path = {(String)getArgument("command path"),null};
		player = new OSCommand(path);

//		tracker = Tracker.getInstance(simu, null);
		
//		PioneerRR[] pionRRList = new PioneerRR[3];
//		
//		for (int i=0; i<robotCount; i++) {
//			tracker.addObject("r"+ Integer.toString(i) , pionRRList[i]);
//		}
	}

	public void executeBody()
	{		
		waitFor(2000, new IComponentStep()
		{
			public Object execute(IInternalAccess args)
			{
				simu = Simulator.getInstance("localhost", 6600);
				return null;
			}
		});
		
		final IComponentStep step = new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				if (simuObjs != null) {
					int count = simuObjs.size();
					for (int i=0; i<count; i++) {
						// update objects position
						String   id  = simuObjs.get(i).getId();
						Position pos = simuObjs.get(i).getObject().getPosition();

						// update the simulator
						simu.setObjectPos(id, pos);
					}
				}

				// TODO Argument
				waitFor(100, this);
				return null;
			}
		};
		waitForTick(step);
	}

	public void agentKilled() {
		simu.shutdown();
		player.terminate();
	}

	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the gui.", 
				null, new IArgument[]{
				new Argument("command path", "This parameter is the argument given to the agent.", "String", 
					"/usr/local/bin/player -p 6600 /Users/sebastian/robotcolla/SimpleAgent/player/uhhsimu1.cfg"),	
		}, null);
	}
}
