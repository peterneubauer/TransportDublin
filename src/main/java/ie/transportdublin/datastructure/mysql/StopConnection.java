package ie.transportdublin.datastructure.mysql;

/**
 * A connection between two Stops
 * 
 * @author Administrator
 */
// @Entity
// @Table(name = "CONNECTIONS")
public class StopConnection {

	/** auto increasing id number */
	// @Id
	// /@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "ID")
	private int id;

	/** From StopID */
	// @Column(name = "FROM_STATION_STOPID", length = 10, nullable = false)
	private String fromStopID;

	/** To StopID */
	// @Column(name = "TO_STATION_STOPID", length = 10, nullable = false)
	private String toStopID;

	/** Time */
	// @Column(name = "TIME")
	private double time;

	/** Distance in km */
	// @Column(name = "DISTANCE")
	private double distance;

	/** DELETE-- Distance in km */
	// @Column(name = "CONNTYPE")
	private String connType;

	public StopConnection() {
		super();
	}

	/**
	 * Stop Connection containing All information needed
	 * 
	 * @param fromStopID2
	 * @param toStopID2
	 * @param time2
	 * @param distance2
	 * @param connType2
	 */
	public StopConnection(String fromStopID2, String toStopID2, double time2,
			double distance2) {

		this.fromStopID = fromStopID2;
		this.toStopID = toStopID2;
		this.time = time2;

		this.distance = distance2;

	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @return the fromStopID
	 */
	public String getFromStopID() {
		return fromStopID;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return the toStopID
	 */
	public String getToStopID() {
		return toStopID;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * @param fromStopID
	 *            the fromStopID to set
	 */
	public void setFromStopID(String fromStopID) {
		this.fromStopID = fromStopID;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @param toStopID
	 *            the toStopID to set
	 */
	public void setToStopID(String toStopID) {
		this.toStopID = toStopID;
	}

}
