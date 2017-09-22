package bcccp.carpark;

import java.time.LocalTime;

// Used to assist with mocking current time.
public class TimeProvider implements ITimeProvider{

	public LocalTime getLocalTime() {
		return Utilities.toLocalDateTime(System.currentTimeMillis()).toLocalTime();
	}
}
