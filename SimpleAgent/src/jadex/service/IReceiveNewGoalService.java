package jadex.service;

import jadex.commons.service.IService;


/**
 *  Service can receive chat messages.
 */
public interface IReceiveNewGoalService extends IService
{
	/**
	 *  Hear something.
	 *  @param name The name of the sender.
	 *  @param robotName The robot name.
	 *  @param content The new goal.
	 */
	public void receive(String name, String robotName, Object content);
		
}
