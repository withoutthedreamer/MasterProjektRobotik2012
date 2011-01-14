package jadex;

import java.util.Vector;

import simulator.Simulator;
import core.OSCommand;
import data.Position;
import data.SimuObject;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;

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
}
