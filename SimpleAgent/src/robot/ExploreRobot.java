package robot;

import java.util.Vector;

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
		if (this.bloFi == null ) { return; }
		int count = this.bloFi.getCount();
		if (count > 0) {
			for (int i=0; i<count; i++) {
				Vector<BlobfinderBlob> blobs = bloFi.getBlobs(); 
				if (blobs.capacity() > i) {
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

	protected void plan () {
		double tmp_turnrate = 0.;

		blobsearch();

		// (Left) Wall following
		this.turnrate = wallfollow();
		// Collision avoidance overrides other turnrate if neccessary!
		// May change this.turnrate or this.currentState
		this.turnrate = collisionAvoid();

		// Set speed dependend on the wall distance
		this.speed = calcspeed();

		// Check if rotating is safe
		// tune turnrate controlling here
		tmp_turnrate = checkrotate();

		// Fusion of the vectors makes a smoother trajectory
		//		this.turnrate = (tmp_turnrate + this.turnrate) / 2;
		double weight = 0.5;
		this.turnrate = weight*tmp_turnrate + (1-weight)*this.turnrate;
	}
	
	public void setBlackboard (Blackboard bb)
	{
		blackboard = bb;
	}
}
