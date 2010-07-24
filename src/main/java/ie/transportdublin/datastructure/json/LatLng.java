package ie.transportdublin.datastructure.json;

public class LatLng {

	Double lat;
	Double lng;

	public LatLng() {
		super();
	}

	public LatLng(Double lat, Double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LatLng [lat=" + lat + ", lng=" + lng + "]";
	}

}
