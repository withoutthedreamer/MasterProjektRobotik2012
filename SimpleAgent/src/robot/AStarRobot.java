package robot;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import data.Position;
import de.unihamburg.informatik.tams.project.communication.Barrel;
import de.unihamburg.informatik.tams.project.communication.MapPosition;
import de.unihamburg.informatik.tams.project.communication.RobotMap;
import de.unihamburg.informatik.tams.project.communication.exploration.Grid;
import de.unihamburg.informatik.tams.project.communication.exploration.GridPosition;
import de.unihamburg.informatik.tams.project.communication.exploration.Exploration.RobotState;
import de.unihamburg.informatik.tams.project.communication.network.CommunicationFactory;
import device.Device;
import device.external.IGripperListener;
import device.external.ILocalizeListener;
import device.external.IPlannerListener;

public class AStarRobot extends PatrolRobot {
	private class sortableList {
		// sortableList list:=(v1,f1,v2,f2,...), where v is an int-value and f is a weight-value for positioning the int-value
		private List<Integer> _list = new ArrayList<Integer>();

		public void enqueue(int knoten, int f) {
			if (_list.isEmpty()) {
				_list.add(knoten);
				_list.add(f);
			} else {
				for (int i = 0; i < _list.size(); i = i + 2) {
					if (f < _list.get(i + 1)) {
						_list.add(i, knoten);
						_list.add(i + 1, f);
						i = i + 2;
						break;
					} else// (i == (_list.size()-2))
					{
						_list.add(knoten);
						_list.add(f);
						i = i + 2;
					}
				}
			}
		}

		public boolean contains(int node) {
			for (int i = 0; i < _list.size(); i = i + 2) {
				if (_list.get(i) == node) {
					return true;
				}
			}
			return false;
		}

		public int get(int index) {
			try {
				return _list.get(index * 2);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("ERROR occured: Index not found!");
				return 1000000;
			}
		}

		public int size() {
			return _list.size() / 2;
		}

		public boolean isEmpty() {
			boolean isEmpty = false;
			if (_list.isEmpty()) {
				isEmpty = true;
			}
			return isEmpty;
		}

		public int dequeue(int index) {
			int node = _list.get(index * 2);
			_list.remove(index * 2);
			_list.remove(index * 2);
			return node;
		}

		public void remove(int node) {
			int index = 0;
			for (int i = 0; i < _list.size(); i = i + 2) {
				if (_list.get(i) == node) {
					index = i;
				}
			}
			_list.remove(index);
			_list.remove(index);
		}

		public void clear() {
			_list.clear();
		}
	}

	// Map
	private int _mapMaxX;
	private int _mapMaxY;
	private int _maplengthX;
	private int _maplengthY;
	// Graph
	private int _quantity = 10;
	private int _graphdensity = 2;
	private List<Integer> _locations = new ArrayList<Integer>();
	private int[] _edge = new int[_quantity * _graphdensity];
	private int _target = 0;
	private int _visitedTargets = _quantity;
	private int _startLocation = 0;
	// A*
	private sortableList _openlist = new sortableList();
	private List<Integer> _closedlist = new ArrayList<Integer>();
	private List<Integer> _successor = new ArrayList<Integer>();
	private int[] _g = new int[_quantity];
	private int[] _h = new int[_quantity];
	private int[] _c = new int[_quantity * _graphdensity];
	// Path
	private List<Integer> _way = new ArrayList<Integer>();
	private List<Integer> _path = new ArrayList<Integer>();
	//Running
	Grid _grid;
	MapPosition _mapMaximumPosition;
	int _pathIterator = 0;
	int _timer = 0;

