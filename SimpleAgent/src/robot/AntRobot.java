package robot;
import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.State;
import de.unihamburg.informatik.tams.project.communication.exploration.Exploration;
import de.unihamburg.informatik.tams.project.communication.exploration.Grid;
import de.unihamburg.informatik.tams.project.communication.exploration.GridPosition;
import device.Device;

public class AntRobot extends NavRobot implements Exploration {

	private data.Position ownPosition = this.getPosition();
	
	private Grid grid;
	private MapPosition position = 
			new MapPosition((int)ownPosition.getX(), (int)ownPosition.getY());
	
	GridPosition gpos = grid.getOwnPosition(position);

	GridPosition north = new GridPosition(gpos.getxPosition()-1, gpos.getyPosition());
	GridPosition  west = new GridPosition(gpos.getxPosition(), gpos.getyPosition()-1);
	GridPosition south = new GridPosition(gpos.getxPosition()+1, gpos.getyPosition());
	GridPosition  east = new GridPosition(gpos.getxPosition(), gpos.getyPosition()+1);

	GridPosition goal = choose();
	
	
	
	public AntRobot(Device[] roboDevList) {
		super(roboDevList);
		// TODO Auto-generated constructor stub
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	private GridPosition choose() {
		// TODO algorithm
		return north;
	}

	@Override
	public boolean hasGripper() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapPosition getMapPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public void transportBarrelTo(MapPosition currentPositionOfBarrel,
			MapPosition targetPositionOfBarrel) {
		// TODO Auto-generated method stub
		
	}
}
