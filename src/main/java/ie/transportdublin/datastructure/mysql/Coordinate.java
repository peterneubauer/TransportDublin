package ie.transportdublin.datastructure.mysql;

public class Coordinate {

	private final double latitude;
	private final double longitude;

	/** distance Value */
	private double distance;

	/** distance Value */
	private int time;

	public Coordinate(final double latitude, final double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Constructor containing
	 * 
	 * @param latitude
	 * @param longitude
	 * @param distance
	 */
	public Coordinate(double latitude, double longitude, double distance) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "longitude=" + longitude + ", latitude=" + latitude;
	}
}
