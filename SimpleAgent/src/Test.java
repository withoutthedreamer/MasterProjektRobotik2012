import java.io.IOException;

import robot.PioneerRR;
import simulator.Simulator;

import core.OSCommand;
import device.RobotClient;



public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		PioneerRR pionRR = null;
		try {
			pionRR = new PioneerRR("localhost", 6666, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pionRR.shutdown();
	}

}
