package ie.transportdublin.mvc.ajax.pathfinder;

import ie.transportdublin.datastructure.neo4j.RelationshipTypes;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.datastructure.neo4j.Waypoint;
import ie.transportdublin.geocost.GeoCostEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.Search;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.SpatialIndexReader;
import org.neo4j.gis.spatial.query.SearchPointsWithinOrthodromicDistance;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;


public class SetupGraph {

	private static GraphDatabaseService graphDbService;
	public static HashMap<String, Double> getNeighboursWithin(double lat,
			double lng, double distance) {

		SpatialDatabaseService spatialService = new SpatialDatabaseService(
				graphDbService);
		Layer layer = spatialService.getLayer(LAYER_NAME);
		SpatialIndexReader reader = layer.getIndex();
		Point refPoint = layer.getGeometryFactory().createPoint(
				new Coordinate(lng, lat));
		Search search = new SearchPointsWithinOrthodromicDistance(refPoint,
				distance);

		reader.executeSearch(search);
		// System.out.println("Distance " + 1 + "km:");
		List<SpatialDatabaseRecord> results = search.getResults();
		HashMap<String, Double> stops = new HashMap<String, Double>();
		for (SpatialDatabaseRecord record : results) {
			// Calculate Distances
			double stopLat = Double.parseDouble((String) record
					.getProperty("lat"));
			double stopLng = Double.parseDouble((String) record
					.getProperty("lng"));
			GeoCostEvaluator geo = new GeoCostEvaluator();
			double distanceBetween = geo.distance(lat, lng, stopLat, stopLng);

			// cities.add((String) record.getProperty("RouteNum"));
			// cities.add((String) record.getProperty("Address"));
			// cities.add((String) record.getProperty("Area"));
			stops.put((String) record.getProperty("StopID"), distanceBetween);
		}

		System.out.println("Stops size  " + stops.size());
		stops = (HashMap<String, Double>) sortByValue(stops);
		return stops;
	}
	public static int minutesBetweenStopTimes(DateTime startTime,
			StopTime neighbourStopTime) {
		Minutes m = Minutes.minutesBetween(startTime, neighbourStopTime
				.getDateTime());
		return m.getMinutes();

	}

	static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		// logger.info(list);
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private IndexService indexService;

	private static final String LAYER_NAME = "stops_layer";

	public SetupGraph() {
		super();
	}

	public SetupGraph(GraphDatabaseService graphDbService,
			IndexService indexService) {
		super();
		SetupGraph.graphDbService = graphDbService;
		this.indexService = indexService;
	}

	/**
	 * Gets all StopTimes that are connected to a Waypoint
	 * 
	 * @param stopid
	 * @return List of StopTimes
	 */
	public ArrayList<StopTime> getStopTimesConnectedToWaypoint(String stopid) {

		Node waypointNode = indexService.getSingleNode(Waypoint.STOPID, stopid);
		Iterable<Relationship> relationships = waypointNode.getRelationships(
				RelationshipTypes.CONNECTION, Direction.OUTGOING);
		ArrayList<StopTime> stopTimeList = new ArrayList<StopTime>();
		for (Relationship relationship : relationships) {
			Node stopTimeNode = relationship.getOtherNode(waypointNode);
			StopTime stopTime = new StopTime(stopTimeNode);
			stopTimeList.add(stopTime);
		}

		return stopTimeList;
	}

