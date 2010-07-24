package ie.transportdublin.datastructure.mysql;

/**
 * Represent a bus stop
 * Contains info on StopID, latitude, longitude, stopName
 * @author pfitzgerald
 *
 */
/**
 * @author pfitzgerald
 * 
 */
public class Stop

{

	/**
	 * This function converts decimal degrees to radians
	 * 
	 * @param deg
	 * @return
	 */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return dist;

	}

	/**
	 * This function converts radians to decimal degrees
	 * 
	 * @param rad
	 * @return
	 */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	/**
	 * autoincreasing id num
	 */
	private int id;
	/**
	 * Lat value
	 */
	private Double latitude;
	/**
	 * Lng Value
	 */
	private Double longitude;

	/**
	 * Parnell St
	 */
	private String stopName;

	/**
	 * 1100110001
	 */
	private String routeID;

	private String stopID;

	public Stop(int id, String routeID, String stopID2, String stopName2,
			Double lat, Double lng) {
		this.id = id;
		this.stopID = stopID2;
		this.routeID = routeID;
		this.stopName = stopName2;
		this.latitude = lat;
		this.longitude = lng;

	}

	public Stop(String stopID, double latitude, double longitude,
			String stopName) {
		super();
		this.stopID = stopID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.stopName = stopName;
	}

	public int getId() {
		return id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getRouteID() {
		return routeID;
	}

	public String getStopID() {
		return stopID;
	}

	public String getStopName() {
		return stopName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	public void setStopID(String stopID) {
		this.stopID = stopID;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	@Override
	public String toString() {
		return "Stop [id=" + id + ", latitude=" + latitude + ", longitude="
				+ longitude + ", routeID=" + routeID + ", stopID=" + stopID
				+ ", stopName=" + stopName + "]";
	}

}