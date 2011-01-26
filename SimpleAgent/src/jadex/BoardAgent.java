package jadex;

import data.Board;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

public class BoardAgent extends MicroAgent
{
	Board board;

	@Override
	public void agentCreated()
	{
		board = new Board();
	}
	
	@Override
	public void executeBody()
	{}
	
	@Override
	public void agentKilled() {
		board.clear();
	}
	
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("requires player", "dummy", "Boolean", new Boolean(false)),
				new Argument("player config", "dummy", "String", "/Users/sebastian/robotcolla/SimpleAgent/player/planner2.cfg")};

		return new MicroAgentMetaInfo("This agent starts up a board agent.", null, args, null);
	}

}
