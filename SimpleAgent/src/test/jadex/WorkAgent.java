package test.jadex;

import jadex.base.fipa.SFipa;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.micro.MicroAgent;

import java.util.Map;

import data.Position;

/**
 *  Answer ping requests. 
 */
public class WorkAgent extends MicroAgent
{
	/**
	 *  Send a reply to the sender.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	@SuppressWarnings("rawtypes")
	public void messageArrived(Map msg, final MessageType mt)
	{
		String perf = (String)msg.get(SFipa.PERFORMATIVE);
		final Position posi = new Position(1,1,0);
		Position content = (Position) msg.get(SFipa.CONTENT);
		
		System.err.println("Received: " + content.toString());
		
		if((SFipa.REQUEST.equals(perf) 
			&& msg.get(SFipa.CONTENT).getClass() == Position.class) )
		{
			createReply(msg, mt).addResultListener(createResultListener(new DefaultResultListener()
			{
				@SuppressWarnings("unchecked")
				public void resultAvailable(Object source, Object result)
				{
					Map reply = (Map)result;
					
					reply.put(SFipa.CONTENT, posi);
					reply.put(SFipa.PERFORMATIVE, SFipa.CONFIRM);
					reply.put(SFipa.SENDER, getComponentIdentifier());

					sendMessage(reply, mt);
				}
			}));
		}
		else
		{
			getLogger().severe("Could not process message: "+msg);
		}
	}
}
