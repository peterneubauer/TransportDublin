package ie.transportdublin.datastructure.json;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;

public class Markers {

	@NotNull
	private ArrayList<Marker> markerList = new ArrayList<Marker>();

	public Markers() {
		super();
	}

	public void add(Marker location) {
		this.markerList.add(location);
	}

	public ArrayList<Marker> getLocationList() {
		return markerList;
	}

	public void setLocationList(ArrayList<Marker> locationList) {
		this.markerList = locationList;
	}

}