	public DateTime setupDateTime(String timeString) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
		DateTime dateTime = formatter.parseDateTime(timeString);
		return dateTime;
	}

	/**
	 * Creates the start coordinate in the db, Find all stops within walking
	 * distance of the end coordinate using Neo4j spatial query Add connections
	 * from all stops within walking distance i.e 100m, to the end coordinate
	 * that has a bus departure time within 3 hours of the start time
	 * 
	 * @param lat
	 * @param lng
	 * @param day
	 * @param startTime
	 * @return
	 */
	public StopTime setupEndWaypoint(double lat, double lng, int day,
			DateTime startTime) {
		String CoordinateId = "" + lat + lng;
		DateTime endTime = new DateTime();
		Node endCoordinate = null;
		// TODO: Get node un till : removed
		StopTime endCoordinateStopTime;
		if (endCoordinate == null) {
			endCoordinateStopTime = new StopTime(graphDbService, 1, endTime,
					CoordinateId, lat, lng);
			indexService.index(endCoordinateStopTime.getUnderlyingNode(),
					StopTime.STOPTIMEID, endCoordinateStopTime
							.getStopTimeID());
		} else {
			endCoordinateStopTime = new StopTime(endCoordinate);
			System.out.println(" ** CoordinateStopTime in db:  "
					+ endCoordinateStopTime);
		}

		HashMap<String, Double> endNeighboursList = getNeighboursWithin(lat,
				lng, 0.3);
		int neighboursCount = 0;
		int stoptimesCounter = 0;
		for (Map.Entry<String, Double> endNeighbour : endNeighboursList
				.entrySet()) {
			if (++neighboursCount < 3) {

				String neighbourStopID = endNeighbour.getKey();
				Double distance = endNeighbour.getValue();
				// System.out.println("endNeighbour  "+endNeighbour.getKey() +
				// "/" + endNeighbour.getValue());

				int distanceInMinutes = (int) (distance * 0.02);
				ArrayList<StopTime> neighbourStopTimesList = getStopTimesConnectedToWaypoint(neighbourStopID);

				for (StopTime endNeighbourStopTime : neighbourStopTimesList) {

					int waitingTime = minutesBetweenStopTimes(startTime,
							endNeighbourStopTime);
					if (distanceInMinutes < 10 && waitingTime > 0
							&& waitingTime < 120
							&& endNeighbourStopTime.getDay().equals(day + "")) {
						// System.out.println("END waiting time"
						// +waitingTime+"  " +endNeighbourStopTime+
						// " createBusTo:" +" - "+distanceInMinutes+" ->"
						// +endCoordinateStopTime);
						endNeighbourStopTime.createBusTo(endCoordinateStopTime,
								distanceInMinutes);
						stoptimesCounter++;
					}
				}
			}
		}

		System.out.println("end stoptimesCounter: " + stoptimesCounter);
		return endCoordinateStopTime;
	}

	/**
	 * Creates the start coordinate in the db, Find all stops within walking
	 * distance of the start coordinate using Neo4j spatial query Add
	 * connections from the start coordinate to all stops within walking
	 * distance i.e 100m, that have a bus departure time in less than 20mins and
	 * with walking distance in minutes as the cost
	 * 
	 * @param lat
	 * @param lng
	 * @param address
	 *            TODO :
	 * @param day
	 * @param startTime
	 * @return
	 */
	public StopTime setupStartWaypoint(double lat, double lng, String address,
			int day, DateTime startTime) {

		// check if in db then create start coord
		String CoordinateId = "" + lat + lng;
		StopTime CoordinateStopTime;
		String nodeId = (day + "" + CoordinateId + startTime
				.toString("HH:mm:ss"));
		System.out.println(" Setup nodeId  " + nodeId);
		Node startCoordinate = null;
		// TODO: Do not read coordinate from db untill : removed
		// indexService.getSingleNode( StopTime.STOPTIMEID, nodeId);

		if (startCoordinate == null) {
			CoordinateStopTime = new StopTime(graphDbService, day, startTime,
					CoordinateId, lat, lng);
			indexService.index(CoordinateStopTime.getUnderlyingNode(),
					StopTime.STOPTIMEID, CoordinateStopTime
							.getStopTimeID());
			System.out.println(" Setup StartTime  " + CoordinateStopTime);
		} else {
			CoordinateStopTime = new StopTime(startCoordinate);
			System.out.println(" ** CoordinateStopTime in db:  "
					+ CoordinateStopTime);
		}

		Transaction tx = graphDbService.beginTx();
		try {
			long startTimer = System.currentTimeMillis();
			HashMap<String, Double> neighboursMap = getNeighboursWithin(lat,
					lng, .3);

			long endTimer = System.currentTimeMillis();
			System.out.println("** getNeighboursWithin 100m, size: "
					+ neighboursMap.size() + " miliseconds "
					+ (endTimer - startTimer) + " milliseconds");

			startTimer = System.currentTimeMillis();
			for (Map.Entry<String, Double> neighbour : neighboursMap.entrySet()) {
				String neighbourStopID = neighbour.getKey();
				Double distance = neighbour.getValue();
				int distanceInMinutes = (int) (distance * 0.02);
				// 11100100001*
				// 1100010000117:30:00
				String query = 1 + neighbourStopID + "09*";

				for (Node hit : indexService.getNodes(StopTime.STOPTIMEID,
						query)) {
					StopTime neighbourStopTime = new StopTime(hit);
					int waitingTime = minutesBetweenStopTimes(startTime,
							neighbourStopTime);

					if (distanceInMinutes < waitingTime && waitingTime < 20) {
						CoordinateStopTime.createBusTo(neighbourStopTime,
								waitingTime);

					}
				}

			}

			endTimer = System.currentTimeMillis();
			System.out.println("setupStartWaypoint "
					+ (endTimer - startTimer) + " milliseconds");

			tx.success();

		} finally {
			tx.finish();

		}
		return CoordinateStopTime;

	}

	
}
