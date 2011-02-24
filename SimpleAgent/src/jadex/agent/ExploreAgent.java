package jadex.agent;

import robot.ExploreRobot;
import data.Position;
import device.DeviceNode;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.HelloService;
import jadex.service.SendPositionService;

public class ExploreAgent extends WallfollowAgent {
	
	@Override public void agentCreated()
	{
	    hs = new HelloService(getExternalAccess());
        ps = new SendPositionService(getExternalAccess());

        addDirectService(hs);
        addDirectService(ps);

        String host = (String)getArgument("host");
        Integer port = (Integer)getArgument("port");
       
        /** Get the device node */
        setDeviceNode( new DeviceNode(new Object[] {host,port, host,port+1}) );
        getDeviceNode().runThreaded();

        setRobot( new ExploreRobot(deviceNode) );
        getRobot().setRobotId((String)getArgument("name"));
       
        /**
         *  Check if a particular position is set
         */
        Position setPose = new Position(
                (Double)getArgument("X"),
                (Double)getArgument("Y"),
                (Double)getArgument("Angle"));
        
        if ( setPose.equals(new Position(0,0,0)) == false )
            getRobot().setPosition(setPose);         
        
        sendHello();
	}
	public static MicroAgentMetaInfo getMetaInfo()
    {
        IArgument[] args = {
                new Argument("host", "Player", "String", "localhost"),
                new Argument("port", "Player", "Integer", new Integer(6665)),
                new Argument("name", "Robot", "String", "r0"),
                new Argument("X", "Meter", "Double", new Double(0.0)),
                new Argument("Y", "Meter", "Double", new Double(0.0)),
                new Argument("Angle", "Degree", "Double", new Double(0.0))
        };
        
        return new MicroAgentMetaInfo("This agent starts up an explore agent.", null, args, null);
    }
}
