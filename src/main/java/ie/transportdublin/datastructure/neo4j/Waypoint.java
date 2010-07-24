package ie.transportdublin.datastructure.neo4j;

import ie.transportdublin.datastructure.mysql.Coordinate;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;


public class Waypoint {
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
	public static final String STOPID = "stopID";
	public static final String ROUTEID = "routeID";
	public static final String STOPNAME = "stopName";
	public static final String COST = "cost";
	private final Node underlyingNode;

	public Waypoint(final GraphDatabaseService graphDb, Double lat, Double lng,
			String stopID, String routeID, String stopName)

	{
		this.underlyingNode = graphDb.createNode();
		Coordinate coordinates = new Coordinate(lat, lng);
		underlyingNode.setProperty(LATITUDE, coordinates.getLatitude());
		underlyingNode.setProperty(LONGITUDE, coordinates.getLongitude());
		underlyingNode.setProperty(STOPID, stopID);
		underlyingNode.setProperty(ROUTEID, routeID);
		underlyingNode.setProperty(STOPNAME, stopName);
		// System.out.println( this );
	}

	public Waypoint(final Node node) {
		this.underlyingNode = node;
	}

	public void createBusTo(final Waypoint other, double cost) {
		Relationship road = underlyingNode.createRelationshipTo(
				other.underlyingNode, RelationshipTypes.ROAD);
		road.setProperty(COST, cost);
	}

	public void createConnectionTo(final StopTime other) {
		Relationship road = underlyingNode.createRelationshipTo(
				other.underlyingNode, RelationshipTypes.CONNECTION);
		road.setProperty(COST, 0);
	}

	public Coordinate getCoordinates() {
		double latitude = (Double) underlyingNode.getProperty(LATITUDE);
		double longitude = (Double) underlyingNode.getProperty(LONGITUDE);
		return new Coordinate(latitude, longitude);
	}

	public String getRouteid() {
		return (String) underlyingNode.getProperty(ROUTEID);
	}

	public String getStopID() {
		return (String) underlyingNode.getProperty(STOPID);
	}

	public String getStopname() {
		return (String) underlyingNode.getProperty(STOPNAME);
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String toString() {
		return "Waypoint [stopID=" + getStopID() + ", routeid= " + getRouteid()
				+ ", stopname= " + getStopname() + ", " + getCoordinates()
				+ "]";
	}

}