	@Override
	public void doStep() {
		if(ownPosition.equals(new Position(0,0,0))) {
			ownPosition = this.getPosition();
		}

		//If all vectors in the graph are visited, then generate a new graph.
		if (_visitedTargets == _quantity) {
			generateGraph();
			_visitedTargets = 0;
			_pathIterator = 0;
		}

//		if (_timer == 60)
//		{
//			System.out.println("Timer rescue!");
//			_timer = 0;
//			_pathIterator++;
//			state = RobotState.NEEDS_NEW_GOAL;
//		}

		if (state == RobotState.NEEDS_NEW_GOAL) {
			position = new MapPosition((int)ownPosition.getX(), (int)ownPosition.getY());

			planner.addIsDoneListener(new IPlannerListener() {
				@Override
				public void callWhenIsDone() {
					_pathIterator++;
					planner.removeIsDoneListener(this);
					state = RobotState.NEEDS_NEW_GOAL;
				}

				@Override
				public void callWhenAbort() {
					_pathIterator++;
					logger.info("Aborted");
					planner.removeIsDoneListener(this);
					state = RobotState.NEEDS_NEW_GOAL;
				}

				@Override
				public void callWhenNotValid() {
					_pathIterator++;
					logger.info("No valid path");
					planner.removeIsDoneListener(this);
					state = RobotState.NEEDS_NEW_GOAL;
				}
			});
			/**
			 * If the current path has another goal, set the goal, else generate a new path.
			 */
			try {
				Position goalPos = new Position(_path.get(_pathIterator*2), _path.get((_pathIterator*2)+1), 0.00);
				this.setGoal(goalPos);
				state = RobotState.ON_THE_WAY;
				_timer = 0;
				System.out.println("Driving to: "+_path.get(_pathIterator*2)+", "+_path.get((_pathIterator*2)+1));
			}
			catch (IndexOutOfBoundsException e) {
				_pathIterator = 0;
				_startLocation = _target;
				generateTarget();
				aStar();
			}

		}
		_timer++;
		checkForNewBarrels();
	}

