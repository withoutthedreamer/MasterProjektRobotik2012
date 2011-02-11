package jadex.agent;

import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.service.GoalReachedService;
import jadex.service.HelloService;
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
	MessageService ms;
	/** Other services */
	HelloService hs;
	SendPositionService ps;
	ReceiveNewGoalService gs;
	GoalReachedService gr;

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
		gr = new GoalReachedService(getExternalAccess());

		addDirectService(ms);
		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		addDirectService(gr);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				MessagePanel.createGui((IMicroExternalAccess)getExternalAccess());
			}
		});
	}
	/**
	 *  Get the services.
	 */
	public MessageService getMessageService() { return ms; 	}
	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
	public GoalReachedService getGoalReachedService() { return gr; }
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent offers a helpline for getting information about missing persons.", null, 
//			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
//			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
//			null, new Class[]{IMessageService.class});
//	}

}
