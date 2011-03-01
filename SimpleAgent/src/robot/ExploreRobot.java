package robot;

import java.util.logging.Logger;

import device.Blackboard;
import device.Device;

public class ExploreRobot extends Pioneer
{
	/** Logging support */
    Logger logger = Logger.getLogger (ExploreRobot.class.getName ());

	protected Blackboard blackboard = null;

	public ExploreRobot(Device roboDevices)
	{
		super(roboDevices);
		// TODO add behaviour blobsearching
	}
	
//	@Override protected void update()
//    {
//        debugSensorData();
//        
//        StateType curState = getCurrentState();
//        
//        switch (curState)
//        {
//            case WALL_SEARCHING :
//    
//            case LWALL_FOLLOWING :
//          
//            case COLLISION_AVOIDANCE :
//                updateSpeed( MAXSPEED );
//                updateTurnrate( planLeftWallfollow() );
//                updatePosi();
////                blobsearch();
//                
//                break;
//           
//            case SET_SPEED :
//                updateSpeed(getSpeed());
//                updateTurnrate(getTurnrate());
//                updatePosi();
//                break;
//
//            default :
//                updateStop();
//                break;
//        }
//    }
	
//	final void blobsearch()
//	{
//	    if (getBloFi() != null )
//	    {
//	        /** Check how many different blobs are currently seen by the device */
//	        int count = getBloFi().getCount();
//	        
//	        if (count > 0)
//	        {
//	            for (int i=0; i<count; i++)
//	            {
//	                /** Get the current blobs */
//	                CopyOnWriteArrayList<BlobfinderBlob> blobs = getBloFi().getBlobs(); 
//
//	                if (blobs.size() > i)
//	                {
//	                    /** Device knows more blobs than currently seen. */
//	                    BlobfinderBlob ablob = blobs.get(i);
//	                    
//	                    // Seen from this position TODO more exactly
//	                    Position blobPose = getPosition();
//	                    ablob.setDiscovered( blobPose );
//	                    BbNote note = new BbNote();
//	                    note.setGoal( blobPose );
//	                    note.setPose( blobPose );
//
//	                    if (blackboard != null) {
//	                        blackboard.add(BlobfinderBlob.getColorString(ablob.getColor()), note);
//	                    } else {
//	                       logger.fine(""+blobPose);
//	                       logger.fine(""+ablob);
//	                    }
//	                }
//	            }
//	        }
//	    }
//	}
	
	public void setBlackboard (Blackboard bb)
	{
		blackboard = bb;
	}
}
