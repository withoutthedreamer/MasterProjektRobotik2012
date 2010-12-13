package usecase;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import robot.PioneerRso;

public class RangerSonarTest {

	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	public static void main (String[] args) {
		try {
			PioneerRso pionRso = new PioneerRso("localhost", 6665, 0);
			
			// Wait until enter is pressed
			in.readLine();
			pionRso.shutdown();

		} catch (Exception e) { e.printStackTrace(); }
	}
}
