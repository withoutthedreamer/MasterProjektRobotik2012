/**
 * 
 */
package jadex.scenario;

import java.util.ArrayList;
import java.util.Collections;

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
				ArrayList<Integer> positions = new ArrayList<Integer>();

				/** Create a sorted number array */
				for (int i=0; i<dispersionPoints.length; i++)
					positions.add(i);

				/** Get all robots */
				ArrayList<String> robotKeys = getBoard().getTopicList(Robot.class.getName());
				Collections.shuffle(robotKeys);

				for (int i=0; i<robotKeys.size(); i++) {

					int nearestGoal = -1;
					Position curGoal = null;

					/** Get the robot object */
					BoardObject bo = getBoard().getObject(robotKeys.get(i)); 

					/** Check for the robot distance to goal */
					if (bo != null) {

						Position robotPose = bo.getPosition();

						if (robotPose != null) {

							double minGoalDistance = Double.MAX_VALUE;


							for (int i1=0; i1<positions.size(); i1++) {
								curGoal = dispersionPoints[positions.get(i1)];

								double robotDist = robotPose.distanceTo(curGoal);

								if (minGoalDistance > robotDist) {
									minGoalDistance = robotDist;
									nearestGoal = i1;
								}
							}
						}
					}

					/** Did we found an apropriate robot goal */
					if (nearestGoal >= 0 && curGoal != null) {
						getReceiveNewGoalService().send(""+getComponentIdentifier(), robotKeys.get(i), curGoal);

						getLogger().info("Sending goal: "+curGoal+" to "+robotKeys.get(i));
						/** Remove goal from list */
						positions.remove(nearestGoal);
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
