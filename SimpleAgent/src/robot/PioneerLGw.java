package robot;

public class PioneerLGw extends PioneerLG {

	public PioneerLGw (String name, int port, int id) throws Exception {
		super(name, port, id);
	}
	public void update() {
		plan();
		execute();
	}
}
