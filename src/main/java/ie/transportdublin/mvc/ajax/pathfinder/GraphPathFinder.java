package ie.transportdublin.mvc.ajax.pathfinder;

import ie.transportdublin.datastructure.neo4j.RelationshipTypes;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.datastructure.neo4j.Waypoint;
import ie.transportdublin.mvc.ajax.routeplanner.RoutePlanner;

import org.joda.time.DateTime;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.Traversal;



/**
 * Finds the shortest path using the neo4j A* algorigthm
 *
 */
public class GraphPathFinder {

	private static final EstimateEvaluator<Double> estimateEval = CommonEvaluators
			.geoEstimateEvaluator(Waypoint.LATITUDE, Waypoint.LONGITUDE);

	private static final CostEvaluator<Double> costEval = CommonEvaluators
			.doubleCostEvaluator(Waypoint.COST);
	private GraphDatabaseService graphDbService;
	private IndexManager indexService;
	public GraphPathFinder() {
		super();
	}

	public GraphPathFinder(GraphDatabaseService graphDbService,
			IndexManager indexService) {
		this.graphDbService = graphDbService;
		this.indexService = indexService;
	}

	/**
	 * Sets up the start and end nodes to find the shortest path
	 * 
	 * @param routePlanner
	 * @return WeightedPath containing the shortest path of nodes
	 */
	public WeightedPath setupStartAndEndPoints(RoutePlanner routePlanner) {

		Expander relExpander = Traversal.expanderForTypes(
				RelationshipTypes.BUS, Direction.OUTGOING);
		PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(relExpander,
				costEval, estimateEval);
		SetupGraph setupGraph = new SetupGraph(graphDbService, indexService);

		Transaction tx = graphDbService.beginTx();
		try {


			DateTime startTime = setupGraph.setupDateTime("09:00:00");
			StopTime start = setupGraph.setupStartWaypoint(routePlanner
					.getLat1(), routePlanner.getLng1(),
					"TODO: Geocoded address", 1, startTime);
			StopTime end = setupGraph.setupEndWaypoint(routePlanner.getLat2(),
					routePlanner.getLng2(), 1, startTime);

			tx.success();
			tx.finish();
			tx = graphDbService.beginTx();

			long startTimer = System.currentTimeMillis();
			WeightedPath path = astar.findSinglePath(start.getUnderlyingNode(),
					end.getUnderlyingNode());

			long endTimer = System.currentTimeMillis();
			System.out.println("FindSinglePath: " + (endTimer - startTimer)
					+ " milliseconds");
			if (path != null) {
				System.out.println("Path " + path.toString());
			}

			tx.success();
			return path;
		} finally {
			tx.finish();
		}
	}

}
