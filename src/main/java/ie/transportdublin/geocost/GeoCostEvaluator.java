package ie.transportdublin.geocost;

public class GeoCostEvaluator {
	private final double RADIUS_EARTH = 6371 * 1000; // Meters

	public double distance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		latitude1 = Math.toRadians(latitude1);
		longitude1 = Math.toRadians(longitude1);
		latitude2 = Math.toRadians(latitude2);
		longitude2 = Math.toRadians(longitude2);
		double cLa1 = Math.cos(latitude1);
		double x_A = RADIUS_EARTH * cLa1 * Math.cos(longitude1);
		double y_A = RADIUS_EARTH * cLa1 * Math.sin(longitude1);
		double z_A = RADIUS_EARTH * Math.sin(latitude1);
		double cLa2 = Math.cos(latitude2);
		double x_B = RADIUS_EARTH * cLa2 * Math.cos(longitude2);
		double y_B = RADIUS_EARTH * cLa2 * Math.sin(longitude2);
		double z_B = RADIUS_EARTH * Math.sin(latitude2);
		return Math.sqrt((x_A - x_B) * (x_A - x_B) + (y_A - y_B) * (y_A - y_B)
				+ (z_A - z_B) * (z_A - z_B));
	}

}
