package jadex.service;

import jadex.commons.service.IService;


/**
 *  Service can receive chat messages.
 */
public interface IHelloService extends IService
{
	/**
	 *  Hear something.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	public void receive(String name, String robotName, Object content);
		
}
