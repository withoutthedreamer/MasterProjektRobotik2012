package robot;

import java.util.concurrent.CopyOnWriteArrayList;

import data.BbNote;
import data.BlobfinderBlob;
import device.Blackboard;
import device.Device;

public class ExploreRobot extends Pioneer {
	
	protected Blackboard blackboard = null;

	public ExploreRobot(Device roboDevices)
	{
		super(roboDevices);
	}
	protected final void blobsearch() {
		if (getBloFi() == null ) { return; }
		int count = getBloFi().getCount();
		if (count > 0) {
			for (int i=0; i<count; i++) {
				CopyOnWriteArrayList<BlobfinderBlob> blobs = getBloFi().getBlobs(); 
//				if (blobs.capacity() > i) {
				if (blobs.size() > i) {
					BlobfinderBlob ablob = blobs.get(i);
					// Seen from this position
					ablob.setDiscovered(this.getPosition());
					BbNote note = new BbNote();
					note.setGoal(this.getPosition());
					note.setPose(this.getPosition());
					//				note.setTrackable(tracked2);
					if (blackboard != null) {
						blackboard.add(BlobfinderBlob.getColorString(ablob.getColor()), note);
					} else {
						System.out.print(this.toString() + " @ " + this.getPosition().toString());
						System.out.println(" found blob @ " + ablob.toString());
					}
				}
			}
		}
	}

	@Override protected double planLeftWallfollow ()
	{
		double newTurnrate = 0.;
        double newTurnrate2 = 0.;

		blobsearch();

		// (Left) Wall following
		newTurnrate = wallfollow();
		// Collision avoidance overrides other turnrate if neccessary!
		// May change this.turnrate or this.currentState
		newTurnrate = collisionAvoid(newTurnrate);

		// Set speed dependend on the wall distance
//		setSpeed( calcspeed(speed) );

		// Check if rotating is safe
		// tune turnrate controlling here
		newTurnrate2 = checkrotate(newTurnrate);

		// Fusion of the vectors makes a smoother trajectory
		//		this.turnrate = (tmp_turnrate + this.turnrate) / 2;
		double weight = 0.5;
		return weight*newTurnrate + (1-weight)*newTurnrate2;
	}
	
	public void setBlackboard (Blackboard bb)
	{
		blackboard = bb;
	}
}
