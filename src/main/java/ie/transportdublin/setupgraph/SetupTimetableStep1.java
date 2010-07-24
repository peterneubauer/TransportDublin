package ie.transportdublin.setupgraph;

import ie.transportdublin.datastructure.mysql.BusJourney;
import ie.transportdublin.datastructure.mysql.Stop;
import ie.transportdublin.datastructure.neo4j.RelationshipTypes;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.datastructure.neo4j.Waypoint;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneFulltextQueryIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public class SetupTimetableStep1 {

	private static final String DB_PATH = "data/neo4j-db";
	private static GraphDatabaseService graphDb;
	private static IndexService index;

	/**
	 * id: 1074700 - times: [(1,06:10:00), (1,06:35:00), (1,07:10:00),
	 * (1,07:45:00), (1,08:10:00), (2,08:30:00), (2,08:50:00), 09:20:00
	 */
	static TreeMap<String, ArrayList<BusJourney>> routeToBusJourneyList;
	/**
	 * id: 1074700 - times: [0, 15, 30, 45]
	 */
	static TreeMap<String, ArrayList<Integer>> routeToTimeProgressionList;

	static TreeMap<String, Stop> stopList;

	static TreeMap<String, Waypoint> waypointList;

	private static String convertToTripleDigit(int stopProgressionCounter) {
		String tripleDigit;
		if (stopProgressionCounter < 10) {
			tripleDigit = "00" + stopProgressionCounter;
		} else if (stopProgressionCounter < 100) {
			tripleDigit = "0" + stopProgressionCounter;
		} else {
			tripleDigit = "" + stopProgressionCounter;
		}
		return tripleDigit;
	}

	public static void main(String[] args) {

		SetupTimetableStep1 setupTimetable = new SetupTimetableStep1();
		graphDb.shutdown();
	}

	public SetupTimetableStep1() {

		super();
		graphDb = new EmbeddedGraphDatabase(DB_PATH);
		index = new LuceneFulltextQueryIndexService(graphDb);
		DatabaseHelper.setupDB();

		routeToBusJourneyList = DatabaseHelper.readTimetable();
		routeToTimeProgressionList = DatabaseHelper.readAllStopTimes();
		stopList = DatabaseHelper.readAllStops();

		waypointList = new TreeMap<String, Waypoint>();
		setupStopsOnSameRoute();

	}

	/**
	 * Create Waypoint
	 * @param stop
	 * @return
	 */
	private Waypoint createWaypoint(Stop stop) {

		Waypoint waypoint = new Waypoint(graphDb, stop.getLatitude(), stop
				.getLongitude(), stop.getStopID(), stop.getRouteID(), stop
				.getStopName());
		return waypoint;
	}

	private void setupStopsOnSameRoute() {

		Transaction tx = graphDb.beginTx();
		int neoTransactionCounter = 0;
		boolean setupReferenceNode = true;

		try {

			for (Entry<String, Stop> stopEntry : stopList.entrySet()) {

				String stopid = stopEntry.getKey();
				Stop stop = stopEntry.getValue();
				Waypoint waypoint = createWaypoint(stop);

				// Setup reference node to first Waypoint
				if (setupReferenceNode) {
					graphDb.getReferenceNode().createRelationshipTo(
							waypoint.getUnderlyingNode(),
							RelationshipTypes.REFERENCE);
					setupReferenceNode = false;
				}
				System.out.println(" NEW Waypoint " + stop.getStopID());
				index.index(waypoint.getUnderlyingNode(), Waypoint.STOPID,
						waypoint.getStopID());
				waypointList.put(stopid, waypoint);
				tx.success();
				tx.finish();
				tx = graphDb.beginTx();
				if (++neoTransactionCounter % 10000 == 0) {
					// Commit the transaction every now and then

					System.out.println(" New transaction "
							+ neoTransactionCounter);
					tx.success();
					tx.finish();
					tx = graphDb.beginTx();
				}
			}

			// Every Route
			for (Entry<String, ArrayList<BusJourney>> routeSqlEntry : routeToBusJourneyList
					.entrySet()) {
				// Get all departure times
				// [(1,06:10:00), (1,06:35:00), ...

				ArrayList<BusJourney> routeToBusJourneysList = routeSqlEntry
						.getValue();
				if (routeToBusJourneysList != null) {
					for (BusJourney busJourney : routeToBusJourneysList) {
						// (1,06:10:00)

						int timeProgressionCounter = 1;
						int timeProgressionPrevious = 0;
						StopTime stopTimePrevious = null;
						for (Integer timeProgression : routeToTimeProgressionList
								.get(routeSqlEntry.getKey())) {
							DateTime progressionTime = busJourney.getTime()
									.plusMinutes(timeProgression);

							String stopid = routeSqlEntry.getKey()
									+ convertToTripleDigit(timeProgressionCounter);
							StopTime stopTime = setupStopTime(busJourney
									.getDay(), progressionTime, stopid);
							index.index(stopTime.getUnderlyingNode(),
									StopTime.STOPTIMEID, stopTime
											.getStopTimeID());
							if (timeProgressionCounter != 1) {
								int cost = (timeProgression - timeProgressionPrevious);
								stopTimePrevious.createBusTo(stopTime, cost);
								System.out.println(stopTimePrevious
										.getStopTimeID()
										+ " -- " + cost + " -> " + stopTime);
							}

							timeProgressionCounter++;
							timeProgressionPrevious = timeProgression;
							stopTimePrevious = stopTime;

							if (++neoTransactionCounter % 10000 == 0) {
								// Commit the transaction every now and then

								System.out.println(" New transaction "
										+ neoTransactionCounter);
								tx.success();
								tx.finish();
								tx = graphDb.beginTx();
							}

						}
					}
				}
			}
			tx.success();
		} finally {
			tx.finish();
			graphDb.shutdown();

		}

	}

	/**
	 * Create StopTime and link to parent Waypoint
	 * 
	 * @param day
	 * @param progressionTime
	 * @param stopid
	 */
	private StopTime setupStopTime(int day, DateTime progressionTime,
			String stopid) {
		Stop stop = stopList.get(stopid);
		StopTime stopTime = new StopTime(graphDb, day, progressionTime, stopid,
				stop.getLatitude(), stop.getLongitude());
		Waypoint waypoint = waypointList.get(stopid);
		waypoint.createConnectionTo(stopTime);
		return stopTime;

	}

}
