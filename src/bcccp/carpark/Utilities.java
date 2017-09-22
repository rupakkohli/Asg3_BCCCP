package bcccp.carpark;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class Utilities {
	
	public static LocalDateTime toLocalDateTime(long millis) {
		return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	
	
	public static boolean isTimeOnOrAfter(LocalTime first, LocalTime second) {
		return first.equals(second) || first.isAfter(second);
	}
	
	
	
	// Midnight is considered after a time.
	public static boolean isTimeOnOrBefore(LocalTime first, LocalTime second) {
		return first != LocalTime.MIDNIGHT && (first.equals(second) || first.isBefore(second));
	}
}
