package ie.transportdublin.mvc.ajax.routeplanner;

public class RoutePlanner {

	private double lat1;
	private double lng1;
	private double lat2;
	private double lng2;

	public RoutePlanner(double lat1, double lng1, double lat2, double lng2) {
		super();
		this.lat1 = lat1;
		this.lng1 = lng1;
		this.lat2 = lat2;
		this.lng2 = lng2;
	}

	public double getLat1() {
		return lat1;
	}

	public double getLat2() {
		return lat2;
	}

	public double getLng1() {
		return lng1;
	}

	public double getLng2() {
		return lng2;
	}

	public void setLat1(double lat1) {
		this.lat1 = lat1;
	}

	public void setLat2(double lat2) {
		this.lat2 = lat2;
	}

	public void setLng1(double lng1) {
		this.lng1 = lng1;
	}

	public void setLng2(double lng2) {
		this.lng2 = lng2;
	}

}