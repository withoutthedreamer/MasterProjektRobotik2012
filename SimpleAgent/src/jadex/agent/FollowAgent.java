/**
 * 
 */
package jadex.agent;

import data.Position;
import device.ILocalizeListener;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgentMetaInfo;

/**
 * @author sebastian
 *
 */
public class FollowAgent extends NavAgent
{
    Position followPose;
    Position robotPose;
    boolean isNewFollowPose;

    /**
     * @see jadex.agent.NavAgent#agentCreated()
     */
    @Override public void agentCreated()
    {
        super.agentCreated();
       
        isNewFollowPose = false;
        followPose = getRobot().getPosition();
        robotPose = getRobot().getPosition();
    }

    /**
     * @see jadex.agent.NavAgent#executeBody()
     */
    @Override public void executeBody()
    {
        super.executeBody();

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
                        if (((String)content[1]).equals( "r"+(Integer)getArgument("robot") ))
                        {
                            Position curPose = (Position) content[2];
                            
                            /** Check for new position */
                            if (followPose.equals(curPose) == false) {
                                followPose = curPose;
                                isNewFollowPose = true;
                            }
                        }
                    }
                });
                return null;
            }
        });
        /**
         *  Register localizer callback
         */
        scheduleStep(new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                if (getRobot().getLocalizer() != null) /** Does it have a localizer? */
                {
                    getRobot().getLocalizer().addListener(new ILocalizeListener()
                    {
                        @Override public void newPositionAvailable(Position newPose)
                        {
                            robotPose = newPose;
                        }
                    });
                }
                return null;
            }
        });
        /**
         * Update goal periodically
         */
        final IComponentStep step = new IComponentStep()
        {
            public Object execute(IInternalAccess ia)
            {
                updateGoal();
                waitFor((Integer)getArgument("updateInterval"),this);
                return null;
            }
        };
        waitForTick(step);
    }
   
    public void updateGoal()
    {
        /** Check for distance to goal */
        if (robotPose.distanceTo( followPose ) >= (Double)getArgument("minDistance")) {
            /** Check for new goal */
            if (isNewFollowPose == true) {
                isNewFollowPose = false;
                getRobot().setGoal( followPose );
            }
        } else
            getRobot().stop();
    }

    /**
     * @see jadex.agent.NavAgent#agentKilled()
     */
    @Override public void agentKilled() {
        super.agentKilled();
    }

    public static MicroAgentMetaInfo getMetaInfo()
    {
        IArgument[] args = {
                new Argument("robot", "To follow", "Integer", new Integer(0)),
                new Argument("host", "Player", "String", "localhost"),
                new Argument("port", "Player", "Integer", new Integer(6667)),
                new Argument("index", "Robot index", "Integer", new Integer(1)),
                new Argument("X", "Meter", "Double", new Double(0.0)),
                new Argument("Y", "Meter", "Double", new Double(0.0)),
                new Argument("Angle", "Degree", "Double", new Double(0.0)),
                new Argument("laser", "Laser ranger", "Boolean", new Boolean(true)),
                new Argument("minDistance", "Meter", "Double", new Double(2.0)),
                new Argument("updateInterval", "ms", "Integer", new Integer(5000))
        };

        return new MicroAgentMetaInfo("This agent starts up a follow agent.", null, args, null);
    }

    /**
     * @return the followPose
     */
    synchronized Position getFollowPose() {
        assert(followPose != null);
        return followPose;
    }

    /**
     * @param newFollowPose the followPose to set
     */
    synchronized void setFollowPose(Position newFollowPose) {
        assert(newFollowPose != null);
        followPose.setPosition( newFollowPose );
    }
}
