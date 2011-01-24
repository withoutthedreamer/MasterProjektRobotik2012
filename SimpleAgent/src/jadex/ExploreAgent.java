package jadex;

import device.DeviceNode;
import jadex.bridge.*;
import jadex.micro.MicroAgentMetaInfo;
import robot.*;

public class ExploreAgent extends PlayerAgent {
	
	ExploreRobot explorer = null;
	DeviceNode devices = null;

	@Override
	public void agentCreated()
	{
		super.agentCreated();
		try {
			devices = new DeviceNode("localhost", port);
			devices.runThreaded();
			explorer = new ExploreRobot(devices);
			explorer.runThreaded();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	@Override
	public void executeBody()
	{
		super.executeBody();
		if (explorer == null) {
			killAgent();
		}
		// TODO no blocking
//		while (pion.isRunning() == true);
	}
	@Override
	public void agentKilled() {
		
		super.agentKilled();
		explorer.shutdown();
		devices.shutdown();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player path", "dummy", "String", playerPath),
				new Argument("player port", "dummy", "Integer", new Integer(port)),	
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg")};
		
		return new MicroAgentMetaInfo("This agent starts up a Player agent.", null, args, null);
	}
}
