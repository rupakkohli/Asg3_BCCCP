package bcccp.carpark;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class ChargeCalculator {
	
	private static final double OUT_OF_HOURS_RATE = 2.0;
	
	private static final double BUSINESS_HOURS_RATE = 5.0;
	
	private static final LocalTime START_BUSINESS = LocalTime.of(7,  0);
	
	private static final LocalTime END_BUSINESS_TIME = LocalTime.of(19, 0);
	
	private static final List<DayOfWeek> BUSINESS_DAYS =
			Arrays.asList(new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
					DayOfWeek.THURSDAY, DayOfWeek.FRIDAY});
	
	
	private static final double MINUTES_IN_DAY = 60 * 24;
	
	
	
	public ChargeCalculator(long entryDateTimeMillis, long exitDateTimeMillis) {
		LocalDateTime entryDateTime = toLocalDateTime(entryDateTimeMillis);
		LocalDateTime exitDateTime = toLocalDateTime(exitDateTimeMillis);	
	}
	
	
	
	// Calculate the charge for a single day.
	public static double calcDayCharge(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {	
		if (BUSINESS_DAYS.contains(dayOfWeek)) {
			return calcBusinessDayCharge(startTime, endTime);
		}
		else {
			return calcChargeBetweenTimes(startTime, endTime, OUT_OF_HOURS_RATE);
		}
	}
	
	
	
	private static double calcBusinessDayCharge(LocalTime startTime, LocalTime endTime) {
		if (endTime.isBefore(START_BUSINESS) || startTime.isAfter(END_BUSINESS_TIME)) {
			return calcChargeBetweenTimes(startTime, endTime, OUT_OF_HOURS_RATE);
		}
		
		if (isTimeOnOrAfter(startTime, START_BUSINESS) && isTimeOnOrBefore(endTime, END_BUSINESS_TIME)) {
			return calcChargeBetweenTimes(startTime, endTime, BUSINESS_HOURS_RATE);
		}
		
		
		
		return 0;
	}
	
	
	
	private static boolean isTimeOnOrAfter(LocalTime first, LocalTime second) {
		return first.equals(second) || first.isAfter(second);
	}
	
	
	
	private static boolean isTimeOnOrBefore(LocalTime first, LocalTime second) {
		return first.equals(second) || first.isBefore(second);
	}
	
	
	private static double calcChargeBetweenTimes(LocalTime startTime, LocalTime endTime, double charge) {
		
		// For the entire day
		if (startTime.equals(LocalTime.MIDNIGHT) && endTime.equals(LocalTime.MIDNIGHT)) {
			return MINUTES_IN_DAY * charge;
		}

		Long minutesBetween = minutesBetweenRounded(startTime, endTime);
		return minutesBetween * charge;
	}
	
	
	
	private static long minutesBetweenRounded(LocalTime startTime, LocalTime endTime) {
		LocalTime startTimeRounded = startTime.withSecond(0);
		LocalTime endTimeRounded = endTime.withSecond(0);	
		return Duration.between(startTimeRounded, endTimeRounded).toMinutes();
	}
	
	
	
	private static LocalDateTime toLocalDateTime(long millis) {
		return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	
	
}
