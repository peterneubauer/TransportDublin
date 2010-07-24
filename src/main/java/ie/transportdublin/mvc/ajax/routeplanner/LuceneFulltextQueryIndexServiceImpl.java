package ie.transportdublin.mvc.ajax.routeplanner;

import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.geocost.GeoCostEvaluator;

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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;


public class LuceneFulltextQueryIndexServiceImpl {
	static final String LAYER_NAME = "stops_layer";
	private static GraphDatabaseService graphDbService;
	private static IndexService indexService;

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

	public LuceneFulltextQueryIndexServiceImpl() {
		super();
	}

	public LuceneFulltextQueryIndexServiceImpl(
			GraphDatabaseService graphDbService, IndexService indexService) {
		super();
		LuceneFulltextQueryIndexServiceImpl.graphDbService = graphDbService;
		LuceneFulltextQueryIndexServiceImpl.indexService = indexService;
		// indexService= new LuceneFulltextQueryIndexService(graphDbService);
	}

	public DateTime setupDateTime(String timeString) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
		DateTime dateTime = formatter.parseDateTime(timeString);
		return dateTime;
	}


}
