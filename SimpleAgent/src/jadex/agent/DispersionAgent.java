/**
 * 
 */
package jadex.agent;

import java.util.ArrayList;
import java.util.Collections;

import robot.Robot;

import data.BoardObject;
import data.Position;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgentMetaInfo;

/**
 * @author sebastian
 *
 */
public class DispersionAgent extends MasterAgent
{
	Integer dispersionInterval;
	
	/**
	 *  Strategic important points on the map.
	 *  Highest assign priority is at top of the list. 
	 */
	// TODO Outsource in file
	Position[] dispersionPoints =
	{
			new Position(-21,4,0), /** Top */
			new Position(-29,-1,0), /** Left */
			new Position(-22,-4,0), /** Bottom */
			new Position(-22,-1.5,0), /** Center */
			new Position(-14,-1.5,0), /** Center right */
			new Position(-3,-1,0) /** Right */
	};

	@Override public void agentCreated()
	{
		super.agentCreated();
		dispersionInterval = (Integer)getArgument("dispersionInterval");
	}
	@Override public void executeBody()
	{
		super.executeBody();

		/**
		 * Request all available robot agents to respond.
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
		 * Wait a little so that all robots have a true ground pose.
		 */
//		scheduleStep(new IComponentStep()
		waitFor(1000, new IComponentStep()
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
//		scheduleStep(new IComponentStep()
		waitFor(1100, new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ArrayList<Integer> positions = new ArrayList<Integer>();

				/** Create a sorted number array */
				for (int i=0; i<dispersionPoints.length; i++)
				{
					positions.add(i);
				}
				
				/** Get all robots */
				ArrayList<String> robotKeys = getBoard().getTopicList(Robot.class.getName());
				/** Randomize robot goal assign order */
				Collections.shuffle(robotKeys);
				
				getLogger().finer("Shuffle robots: "+robotKeys);

				/**
				 * Assign a goal to each robot
				 */
				for (int i=0; i<robotKeys.size(); i++)
				{
					int goalIndex = -1;
					Position nearestGoal = null;
					Position curGoal = null;

					/** Get the robot board object */
					BoardObject bo = getBoard().getObject(robotKeys.get(i)); 

					/** Check for the robot distance to goal */
					if (bo != null)
					{
						Position robotPose = bo.getPosition();

						if (robotPose != null)
						{
							getLogger().finer(robotKeys.get(i)+" pose: "+robotPose);

							double minGoalDistance = Double.MAX_VALUE;

							for (int i1=0; i1<positions.size(); i1++)
							{
								curGoal = dispersionPoints[positions.get(i1)];
								logger.finer("Checking for goal: "+curGoal);

								double robotDist = robotPose.distanceTo(curGoal);
								logger.finer("Distance: "+robotDist);

								if (minGoalDistance > robotDist)
								{
									minGoalDistance = robotDist;
									goalIndex = i1;
									nearestGoal = curGoal;
								}
							}
						}
						else
						{
							logger.info("Robot pose null from: "+bo);
						}
					}
					else
					{
						logger.info("Board object null from key: "+robotKeys.get(i));
					}

					/** Did we found an appropriate robot goal */
					if (nearestGoal != null)
					{
						getLogger().finer("Nearest goal is "+nearestGoal+" index: "+positions.get(goalIndex));
						
						getReceiveNewGoalService().send(""+getComponentIdentifier(), robotKeys.get(i), nearestGoal);

						getLogger().finer("Sending goal: "+nearestGoal+" to "+robotKeys.get(i));
					
						/** Remove goal from list */
						positions.remove(goalIndex);
					}

				}

				if (dispersionInterval != -1)
				{
					waitFor(dispersionInterval,this);
				}
				else
				{
					killAgent();
				}

				return null;
			}
		});
	}
	protected void requestAllPositions()
	{
		getSendPositionService().send(""+getComponentIdentifier(), "request", null);

		getLogger().info(""+getComponentIdentifier()+" requesting all positions");		
	}
	@Override public void agentKilled()
	{
		super.agentKilled();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args =
		{
				new Argument("pingInterval", "Time between pings in ms", "Integer", new Integer(30000)),
				new Argument("dispersionInterval", "Time between dispersions in ms", "Integer", new Integer(60000)),
		};

		return new MicroAgentMetaInfo("This agent starts up a dispersion scenario. Set dispersionInterval to -1 for no repetition.", null, args, null);
	}
}
