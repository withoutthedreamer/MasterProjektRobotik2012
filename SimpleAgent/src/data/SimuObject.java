package data;

public class SimuObject {
	protected Trackable obj = null;
	protected String    id  = "";
//	protected Position  pos = null;
	
	public SimuObject (String id, Trackable obj) {
		this.id = id;
		this.obj = obj;
	}
	public String getId () {
		return this.id;
	}
	public Trackable getObject () {
		return this.obj;
	}
//	public Position getPosition () {
//		return this.pos;
//	}
//	public void setPosition (Position pos) {
//		this.pos = pos;
//	}
}
