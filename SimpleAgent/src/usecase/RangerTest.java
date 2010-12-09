package usecase;

import robot.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RangerTest {

	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	public static void main (String[] args) {
		try {
			PioneerR pionR = new PioneerR("localhost", 6666, 1);
			
			// Wait until enter is pressed
			in.readLine();
			pionR.shutdown();

		} catch (Exception e) { e.printStackTrace(); }
	}
}
