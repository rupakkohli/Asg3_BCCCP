package bcccp.carpark;

import java.time.LocalDateTime;

public interface ITimeProvider {

	// Gets the local time.
	LocalDateTime getLocalDateTime();
}