	public AStarRobot(Device[] roboDevList) {
		super(roboDevList);
		state = RobotState.NEEDS_NEW_GOAL;
		getGripper().close(new IGripperListener(){
			@Override
			public void whenOpened() {}
			@Override
			public void whenClosed() {
				gripperState = RobotState.GRIPPER_CLOSE;
			}
			@Override
			public void whenLifted() {}
			@Override
			public void whenReleased() {}
			@Override
			public void whenClosedLifted() {}
			@Override
			public void whenReleasedOpened() {}
			@Override
			public void whenError() {}
			});
		planner = getPlanner();
		barrelPositions = Collections.synchronizedList(new ArrayList<double[]>());
		startRobotEyes(barrelPositions);
		knownBarrels = new ArrayList<Barrel>();
		localizer = getLocalizer();
		localizer.addListener(new ILocalizeListener()
        {
            public void newPositionAvailable(Position newPose)
            {
                ownPosition = newPose;
                System.err.println("New position: "+newPose.toString());
            }
        });
		CommunicationFactory cf = new CommunicationFactory();
		try {
			map = cf.getSlaveMap(server, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Map size
		_mapMaximumPosition = map.getMaximumPosition();
		_mapMaxX = (int)_mapMaximumPosition.getxPosition();
		_mapMaxY = (int)_mapMaximumPosition.getyPosition();
		getMapLength();

		_grid = map.getGrid();

		//First Graph
		generateGraph();
		_visitedTargets = 0;
		_pathIterator = 0;
		//First Path
		_pathIterator = 0;
		_startLocation = _target;
		generateTarget();
		aStar();
	}

	/**
	 * Calculating the length of the Map, for 0-point is in the middle of the map.
	 */
	private void getMapLength() {
		_maplengthX = _mapMaxX * 2;
		_maplengthY = _mapMaxY * 2;
	}

	/**
	 * Weightfunction for A*-Algorithm, the euclidian-distance.
	 */
	private int weightFunction(int x1, int y1, int x2, int y2) {
		int distance = 0;
		distance = (int) Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
		return distance;
	}

	/**
	 * Searching for the shortest way in the existing graph, if one exist.
	 */
	private void aStar() {
		System.out.println("aStar wird ausgeführt.");
		int currentNode;

		calculateCosts();
		_openlist.clear();
		_closedlist.clear();
		_way.clear();

		_openlist.enqueue(_startLocation, 0);

		do {
			currentNode = _openlist.dequeue(0);
			if (currentNode == _target) {
				_way.add(_target);
				makePath();
				return;
			}
			expandNode(currentNode);
			_closedlist.add(currentNode);
		} while (!_openlist.isEmpty());
		_way.add(_target);
		makePath();
		return;
	}

	/**
	 * From the current node in the graph, checking all successors for the best.
	 * @param currentNode The current node in the graph.
	 */
	private void expandNode(int currentNode) {
		int tentative_g = 0;
		int c = 0;
		int f = 0;
		getSuccessors(currentNode);
		for (int successor : _successor) {
			if (_closedlist.contains(successor)) {
				continue;
			}
			for (int i = 0; i < _edge.length; i++) {
				if (((i % _quantity) == currentNode) && (_edge[i] == successor)) {
					c = _c[i];
				}
			}
			tentative_g = _g[currentNode] + c;
			if ((_openlist.contains(successor))
					&& (tentative_g >= _g[successor])) {
				continue;
			}
			if (!(_way.contains(currentNode))) {
				_way.add(currentNode);
			}
			_g[successor] = tentative_g;
			f = tentative_g + _h[successor];
			if (_openlist.contains(successor)) {
				_openlist.remove(successor);
				_openlist.enqueue(successor, f);
			} else {
				_openlist.enqueue(successor, f);
			}
		}
	}

	/**
	 * Search all successors for a given node in the existing graph.
	 * @param node
	 */
	private void getSuccessors(int node) {
		_successor.clear();

		for (int i = 0; i < _edge.length; i++) {
			if ((i % _quantity) == node) {
				_successor.add(_edge[i]);
			} else if (_edge[i] == node) {
				_successor.add(i % _quantity);
			}
		}
	}

	/**
	 * Calculating all costs are needed for the A*-Algorithm.
	 */
	private void calculateCosts() {
		// HCosts
		for (int i = 0; i < _quantity - 1; i++) {
			_h[i] = weightFunction(_locations.get(i * 2),
					_locations.get((i * 2) + 1), _locations.get(_target * 2),
					_locations.get((_target * 2) + 1));
		}

		// CCosts
		int index = 0;
		int from, to = 0;
		int from_x, from_y = 0;
		int to_x, to_y = 0;
		for (int i = 0; i < _edge.length; i++) {
			index = i % _quantity;
			from = index;
			to = _edge[i];
			from_x = _locations.get(from * 2);
			from_y = _locations.get((from * 2) + 1);
			to_x = _locations.get(to * 2);
			to_y = _locations.get((to * 2) + 1);
			_c[i] = weightFunction(from_x, from_y, to_x, to_y);
		}

		// GCosts
		for (int i = 0; i < _g.length; i++) {
			_g[i] = 0;
		}
	}

	/**
	 * Generating a path in coordinate view from an existing way (which has nodes).
	 */
	private void makePath() {
		_path.clear();
		for (int i = 0; i < _way.size(); i++) {
			_path.add(_locations.get(_way.get(i) * 2));
			_path.add(_locations.get((_way.get(i) * 2) + 1));
		}
		System.out.println(_path);
	}

	/**
	 * Generating a new Graph by generating random vectors and edges.
	 */
	private void generateGraph() {
		// generates all vectors
		_locations.clear();
		for (int i = 0; i < _quantity; i++) {
			int x = (int) (Math.random() * _maplengthX);
			int y = (int) (Math.random() * _maplengthY);
			x = x - _mapMaxX;
			y = y - _mapMaxY;
			GridPosition gridposition = new GridPosition(x, y);
			if(_grid.isValuePosition(gridposition)) {
				_locations.add(x);
				_locations.add(y);
			} else {
				i--;
			}
		}

		// generate all edges
		for (int i = 0; i < (_quantity * _graphdensity); i++) {
			_edge[i] = ((int) (Math.random() * (_quantity) + 0));
		}
		System.out.println("Graph ist erstellt.");
	}

	/**
	 *Iteration through all vectors in the graph.
	 */
	private void generateTarget() {
		System.out.println("Neues Pfadziel wird erstellt.");
		_target = _visitedTargets;
		_visitedTargets++;
	}

	public void setGrid(Grid grid) {
		// TODO Auto-generated method stub

	}
}
