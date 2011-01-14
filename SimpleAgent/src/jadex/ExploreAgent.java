package jadex;

import robot.*;

public class ExploreAgent extends PlayerAgent {
	
	protected final static String[] playerCmd={"/usr/local/bin/player","/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg"};

	PioneerRsB pion = null;

	@Override
	protected void agentStarted () {
		try {
			pion = new PioneerRsB("localhost", port, 1);
			pion.runThreaded();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	@Override
	protected void agentBody () {
		if (pion == null) {
			killAgent();
		}
		// TODO no blocking
//		while (pion.isRunning() == true);
	}
}
