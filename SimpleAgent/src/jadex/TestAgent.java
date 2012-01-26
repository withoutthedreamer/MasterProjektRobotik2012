package jadex;

import java.lang.management.ManagementFactory;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.MessageService;

public class TestAgent extends MicroAgent {

//-------- attributes --------
	
	/** The message service. */
	protected MessageService ms;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		ms = new MessageService(getExternalAccess());
		addDirectService(ms);
//		ms.tell("TestAgent", "msg from testagent");
		ms.tell(ManagementFactory.getRuntimeMXBean().getName(), "msg from testagent");
	}
	
	/**
	 *  Get the chat service.
	 */
	public MessageService getMessageService()
	{
		return ms;
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts up the Explorer agent.", 
				null, new IArgument[]{
				new Argument("player path", "This parameter is the argument given to the agent.", "String", 
						"test"),	
			}, null);
	}

}
