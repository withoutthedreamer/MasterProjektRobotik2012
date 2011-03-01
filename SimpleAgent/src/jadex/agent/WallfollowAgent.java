/**
 * 
 */
package jadex.agent;

import robot.Pioneer;
import robot.Robot;
import data.Position;
import device.DeviceNode;
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
           
            /** Get the device node */
            deviceNode = new DeviceNode(new Object[] {host,port, host,port+1});
            deviceNode.runThreaded();

            robot = new Pioneer(deviceNode);
            robot.setRobotId((String)getArgument("name"));
           
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
        
        void sendHello() {
            hs.send(""+getComponentIdentifier(), robot.getRobotId(), Robot.class.getName());
        }

        void sendPosition(Position newPose) {
            ps.send(getComponentIdentifier().toString(), robot.getRobotId(), newPose);
        }
        
        @Override public void executeBody()
        {
            /** Agent is worthless if underlying robot or devices fail */
            if (robot == null || deviceNode == null) {
                killAgent();
            }
            
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
                            if (((String)content[1]).equals("request")) {
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
                    new Argument("name", "Robot", "String", "r0"),
                    new Argument("X", "Meter", "Double", new Double(0.0)),
                    new Argument("Y", "Meter", "Double", new Double(0.0)),
                    new Argument("Angle", "Degree", "Double", new Double(0.0))
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
}
