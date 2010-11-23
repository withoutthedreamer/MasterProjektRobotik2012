package robot;

import data.BbNote;
import data.BlobfinderBlob;
import data.Position;
import device.Blackboard;
import device.Blobfinder;
import device.Sonar;
import javaclient3.PlayerException;

final public class PioneerSB extends Pioneer {
	protected Blobfinder blofi = null;
	protected Blackboard blackboard = null;

	public PioneerSB(String name, int port, int id) {
		super(name, port, id);
		try {
			this.sonar = new Sonar (this.playerclient, this.id);
			this.blofi = new Blobfinder(this.playerclient, this.id);
		} catch (PlayerException e) {
			System.err.println ("PioneerSB: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
		this.playerclient.runThreaded (-1, -1);
	}
	public void shutdownDevices () {
		this.sonar.thread.interrupt();
		while(this.sonar.thread.isAlive());
		this.blofi.thread.interrupt();
		while(this.blofi.thread.isAlive());
	}
	protected final void blobsearch() {
		if (this.blofi == null ) { return; }
		int count = this.blofi.getCount();
		if (count > 0) {
			for (int i=0; i<count; i++) {
				BlobfinderBlob ablob = blofi.getBlobs().get(i);
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
	@Override
	public void setGoal(Position goal) {
		// TODO Auto-generated method stub
		
	}
	public void setBlackboard (Blackboard bb) {
		this.blackboard = bb;
	}
	@Override
	public Position getGoal() {
		// TODO Auto-generated method stub
		return null;
	}
}
