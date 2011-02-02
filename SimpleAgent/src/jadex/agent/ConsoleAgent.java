package jadex.agent;

import jadex.IMessageService;
import jadex.MessagePanel;
import jadex.MessageService;
import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import javax.swing.SwingUtilities;

/**
 *  Message micro agent. 
 */
public class ConsoleAgent extends MicroAgent
{
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
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				MessagePanel.createGui((IMicroExternalAccess)getExternalAccess());
			}
		});
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
		return new MicroAgentMetaInfo("This agent offers a helpline for getting information about missing persons.", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
			null, new Class[]{IMessageService.class});
	}

}
