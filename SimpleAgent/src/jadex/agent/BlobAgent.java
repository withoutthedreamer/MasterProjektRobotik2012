/**
 * 
 */
package jadex.agent;

import java.util.concurrent.CopyOnWriteArrayList;

import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.Simulation;
import jadex.bridge.*;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.ReceiveNewGoalService;

/**
 * @author sebastian
 *
 */
public class BlobAgent extends MicroAgent
{
	ReceiveNewGoalService gs;
	Position blobPose;
    DeviceNode dn;
    Simulation simu;

	@Override public void agentCreated()
	{
        gs = new ReceiveNewGoalService(getExternalAccess());
        addDirectService(gs);
        blobPose = new Position(
        		(Double)getArgument("X"),
        		(Double)getArgument("Y"),
        		0
        );

        String host = (String)getArgument("host");
        Integer port = (Integer)getArgument("port");

        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,null,-1,-1) );


        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
       
        /** Get the device node */
        dn = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
        dn.runThreaded();
        
		simu = (Simulation) dn.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));

	}

	@Override public void executeBody()
	{
        getReceiveNewGoalService().send(""+getComponentIdentifier(), "collectGoal", blobPose);
        if (simu != null)
        	simu.setPositionOf((String)getArgument("blob"), blobPose);
       
        waitFor(200, new IComponentStep()
		{
			public Object execute(IInternalAccess args)
			{
		        killAgent();
				return null;
			}
		});

	}
	@Override public void agentKilled()
	{
		dn.shutdown();
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
				new Argument("host", "Player", "String", "localhost"),
				new Argument("port", "Player", "Integer", new Integer(6665)),
				new Argument("X", "Meter", "Double", new Double(0.0)),
				new Argument("Y", "Meter", "Double", new Double(0.0)),
				new Argument("blob", "color", "String", new String("green"))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a blob agent.", null, args, null);
	}
	public ReceiveNewGoalService getReceiveNewGoalService() { return gs; }
}
