package ie.transportdublin.datastructure.neo4j;

import ie.transportdublin.datastructure.json.LatLng;
import ie.transportdublin.datastructure.mysql.Coordinate;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;


public class StopTime {

	public static final String DAY = "day";
	public static final String TIME = "time";
	public static final String STOPTIMEID = "stopTimeID";
	public static final String STOPID = "stopID";
	public static final String COST = "cost";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";

	final Node underlyingNode;

	public StopTime(final GraphDatabaseService graphDb, Integer day,
			DateTime time, String stopID, Double lat, Double lng) {

		this.underlyingNode = graphDb.createNode();
		String timeString = time.toString("HH:mm:ss");
		timeString = timeString.replace(":", "");
		underlyingNode.setProperty(STOPTIMEID,
				(day.toString() + stopID + timeString));
		underlyingNode.setProperty(DAY, day.toString());
		underlyingNode.setProperty(TIME, time.toString("HH:mm:ss"));
		underlyingNode.setProperty(STOPID, stopID);
		underlyingNode.setProperty(LATITUDE, lat);
		underlyingNode.setProperty(LONGITUDE, lng);

		System.out.println(this);
	}

	public StopTime(final Node node) {
		this.underlyingNode = node;
	}

	public void createBusTo(final StopTime other, double cost) {
		Relationship bus = underlyingNode.createRelationshipTo(
				other.underlyingNode, RelationshipTypes.BUS);
		bus.setProperty(COST, cost);
	}

	public Coordinate getCoordinates() {
		double latitude = (Double) underlyingNode.getProperty(LATITUDE);
		double longitude = (Double) underlyingNode.getProperty(LONGITUDE);
		return new Coordinate(latitude, longitude);
	}

	public DateTime getDateTime() {
		String dateString = (String) underlyingNode.getProperty(TIME);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
		DateTime dateTime = formatter.parseDateTime(dateString);
		return dateTime;
	}

	public String getDay() {
		return (String) underlyingNode.getProperty(DAY);
	}

	public LatLng getLatLng() {
		LatLng latlng = new LatLng(this.getCoordinates().getLatitude(), this
				.getCoordinates().getLongitude());

		return latlng;

	}

	public String getStopID() {
		return (String) underlyingNode.getProperty(STOPID);
	}

	public String getStopTimeID() {
		return (String) underlyingNode.getProperty(STOPTIMEID);
	}

	public String getTime() {
		return (String) underlyingNode.getProperty(TIME);
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String toString() {
		return "StopTime [STOPTIMEID=" + getStopTimeID() + ", day= " + getDay()
				+ ", time= " + getTime() + " STOPID " + getStopID() + "]";
	}

}
