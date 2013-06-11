package ie.transportdublin.mvc.ajax.routeplanner;

import com.vividsolutions.jts.geom.Coordinate;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.geocost.GeoCostEvaluator;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.index.IndexManager;

import java.util.*;


public class LuceneFulltextQueryIndexServiceImpl {
    static final String LAYER_NAME = "stops_layer";
    private static GraphDatabaseService graphDbService;
    private static IndexManager indexService;

    public LuceneFulltextQueryIndexServiceImpl() {
        super();
    }

    public LuceneFulltextQueryIndexServiceImpl(
            GraphDatabaseService graphDbService, IndexManager indexService) {
        super();
        LuceneFulltextQueryIndexServiceImpl.graphDbService = graphDbService;
        LuceneFulltextQueryIndexServiceImpl.indexService = indexService;
        // indexService= new LuceneFulltextQueryIndexService(graphDbService);
    }

    public static HashMap<String, Double> getNeighboursWithin(double lat,
                                                              double lng, double distance) {

        SpatialDatabaseService spatialService = new SpatialDatabaseService(
                graphDbService);
        Layer layer = spatialService.getLayer(LAYER_NAME);
        List<SpatialDatabaseRecord> results = GeoPipeline.startNearestNeighborLatLonSearch(
                layer, new Coordinate(lng, lat), distance).sort(
                "OrthodromicDistance").toSpatialDatabaseRecordList();

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
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public DateTime setupDateTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
        DateTime dateTime = formatter.parseDateTime(timeString);
        return dateTime;
    }


}
