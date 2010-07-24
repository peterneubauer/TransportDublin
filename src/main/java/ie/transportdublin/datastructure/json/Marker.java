package ie.transportdublin.datastructure.json;

public class Marker {

	String name;
	String time;

	String address;
	LatLng latlng;

	public Marker(String name, String time, String address, LatLng latlng) {
		super();
		this.name = name;
		this.time = time;
		this.address = address;
		this.latlng = latlng;
	}

	public String getAddress() {
		return address;
	}

	public LatLng getLatlng() {
		return latlng;
	}

	public String getName() {
		return name;
	}

	public String getTime() {
		return time;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Location [address=" + address + ", latlng=" + latlng
				+ ", name=" + name + ", time=" + time + "]";
	}

}
