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
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;

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
		 * Request all robot agents.
		 * Do it periodically.
		 */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getBoard().clear();
				pingAllAgents();
				
				waitFor(10000,this);
				return null;
			}
		});
		
		/** Assign random goal positions */
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getLogger().info("Checking agents on network");
				ArrayList<Integer> positions = new ArrayList<Integer>();
			
				/**
				 *  Create a sorted number array
				 */
				for (int i=0; i<dispersionPoints.length; i++)
					positions.add(i);
				
				Collections.shuffle(positions);
				getLogger().info("Shuffle positions: "+positions);

				/**
				 *  Loop through dispersion points
				 */
				Iterator<Entry<String, BoardObject>> it = getBoard().getSet().iterator();
				for (int i=0; i<positions.size(); i++) {
					if (it.hasNext()) {
						String key = it.next().getKey();
						
						/** Check if it is a navigation agent */
						if ( getBoard().getObject(key).getName().equals(Robot.class.getName()) )
						{
							getReceiveNewGoalService().send(""+getComponentIdentifier(), key, dispersionPoints[positions.get(i)]);
							
							getLogger().info("Sending goal: "+dispersionPoints[positions.get(i)]+" to "+key);
						}
					}
				}
				waitFor(30000,this);
				return null;
			}
		});
	}
	@Override public void agentKilled() {
		super.agentKilled();
	}
}
