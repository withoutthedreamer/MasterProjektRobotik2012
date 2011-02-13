package device;

import data.Position;

public interface ILocalizeListener {

	public void newPositionAvailable(Position newPose);
}
