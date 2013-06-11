package ie.transportdublin.setupgraph;

import com.vividsolutions.jts.geom.Coordinate;
import ie.transportdublin.datastructure.mysql.Stop;
import ie.transportdublin.datastructure.neo4j.RelationshipTypes;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.datastructure.neo4j.Waypoint;
import ie.transportdublin.geocost.GeoCostEvaluator;
import org.joda.time.Minutes;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class SetupTimetableConnectionsStep3 {

    private static final String DB_PATH = "data/neo4j-db";
    static TreeMap<String, Stop> stopList;
    private static GraphDatabaseService graphDb;
    private static IndexManager index;

    public SetupTimetableConnectionsStep3() {

        super();
        graphDb = new EmbeddedGraphDatabase(DB_PATH);
//		index = new LuceneFulltextQueryIndexService(graphDb);
        DatabaseHelper.setupDB();
        stopList = DatabaseHelper.readAllStops();
        setupStopConnections();

    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Double> getNeighboursWithin(double lat,
                                                              double lng, double distance, GraphDatabaseService graphDbService) {

        SpatialDatabaseService spatialService = new SpatialDatabaseService(
                graphDbService);
        Transaction tx = graphDbService.beginTx();
        try {

            Layer layer = spatialService.getLayer("stops_layer");
            List<SpatialDatabaseRecord> results = GeoPipeline.startNearestNeighborLatLonSearch(
                    layer, new Coordinate(lng, lat), distance).sort(
                    "OrthodromicDistance").toSpatialDatabaseRecordList();
            HashMap<String, Double> neighbours = new HashMap<String, Double>();
            for (SpatialDatabaseRecord record : results) {

                // Calculate Distances
                double stopLat = Double.parseDouble((String) record
                        .getProperty("lat"));
                double stopLng = Double.parseDouble((String) record
                        .getProperty("lng"));
                GeoCostEvaluator geo = new GeoCostEvaluator();
                double distanceBetween = geo.distance(lat, lng, stopLat,
                        stopLng);
                neighbours.put((String) record.getProperty("StopID"),
                        distanceBetween);

            }
            tx.success();

            return neighbours;

        } finally {
            tx.finish();

        }

    }

    public static void main(String[] args) {

        SetupTimetableConnectionsStep3 SetupTimetableConnectionsStep3 = new SetupTimetableConnectionsStep3();
        graphDb.shutdown();
    }

    /**
     * Gets all StopTimes that are connected to a Waypoint
     *
     * @param stopid
     * @return List of StopTimes
     */
    public ArrayList<StopTime> getStopTimesConnectedToWaypoint(String stopid) {

        Node waypointNode = index.forNodes(ImportSpatialStopDataStep2.LAYER_NAME).get(Waypoint.STOPID, stopid).getSingle();
        Waypoint waypoint = new Waypoint(waypointNode);
        // System.out.println("waypoint " + waypoint);
        Iterable<Relationship> relationships = waypointNode.getRelationships(
                RelationshipTypes.CONNECTION, Direction.OUTGOING);
        ArrayList<StopTime> stopTimeList = new ArrayList<StopTime>();
        for (Relationship relationship : relationships) {
            Node stopTimeNode = relationship.getOtherNode(waypointNode);
            StopTime stopTime = new StopTime(stopTimeNode);
            // System.out.println("stopTime " + stopTime);

            stopTimeList.add(stopTime);
        }

        return stopTimeList;
    }

    public int minutesBetweenStopTimes(StopTime stopTime,
                                       StopTime neighbourStopTime) {
        Minutes m = Minutes.minutesBetween(stopTime.getDateTime(),
                neighbourStopTime.getDateTime());
        return m.getMinutes();

    }

    private void setupStopConnections() {
        int neoTransactionCounter = 0;
        Transaction tx = graphDb.beginTx();
        try {

            // every stop
            for (String stopid : stopList.keySet()) {

                Stop stop = stopList.get(stopid);
                // Stops within 100m
                HashMap<String, Double> neighboursList = getNeighboursWithin(
                        stop.getLatitude(), stop.getLongitude(), 0.1, graphDb);
                // System.out.println("getStopTimesConnectedToWaypoint : stopid "
                // + stopid);
                ArrayList<StopTime> stopTimesList = getStopTimesConnectedToWaypoint(stopid);

                // For every StopTime connected the waypoint
                for (StopTime stopTime : stopTimesList) {
                    // For each Neighbour
                    for (String neighbourStopID : neighboursList.keySet()) {
                        ArrayList<StopTime> neighbourStopTimesList = getStopTimesConnectedToWaypoint(neighbourStopID);
                        for (StopTime neighbourStopTime : neighbourStopTimesList) {
                            boolean sameDay = (Integer.parseInt(stopTime
                                    .getDay()) == Integer
                                    .parseInt(neighbourStopTime.getDay()));
                            boolean sameRoute = (stopTime.getStopID()
                                    .substring(2, 7).equals(neighbourStopTime
                                            .getStopID().substring(2, 7)));

                            if (sameDay && !sameRoute) {
                                int waitingTime = minutesBetweenStopTimes(
                                        stopTime, neighbourStopTime);
                                Double neighbourDistance = neighboursList
                                        .get(neighbourStopID);

                                int distanceInMinutes = (int) (neighbourDistance * 0.02);
                                if (waitingTime > 5 && waitingTime < 40
                                        && distanceInMinutes < waitingTime) {
                                    System.out.println(stopTime.getStopTimeID()
                                            + " .createBusTo "
                                            + neighbourStopTime.getStopTimeID()
                                            + " " + waitingTime);
                                    int changeBusPenalty = 40;
                                    stopTime.createBusTo(neighbourStopTime,
                                            (double) waitingTime
                                                    + changeBusPenalty);

                                    if (++neoTransactionCounter % 2000 == 0) {
                                        // Commit the transaction every now and
                                        // then
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
                }
            }

            tx.success();
        } finally {
            tx.finish();
            graphDb.shutdown();

        }

    }

}
