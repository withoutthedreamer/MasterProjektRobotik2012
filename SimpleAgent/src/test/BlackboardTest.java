package test;

import junit.framework.TestCase;

import org.junit.Test;

import robot.Robot;
import data.BbNote;
import data.Position;
import device.Blackboard;
import device.Simulation;

public class BlackboardTest extends TestCase {

	static Blackboard blackb = null;
	static Robot explorer = null;
	static Simulation simu = null;
	
	
	@Test
	public void testGetInstance() {
		explorer = new Robot();
		simu = Simulation.getInstance();
		
		blackb = Blackboard.getInstance(explorer);
		assertNotNull(blackb);
		
		blackb.runThreaded();
		assertTrue(blackb.isRunning());
		assertTrue(blackb.isThreaded());

	}

	@Test
	public void testSetSimulation() {
		// for modifying world
		blackb.setSimulation(simu);
		blackb.runThreaded();
		assertTrue(blackb.isRunning());
		assertTrue(blackb.isThreaded());
	}

	@Test
	public void testAdd() {
		BbNote note = new BbNote();
		
		note.setGoal(new Position(7,7,0));
		note.setKey("testnote");
		
		note.setPose(new Position(0,0,0));
		note.setSimu(simu);
		note.setTrackable(explorer);
	}

	@Test
	public void testGet() {
	}

	@Test
	public void testShutdown() {
		blackb.shutdown();
		assertFalse(blackb.isRunning());
		assertFalse(blackb.isThreaded());
	}

}
