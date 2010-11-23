import java.io.BufferedReader;
import java.io.InputStreamReader;

import robot.PioneerLG;
import robot.PioneerSB;
import simulator.Simulator;
import simulator.Tracker;
import data.Position;
import device.Blackboard;

public class FindCollectExample {

	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

	
	@SuppressWarnings("unused")
	public static void main (String[] args) {
		try {
			PioneerSB pionSB = new PioneerSB("localhost", 6665, 0);
			PioneerLG pionLG = new PioneerLG("localhost", 6666, 1);
			pionLG.setPlanner("localhost", 6685);
			pionLG.setPose(new Position(-3,-5,Math.toRadians(90)));
			// Task synchronization
			Blackboard blackb= Blackboard.getInstance(pionLG);
			// wants to write notes
			pionSB.setBlackboard(blackb);
			
			// for modifying world
			Simulator simu   = Simulator.getInstance("localhost", 6665);
			blackb.setSimulation(simu);
//			Tracker tracker  = Tracker.getInstance(simu, null);
//			tracker.addObject("r0", pionSB);
//			tracker.addObject("r1", pionLG);
		
			// Wait until enter is pressed
			in.readLine();
//			tracker.shutdown();
			pionSB.shutdown();
			pionLG.shutdown();
			simu.shutdown();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
}
