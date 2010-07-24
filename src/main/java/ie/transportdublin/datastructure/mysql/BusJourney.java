package ie.transportdublin.datastructure.mysql;

import org.joda.time.DateTime;

public class BusJourney {

	int day;
	DateTime time;

	public BusJourney(int day, DateTime time) {
		super();
		this.day = day;
		this.time = time;
	}

	public int getDay() {
		return day;
	}

	public DateTime getTime() {
		return time;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

}
