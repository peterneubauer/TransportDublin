package ie.transportdublin.setupgraph;

import ie.transportdublin.datastructure.mysql.BusJourney;
import ie.transportdublin.datastructure.mysql.Stop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.TreeMap;

import org.joda.time.DateTime;


public class DatabaseHelper {
	static Connection con;

	/**
	 * Reads Stops Database and returns TreeMap containing a list of Stops
	 * objects
	 * 
	 * @return Arraylist containing all stops
	 */
	public static TreeMap<String, Stop> readAllStops() {
		TreeMap<String, Stop> stopList = new TreeMap<String, Stop>();

		try {
			long startGetNeighboursTime = System.nanoTime();
			long elapsedNeighboursTime = 0;
			// setupDB();

			PreparedStatement pstmt = con
					.prepareStatement("SELECT * FROM Stops");
			ResultSet rs = pstmt.executeQuery();
			// For each stop Create Node

			while (rs.next()) {
				int id = rs.getInt("ID");
				String routeID = rs.getString("ROUTEID");
				String stopID = rs.getString("STOPID");
				String stopName = rs.getString("STOPNAME");
				Double lat = rs.getDouble("LAT");
				Double lng = rs.getDouble("LNG");
				Stop stop = new Stop(id, routeID, stopID, stopName, lat, lng);
				stopList.put(stopID, stop);

			}

			elapsedNeighboursTime = elapsedNeighboursTime
					+ (System.nanoTime() - startGetNeighboursTime);
			System.out.println("readAllStops : " + (elapsedNeighboursTime)
					/ 10000000 + "ms");
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stopList;

	}

	/**
	 * Selects all values from the StopTimes DB to Populate A TreeMap<String,
	 * ArrayList<Integer>>
	 * 
	 * @return Hashmap of Connections
	 */
	public static TreeMap<String, ArrayList<Integer>> readAllStopTimes() {
		long startGetNeighboursTime = System.nanoTime();
		long elapsedNeighboursTime = 0;

		TreeMap<String, ArrayList<Integer>> StopTimesList = new TreeMap<String, ArrayList<Integer>>();
		try {
			// setupDB();
			PreparedStatement pstmt = con
					.prepareStatement("select * from stoptimes");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<Integer> listOfTimes;
			// Read all StopTimes from DB
			while (rs.next()) {

				int routeSQL = rs.getInt("ROUTESQL");
				String routeSQLString = Integer.toString(routeSQL);
				int stopIndex = rs.getInt("STOPINDEX");
				int time = rs.getInt("TIME");

				// stopConnections contains the stop add to list of connections
				if (StopTimesList.containsKey(routeSQLString)) {
					StopTimesList.get(routeSQLString).add(time);

				} else {
					// create a list of connections and add the stop to list of
					// connections
					listOfTimes = new ArrayList<Integer>();
					listOfTimes.add(time);
					StopTimesList.put(routeSQLString, listOfTimes);

				}
			}
			rs.close();
			// con.close();
			pstmt.close();
			elapsedNeighboursTime = elapsedNeighboursTime
					+ (System.nanoTime() - startGetNeighboursTime);
			System.out.println("readAllStopTimes: " + (elapsedNeighboursTime)
					/ 10000000 + "ms");

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return StopTimesList;

	}

	/**
	 * Selects all values from the Timetable DB to Populate A TreeMap<String,
	 * ArrayList<BusJourney>>
	 * 
	 * @return Hashmap of Connections
	 */
	public static TreeMap<String, ArrayList<BusJourney>> readTimetable() {
		long startGetNeighboursTime = System.nanoTime();
		long elapsedNeighboursTime = 0;
		// [MON1101301,{08:24, 08:48, 09:15, 09:45, 10:15, 10:45, 11:15, ....
		TreeMap<String, ArrayList<BusJourney>> StopTimetableList = new TreeMap<String, ArrayList<BusJourney>>();
		try {
			// setupDB();
			PreparedStatement pstmt = con
					.prepareStatement("select * from timetable");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<BusJourney> busJourneysList;
			// Read all StopTimes from DB
			// 1100200 day:1 time:08:00:00
			int counter = 0;
			while (rs.next()) {

				int routeSQL = rs.getInt("ROUTESQL");
				String routeSQLString = Integer.toString(routeSQL);

				int runNumber = rs.getInt("RUNNUMBER");
				// 1=Mon,2=Sat, 3=Sun
				int day = rs.getInt("DAY");

				Time time = rs.getTime("DEPTTIME");
				DateTime datetime = new DateTime(time);
				ie.transportdublin.datastructure.mysql.BusJourney busJourney = new BusJourney(day,
						datetime);

				// stopConnections contains the stop add to list of connections
				if (StopTimetableList.containsKey(routeSQLString)) {
					StopTimetableList.get(routeSQLString).add(busJourney);
				} else {
					// create a list of connections and add the stop to list of
					// connections
					counter++;
					busJourneysList = new ArrayList<BusJourney>();

					busJourneysList.add(busJourney);
					StopTimetableList.put(routeSQLString, busJourneysList);

				}
			}
			rs.close();
			// con.close();
			pstmt.close();
			elapsedNeighboursTime = elapsedNeighboursTime
					+ (System.nanoTime() - startGetNeighboursTime);
			System.out.println("readTimetable: " + (elapsedNeighboursTime)
					/ 10000000 + "ms");

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return StopTimetableList;

	}

	/**
	 * Sets up Connection to Database.
	 */
	public static void setupDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/neo4j";
			// insert mysql password here
			con = DriverManager.getConnection(url, "root", "8217878");
			System.out.println("***************database connection 1_+");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
