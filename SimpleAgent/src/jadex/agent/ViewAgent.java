package jadex.agent;

import java.util.concurrent.CopyOnWriteArrayList;

import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.Simulation;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.HelloService;
import jadex.service.SendPositionService;

public class ViewAgent extends MicroAgent
{
	/** Services */
	HelloService hs;
	SendPositionService ps;
	
	/** API to the simulator (gui) */
	protected Simulation simu = null;
	protected DeviceNode deviceNode = null;
	
	@Override public void agentCreated()
	{
		hs = new HelloService(getExternalAccess());
		ps = new SendPositionService(getExternalAccess());

		addDirectService(hs);
		addDirectService(ps);

		String host = (String)getArgument("host");
        Integer port = (Integer)getArgument("port");

		/** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1) );

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));
        
        /** Get the device node */
        deviceNode = new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()]));
		deviceNode.runThreaded();
		
		hs.send(""+getComponentIdentifier(), "", "Hello");
	}

	@Override public void executeBody()
	{
		scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				getSendPositionService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();

						simu.setPositionOf((String)content[1], (Position)content[2]);
//						System.err.println("View receive: "+(String)content[1]+(Position)content[2]);
					}
				});
				return null;
			}
		});

		waitFor(200, new IComponentStep()
		{
			public Object execute(IInternalAccess args)
			{
				simu = (Simulation) deviceNode.getDevice(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
				simu.initPositionOf("r0");
				simu.initPositionOf("r1");
				return null;
			}
		});
	}
	@Override public void agentKilled() {
		deviceNode.shutdown();
		hs.send(getComponentIdentifier().toString(), "", "Bye");
	}
	public static MicroAgentMetaInfo getMetaInfo()
	{
		IArgument[] args = {
                new Argument("host", "Player", "String", "localhost"),
				new Argument("port", "Player", "Integer", new Integer(6600)),
                new Argument("robot", "Only track this", "Integer", new Integer(-1))
		};
		
		return new MicroAgentMetaInfo("This agent starts up a view agent.", null, args, null);
	}

	public HelloService getHelloService() { return hs; }
	public SendPositionService getSendPositionService() { return ps; }
}
