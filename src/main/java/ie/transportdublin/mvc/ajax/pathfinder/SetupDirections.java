package ie.transportdublin.mvc.ajax.pathfinder;

import ie.transportdublin.datastructure.json.Directions;
import ie.transportdublin.datastructure.json.LatLng;
import ie.transportdublin.datastructure.json.Marker;
import ie.transportdublin.datastructure.json.Markers;
import ie.transportdublin.datastructure.json.Polyline;
import ie.transportdublin.datastructure.json.Polylines;
import ie.transportdublin.datastructure.neo4j.StopTime;
import ie.transportdublin.datastructure.neo4j.Waypoint;

import java.util.ArrayList;
import java.util.LinkedList;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Node;
import org.neo4j.index.IndexService;

import com.google.gson.Gson;


public class SetupDirections {

	private IndexService indexService;
	Polylines polylines;
	Markers markers;
	WeightedPath path;

	/**
	 * Sets up a Directions Json which is used to populate the route on the map
	 * @param path
	 * @param indexService
	 */
	public SetupDirections(WeightedPath path, IndexService indexService) {

		this.path = path;
		this.indexService = indexService;
		if (path != null) {
			System.out.println(" PATH FOUND");

			for (Node node : path.nodes()) {
				StopTime stopTime = new StopTime(node);
				System.out.println(stopTime);
			}

			setupPolylines();
			setupMarkers();
		} else {
			System.out.println(" NO PATH");
			// no path
			String[] suggestions = new String[3];
			suggestions[0] = "Tester suggestion[0] ";
		}
		// TODO Auto-generated constructor stub
	}

	public String getJson() {

		Directions directions = new Directions(polylines, markers);
		Gson gson2 = new Gson();
		String json = gson2.toJson(directions);
		System.out.println("RETURN:" + json);
		return json;
	}

	private LatLng nodeToLatLng(Node node) {
		StopTime stopTime = new StopTime(node);
		LatLng latLng = new LatLng(stopTime.getCoordinates().getLatitude(),
				stopTime.getCoordinates().getLongitude());
		return latLng;
	}

	public void setupMarkers() {
		System.out.println("setupMarkers");
		markers = new Markers();
		ArrayList<Marker> locationList = new ArrayList<Marker>();
		StopTime prevStopTime = null;
		for (Node node : path.nodes()) {
			StopTime stopTime = new StopTime(node);
			if (prevStopTime != null) {
				if (!stopTime.getStopID().substring(3, 6).equals(
						prevStopTime.getStopID().substring(3, 6))) {
					// newRoute=true;
					System.out.println(" newRoute ! Marker"
							+ stopTime.getStopTimeID());
					LatLng latlng = new LatLng(stopTime.getCoordinates()
							.getLatitude(), stopTime.getCoordinates()
							.getLongitude());
					Node stop = indexService.getSingleNode(Waypoint.STOPID,
							stopTime.getStopID());
					// if is null or is star or end node
					if (stop != null) {
						Waypoint waypoint = new Waypoint(stop);

						String stopName = waypoint.getStopname();
						String routeId = waypoint.getRouteid();
						String time = stopTime.getTime();
						Marker location = new Marker(routeId,
								time, stopName, latlng);
						System.out.println("*location " + location);
						locationList.add(location);
					}

				}
			} else {
				System.out.println(" first ! Marker");
			}
			prevStopTime = stopTime;
		}
		markers.setLocationList(locationList);

	}

	public void setupPolylines() {

		polylines = new Polylines();
		System.out.println(" PATH FOUND");
		LinkedList<Node> nodesList = new LinkedList<Node>();
		for (Node node : path.nodes()) {
			nodesList.add(node);
		}

		// Setup start And End walking lines
		Polyline startLine = new Polyline("#ff0000", 1.0, 2.0);
		startLine.addLatLng(nodeToLatLng(nodesList.removeFirst()));
		startLine.addLatLng(nodeToLatLng(nodesList.getFirst()));
		polylines.addPolyline(startLine);
		Polyline endLine = new Polyline("#ff0000", 1.0, 2.0);
		endLine.addLatLng(nodeToLatLng(nodesList.removeLast()));
		endLine.addLatLng(nodeToLatLng(nodesList.getLast()));
		polylines.addPolyline(endLine);

	
		Polyline routePolyline = new Polyline("3399FF", 1.0, 2.0);
		for (Node node : nodesList) {
			StopTime stopTime = new StopTime(node);
			routePolyline.addLatLng(stopTime.getLatLng());
			System.out.println("*: " + stopTime.getLatLng());
		}
		System.out.println("routePolyline: " + routePolyline);
		polylines.addPolyline(routePolyline);

		int nodeCounter = 0;
		StopTime prevStopTime = null;
		boolean newRoute = false;

		for (Node node : nodesList) {

			StopTime stopTime = new StopTime(node);
			System.out.println("count: " + nodeCounter + ",  stopTime: "
					+ stopTime.getStopID() + ",Time: " + stopTime.getTime());
			if (prevStopTime != null) {
				if (!stopTime.getStopID().substring(3, 6).equals(
						prevStopTime.getStopID().substring(3, 6))) {
					newRoute = true;
					System.out.println(" newRoute ! " + "stopTime"
							+ stopTime.getStopTimeID());
				}
			}
			prevStopTime = stopTime;
			nodeCounter++;
		}
	}
}