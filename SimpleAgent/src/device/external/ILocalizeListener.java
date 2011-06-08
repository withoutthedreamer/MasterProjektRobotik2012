package device.external;

import data.Position;

public interface ILocalizeListener {

	public void newPositionAvailable(Position newPose);
}
