package data;

import robot.external.IRobot;

public class SimuObject {
	protected IRobot obj = null;
	protected String    id  = "";
//	protected Position  pos = null;
	
	public SimuObject (String id, IRobot obj) {
		this.id = id;
		this.obj = obj;
	}
	public String getId () {
		return this.id;
	}
	public IRobot getObject () {
		return this.obj;
	}
//	public Position getPosition () {
//		return this.pos;
//	}
//	public void setPosition (Position pos) {
//		this.pos = pos;
//	}
}
