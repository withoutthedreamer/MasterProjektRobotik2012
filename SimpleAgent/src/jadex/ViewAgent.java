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
import jadex.micro.MicroAgentMetaInfo;

public class ViewAgent extends PlayerAgent {

	protected static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/uhhsimu1.cfg"};
	protected OSCommand startPlayer = null;
	protected static String port = "6600";
	
	// API to the simulator (gui)
	protected Simulator simu = null;
	protected Vector<SimuObject> simuObjs = null;
	// Keeps simulation in sync with receiving position updates
//	protected Tracker tracker = null;
	// Max count of robots in gui
	protected final static int robotCount = 3;

	public void agentStarted()
	{
//		tracker = Tracker.getInstance(simu, null);
		
//		PioneerRR[] pionRRList = new PioneerRR[3];
//		
//		for (int i=0; i<robotCount; i++) {
//			tracker.addObject("r"+ Integer.toString(i) , pionRRList[i]);
//		}
	}

	public void agentBody()
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

	public void agentTerminated() {
		simu.shutdown();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		Argument[] args = {
				new Argument("player path", "dummy", "String"),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String")};
		
		args[0].setDefaultValue(playerCmd[0]);
		args[2].setDefaultValue(playerCmd[1]);
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", 
				null, new IArgument[]{args[0], args[1], args[2]}, null);
	}
}
