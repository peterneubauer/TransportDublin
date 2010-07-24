package ie.transportdublin.datastructure.json;

public class Directions {

	Polylines polylines;
	Markers markers;

	public Directions(Polylines polylines, Markers markers) {
		super();
		this.polylines = polylines;
		this.markers = markers;
	}

	public Markers getMarkers() {
		return markers;
	}

	public Polylines getPolylines() {
		return polylines;
	}

	public void setMarkers(Markers markers) {
		this.markers = markers;
	}

	public void setPolylines(Polylines polylines) {
		this.polylines = polylines;
	}

}
