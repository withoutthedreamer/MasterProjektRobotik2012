package robot;

import device.Ranger;

public class PioneerRso extends Pioneer {

	public PioneerRso(String name, int port, int id) throws IllegalStateException {

		super(name, port, id);

		sonar = new Ranger(roboClient, this.id, 0);
	}

	protected void shutdownDevices () {
		super.shutdownDevices();
		sonar.thread.interrupt();
		while (sonar.thread.isAlive());
	}
}
