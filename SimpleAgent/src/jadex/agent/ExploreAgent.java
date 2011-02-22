package jadex.agent;

import data.Position;
import device.DeviceNode;
import jadex.service.GoalReachedService;
import jadex.service.HelloService;
import jadex.service.ReceiveNewGoalService;
import jadex.service.SendPositionService;
import robot.*;

public class ExploreAgent extends NavAgent {
	
	@Override public void agentCreated()
	{
		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());
		gs = new ReceiveNewGoalService(getExternalAccess());
		gr = new GoalReachedService(getExternalAccess());

		addDirectService(hs);
		addDirectService(ps);
		addDirectService(gs);
		addDirectService(gr);

		String host = (String)getArgument("host");
		Integer port = (Integer)getArgument("port");
		/** Get the device node */
		setDeviceNode( new DeviceNode(new Object[] {host,port, host,port+1}) );
		getDeviceNode().runThreaded();

		setRobot( new ExploreRobot(getDeviceNode()) );
		getRobot().runThreaded();
		
		getRobot().setRobotId((String)getArgument("name"));
		getRobot().setPosition(new Position((Double)getArgument("X"), (Double)getArgument("Y"), Math.toRadians((Double)getArgument("Angle"))));
		
		sendHello();
	}
	
	@Override public void executeBody()
	{
		super.executeBody();
        getRobot().setCurrentState(IPioneer.StateType.LWALL_FOLLOWING);

	}
	@Override public void agentKilled()
	{
		getRobot().stop();
		super.agentKilled();
	}
}
