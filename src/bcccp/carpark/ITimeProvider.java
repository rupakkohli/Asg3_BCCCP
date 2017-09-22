package bcccp.carpark;

import java.time.LocalTime;

public interface ITimeProvider {

	// Gets the local time.
	LocalTime getLocalTime();
}
