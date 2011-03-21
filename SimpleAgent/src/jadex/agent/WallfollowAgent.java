/**
 * 
 */
package jadex.agent;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import robot.Pioneer;
import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.IDevice;
import device.ILocalizeListener;
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

/**
 * @author sebastian
 *
 */
public class WallfollowAgent extends MicroAgent
{
    /** Logging support */
    Logger logger = Logger.getLogger (WallfollowAgent.class.getName ());
    
    /** Services */
    HelloService hs;
    SendPositionService ps;

    DeviceNode deviceNode;
    Pioneer robot;

    @Override public void agentCreated()
    {
        hs = new HelloService(getExternalAccess());
        ps = new SendPositionService(getExternalAccess());

        addDirectService(hs);
        addDirectService(ps);
        
        String host = (String)getArgument("host");
        Integer port = (Integer)getArgument("port");
        Integer robotIdx = (Integer)getArgument("index");
        Integer devIdx = (Integer)getArgument("devIndex");
        Boolean hasLaser = (Boolean)getArgument("laser");
        Boolean hasLocalize = (Boolean)getArgument("localize");

        /** Device list */
        CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
        devList.add( new Device(IDevice.DEVICE_POSITION2D_CODE,host,port,devIdx) );
        devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,devIdx) );
        devList.add( new Device(IDevice.DEVICE_SONAR_CODE,host,port,devIdx));
        devList.add( new Device(IDevice.DEVICE_SIMULATION_CODE,host,port,-1) );

        if (hasLocalize == true)
        {
            devList.add( new Device(IDevice.DEVICE_PLANNER_CODE,host,port+1,devIdx) );
            devList.add( new Device(IDevice.DEVICE_LOCALIZE_CODE,host,port+1,devIdx) );
        }

        /** Optional laser ranger */
        if (hasLaser == true)
            devList.add( new Device(IDevice.DEVICE_RANGER_CODE,host,port,devIdx+1));

        /** Host list */
        CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
        hostList.add(new Host(host,port));

        /** Optional planner device */
        if (hasLocalize == true)
            hostList.add(new Host(host,port+1));

        /** Get the device node */
        setDeviceNode( new DeviceNode(hostList.toArray(new Host[hostList.size()]), devList.toArray(new Device[devList.size()])));
        getDeviceNode().runThreaded();

        setRobot( new Pioneer(getDeviceNode().getDeviceListArray()) );
        getRobot().setRobotId("r"+robotIdx);

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

    void sendHello()
    {
        hs.send(""+getComponentIdentifier(), ""+getRobot().getRobotId(), getRobot().getClass().getName());
    }

    void sendPosition(Position newPose)
    {
        if (newPose != null)
        {
            ps.send(""+getComponentIdentifier(), ""+getRobot().getRobotId(), newPose);
        }
    }

    @Override public void executeBody()
    {
        /**
         *  Register localizer callback
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                if (robot.getLocalizer() != null) /** Does it have a localizer? */
                {
                    robot.getLocalizer().addListener(new ILocalizeListener()
                    {
                        @Override public void newPositionAvailable(Position newPose)
                        {
                            sendPosition(newPose);
                        }
                    });
                }
                else
                {
                    /**
                     * Read position periodically
                     */
                    final IComponentStep step = new IComponentStep()
                    {
                        public Object execute(IInternalAccess ia)
                        {
                            Position curPose = robot.getPosition();
                            sendPosition(curPose);
                            logger.finest("Sending new pose "+curPose+" for "+robot);
                            waitFor(1000,this);
                            return null;
                        }
                    };
                    waitForTick(step);
                }
                return null;
            }
        });

        /**
         *  Register to HelloService
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                getHelloService().addChangeListener(new IChangeListener()
                {
                    public void changeOccurred(ChangeEvent event)
                    {
                        Object[] content = (Object[])event.getValue();
                        StringBuffer buf = new StringBuffer();
                        buf.append("[").append(content[0].toString()).append("]: ").append(content[1].toString()).append(" ").append(content[2].toString());

                        /** Check for reply request */
                        if (((String)content[2]).equalsIgnoreCase("ping"))
                        {
                            sendHello();
                        }
                    }
                });
                return null;
            }
        });

        /**
         *  Register to Position update service
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                getSendPositionService().addChangeListener(new IChangeListener()
                {
                    public void changeOccurred(ChangeEvent event)
                    {
                        Object[] content = (Object[])event.getValue();

                        /** Sending position on request */
                        if (((String)content[1]).equals("request"))
                        {
                            sendPosition(robot.getPosition());
                        }
                    }
                });
                return null;
            }
        });

        /**
         * Init wall following
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                robot.setWallfollow();
                robot.runThreaded();
                return null;
            }
        });
    }

    @Override public void agentKilled()
    {    
        robot.stop();
        robot.shutdown();
        deviceNode.shutdown();

        hs.send(getComponentIdentifier().toString(), robot.getRobotId(), "Bye");
    }

    public HelloService getHelloService() { return hs; }
    public SendPositionService getSendPositionService() { return ps; }

    public static MicroAgentMetaInfo getMetaInfo()
    {
        IArgument[] args = {
                new Argument("host", "Player", "String", "localhost"),
                new Argument("port", "Player", "Integer", new Integer(6665)),
                new Argument("index", "Robot index", "Integer", new Integer(0)),
                new Argument("devIndex", "Device index", "Integer", new Integer(0)),
                new Argument("X", "Meter", "Double", new Double(0.0)),
                new Argument("Y", "Meter", "Double", new Double(0.0)),
                new Argument("Angle", "Degree", "Double", new Double(0.0)),
                new Argument("laser", "Laser ranger", "Boolean", new Boolean(true)),
                new Argument("localize", "Localize device", "Boolean", new Boolean(true))
        };

        return new MicroAgentMetaInfo("This agent starts up a wallfollow agent.", null, args, null);
    }

    /**
     * @return the robot
     */
    protected Pioneer getRobot() {
        return robot;
    }

    /**
     * @return the deviceNode
     */
    protected DeviceNode getDeviceNode() {
        return deviceNode;
    }

    /**
     * @param deviceNode the deviceNode to set
     */
    protected void setDeviceNode(DeviceNode deviceNode) {
        this.deviceNode = deviceNode;
    }

    /**
     * @param robot the robot to set
     */
    protected void setRobot(Pioneer robot) {
        this.robot = robot;
    }

    /**
     * @return the logger
     */
    @Override public Logger getLogger() {
        return logger;
    }
}
