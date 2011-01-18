package robot;

public class PioneerLGw extends GripperRobot {

	public PioneerLGw (String name, int port, int id) {
		super(name, port, id);
	}
	public void update() {
		plan();
		execute();
	}
}
