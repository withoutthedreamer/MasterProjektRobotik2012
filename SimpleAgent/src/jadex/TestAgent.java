package jadex;

import javax.swing.SwingUtilities;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

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
		ms.tell("TestAgent", "msg from testagent");
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
		return new MicroAgentMetaInfo("This agent sends a test message", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
			null, new Class[]{IMessageService.class});
	}

}
