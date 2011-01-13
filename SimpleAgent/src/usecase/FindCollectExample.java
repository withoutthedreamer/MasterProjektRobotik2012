package usecase;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import robot.PioneerRG;
import robot.PioneerRsB;
import simulator.Simulator;
import data.Position;
import device.Blackboard;

public class FindCollectExample {

	private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

	
	public static void main (String[] args) {
		try {
//			PioneerSB pionSB = new PioneerSB("localhost", 6665, 0);
			PioneerRsB pionRsB = new PioneerRsB("localhost", 6665, 0);
//			PioneerLG pionLG = new PioneerLG("localhost", 6666, 1);
			PioneerRG pionRG = new PioneerRG("localhost", 6666, 1);
			pionRG.setPlanner("localhost", 6685);
			pionRG.setPosition(new Position(-16,3,Math.toRadians(90)));
			// Task synchronization
//			Blackboard blackb= Blackboard.getInstance(pionRG);
			// wants to write notes
//			pionRsB.setBlackboard(blackb);
			
			// for modifying world
//			Simulator simu   = Simulator.getInstance("localhost", 6665);
//			blackb.setSimulation(simu);
//			Tracker tracker  = Tracker.getInstance(simu, null);
//			tracker.addObject("r0", pionSB);
//			tracker.addObject("r1", pionLG);
//			while (true) {
//				Thread.sleep(1000);
//				System.out.println(pionRG.getPosition().toString());
//			}
			// Wait until enter is pressed
			in.readLine();
//			tracker.shutdown()
//			blackb.shutdown();
			pionRsB.shutdown();
			pionRG.shutdown();
//			simu.shutdown();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
}
