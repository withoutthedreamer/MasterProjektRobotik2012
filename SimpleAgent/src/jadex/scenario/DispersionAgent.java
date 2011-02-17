/**
 * 
 */
package jadex.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

import robot.Robot;

import data.BoardObject;
import data.Position;
import jadex.agent.MasterAgent;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgentMetaInfo;

/**
 * @author sebastian
 *
 */
public class DispersionAgent extends MasterAgent {
	
	/** Strategic important points on the map */
	Position[] dispersionPoints = {
			new Position(-21,4,0), /** Top */
			new Position(-29,-1,0), /** Left */
			new Position(-22,-4,0), /** Bottom */
			new Position(-3,-1,0) /** Right */
	};

	@Override public void agentCreated() {
		super.agentCreated();
	}
	@Override public void executeBody() {
		super.executeBody();

		/**
		 * Request all available robot agents.
		 * Do it periodically.
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
//				getBoard().clear();
				pingAllAgents();
				
				waitFor((Integer)getArgument("pingInterval"),this);
				return null;
			}
		});
		
		/**
		 * Request all positions of available robot agents.
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				requestAllPositions();
				return null;
			}
		});

		/**
		 *  Assign goal positions
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
//				getLogger().info("Checking agents on network");
				ArrayList<Integer> positions = new ArrayList<Integer>();
			
				/** Create a sorted number array */
				for (int i=0; i<dispersionPoints.length; i++)
					positions.add(i);
				/** Randomize the positions, that it is not always the same */ // TODO Heuristics
				Collections.shuffle(positions);
				getLogger().info("Shuffle positions: "+positions);

				/** Get the board objects */
				Iterator<Entry<String, BoardObject>> it = getBoard().getSet().iterator();
				
				/**
				 *  Loop through dispersion points
				 *  and find the nearest robot.
				 */
//				for (int i=0; i<dispersionPoints.length; i++) {
				for (int i=0; i<positions.size(); i++) {
					Position curGoal = dispersionPoints[positions.get(i)];
					double minGoalDistance = Double.MAX_VALUE;
					String minDistRobot = null;
					
					if (it.hasNext()) {
						String key = it.next().getKey();
						
						/** Check if it is a robot agent */
						if ( getBoard().getObject(key).getName().equals(Robot.class.getName()) )
						{
							/** Check for the robot distance to goal */
							if (getBoard().getObject(key) != null) {
								if (getBoard().getObject(key).getPosition() != null) {
									double robotDist = getBoard().getObject(key).getPosition().distanceTo(curGoal);

									if (minGoalDistance > robotDist) {
										minGoalDistance = robotDist;
										minDistRobot = key;
									}
								}
							}
						}
					}
					/** Did we found an apropriate robot agent */
					if (minDistRobot != null) {
						getReceiveNewGoalService().send(""+getComponentIdentifier(), minDistRobot, curGoal);

						getLogger().info("Sending goal: "+curGoal+" to "+minDistRobot);
					}
				}
				
				if ((Integer)getArgument("dispersionInterval") != -1)
					waitFor((Integer)getArgument("dispersionInterval"),this);
				
				return null;
			}
		});
	}
	protected void requestAllPositions() {
		getSendPositionService().send(""+getComponentIdentifier(), "request", null);

		getLogger().info(""+getComponentIdentifier()+" requesting all positions");		
	}
	@Override public void agentKilled() {
		super.agentKilled();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("pingInterval", "Time between pings in ms", "Integer", new Integer(30000)),
				new Argument("dispersionInterval", "Time between dispersions in ms", "Integer", new Integer(60000)),
		};
		
		return new MicroAgentMetaInfo("This agent starts up a dispersion scenario.", null, args, null);
	}
}