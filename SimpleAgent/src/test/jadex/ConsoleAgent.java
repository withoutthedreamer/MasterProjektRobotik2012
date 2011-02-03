package test.jadex;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.HelloService;
import jadex.service.IMessageService;
import jadex.service.MessagePanel;
import jadex.service.MessageService;
import jadex.service.ReceiveNewGoalService;
import jadex.service.SendPositionService;

import javax.swing.SwingUtilities;

/**
 *  Message micro agent. 
 */
public class ConsoleAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The message service. */
	protected MessageService ms;
	/* Services */
	HelloService hs = null;
	SendPositionService ps = null;
	ReceiveNewGoalService gs = null;


	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		ms = new MessageService(getExternalAccess());
		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());

		assert(hs != null && gs != null && ps != null && ms != null);
		
		addDirectService(ms);
		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		
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
	public MessageService getMessageService() { return ms; 	}
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	
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
