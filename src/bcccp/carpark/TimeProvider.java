package bcccp.carpark;

import java.time.LocalDateTime;

// Used to assist with mocking current time.
public class TimeProvider implements ITimeProvider{

	public LocalDateTime getLocalDateTime() {
		return Utilities.toLocalDateTime(System.currentTimeMillis());
	}
}
