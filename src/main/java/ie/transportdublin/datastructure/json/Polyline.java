package ie.transportdublin.datastructure.json;

import java.util.ArrayList;

public class Polyline {

	ArrayList<LatLng> latlngList;
	String strokeColor;
	Double strokeOpacity;
	Double strokeWeight;

	public Polyline(String strokeColor, Double strokeOpacity,
			Double strokeWeight) {
		super();
		latlngList = new ArrayList<LatLng>();
		this.strokeColor = strokeColor;
		this.strokeOpacity = strokeOpacity;
		this.strokeWeight = strokeWeight;
	}

	public void addLatLng(LatLng latlng) {
		latlngList.add(latlng);
	}

	@Override
	public String toString() {
		return latlngList.toString();

	}

}
