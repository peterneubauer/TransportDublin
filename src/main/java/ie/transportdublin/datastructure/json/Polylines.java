package ie.transportdublin.datastructure.json;

import java.util.ArrayList;

public class Polylines {

	ArrayList<Polyline> polylineList = new ArrayList<Polyline>();

	public void addPolyline(Polyline polyline) {
		polylineList.add(polyline);
	}

	public ArrayList<Polyline> getPolylineList() {
		return polylineList;
	}

	public void setPolylineList(ArrayList<Polyline> polylineList) {
		this.polylineList = polylineList;
	}

}
