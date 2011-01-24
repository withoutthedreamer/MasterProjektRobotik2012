package robot;

import data.Position;

/**
 * Abstract interface for any robot.
 * @author sebastian
 *
 */
public interface IRobot
{	
	public void setPosition (Position newPosition);
	public Position getPosition ();
	public void setGoal (Position newGoal);
	public Position getGoal();	
}
