/**
 * 
 */
package jadex.agent;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.GoalReachedService;
import jadex.service.HelloService;
import jadex.service.ReceiveNewGoalService;
import jadex.service.SendPositionService;
import robot.NavRobot;
import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.external.IDevice;

/**
 * @author sebastian
 *
 */
public class SwarmAgent extends NavAgent
{
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
        Integer robotIdx = (Integer)getArgument("robId");
        
        /** Get the device node */
        deviceNode = new DeviceNode(
            new Host[]
            {
                new Host(host,port),
                new Host(host,port+1)
            },
            new Device[]
            {
                new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,robotIdx),
                new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,robotIdx),
                new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1)
            });
        
        deviceNode.runThreaded();

        robot = new NavRobot(deviceNode.getDeviceListArray());
        robot.setRobotId("r"+robotIdx);
        
        /**
         *  Check if a particular position is set
         */
        Position setPose = new Position(
                (Double)getArgument("X"),
                (Double)getArgument("Y"),
                (Double)getArgument("Angle"));
        
        if ( setPose.equals(new Position(0,0,0)) == false )
            robot.setPosition(setPose);         

        sendHello();
    }
   
    public static MicroAgentMetaInfo getMetaInfo()
    {
        IArgument[] args = {
                new Argument("host", "Robot host", "String", "localhost"),
                new Argument("port", "Robot port", "Integer", new Integer(6665)),
                new Argument("robId", "Robot identifier", "Integer", new Integer(0)),
                new Argument("X", "Meter", "Double", new Double(0.0)),
                new Argument("Y", "Meter", "Double", new Double(0.0)),
                new Argument("Angle", "Degree", "Double", new Double(0.0))
        };
        
        return new MicroAgentMetaInfo("This agent starts up a swarm agent.", null, args, null);
    }
}
