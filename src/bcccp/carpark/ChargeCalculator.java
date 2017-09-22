package bcccp.carpark;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class ChargeCalculator {
	
	private static final double MIN_IN_HOUR = 60;
	
	private static final double AFTER_HOURS_RATE_PER_MIN = 2.0 / MIN_IN_HOUR;
	
	private static final double BUSINESS_HOURS_RATE_PER_MIN = 5.0 / MIN_IN_HOUR;
	
	private static final LocalTime START_BUSINESS_TIME = LocalTime.of(7,  0);
	
	private static final LocalTime END_BUSINESS_TIME = LocalTime.of(19, 0);
	
	private static final List<DayOfWeek> BUSINESS_DAYS =
			Arrays.asList(new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
					DayOfWeek.THURSDAY, DayOfWeek.FRIDAY});
	
	
	private static final double MINUTES_IN_DAY = 60 * 24;

	private LocalDateTime entryDateTime;

	private LocalDateTime exitDateTime;
	
	
	
	public ChargeCalculator(long entryDateTimeMillis, long exitDateTimeMillis) {
		LocalDateTime entryDateTime = toLocalDateTime(entryDateTimeMillis);
		LocalDateTime exitDateTime = toLocalDateTime(exitDateTimeMillis);
		if (exitDateTime.isBefore(entryDateTime)) {
			throw new RuntimeException("The exit date time must be after the entry date time.");
		}
		
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;		
	}
	
	
	public double calcParkingCharge() {
		LocalDate entryDate = this.entryDateTime.toLocalDate();
		LocalDate exitDate = this.exitDateTime.toLocalDate();
		LocalDate currentDate = entryDate;
		LocalTime currentStartTime = this.entryDateTime.toLocalTime();
		LocalTime currentEndTime = LocalTime.MIDNIGHT;
		
		double charge = 0;
		while(!currentDate.equals(exitDate)) {
			charge += calcDayCharge(currentStartTime, currentEndTime, currentDate.getDayOfWeek());
			currentStartTime = currentEndTime;
			currentDate = currentDate.plusDays(1);
		}
		
		charge += calcDayCharge(currentStartTime, this.exitDateTime.toLocalTime(), currentDate.getDayOfWeek());
		return charge;
	}
	
	
	
	// Calculate the charge for a single day.
	public static double calcDayCharge(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {	
		
		if (!areTimesValid(startTime, endTime)) {
			throw new RuntimeException("The end time should be after the start time.");
		}
		
		return BUSINESS_DAYS.contains(dayOfWeek) ? calcBusinessDayCharge(startTime, endTime)
					: calcChargeBetweenTimes(startTime, endTime, AFTER_HOURS_RATE_PER_MIN);
	}
	
	
	// Check whether a start and end time are correct.
	private static boolean areTimesValid(LocalTime startTime, LocalTime endTime) {
		
		// Whole day is correct.
		// Any time until midnight is correct.
		if (endTime.equals(LocalTime.MIDNIGHT)) {
			return true;
		}

		return endTime.isAfter(startTime);
	}
	
	
	
	private static double calcBusinessDayCharge(LocalTime startTime, LocalTime endTime) {
				
		if ((!endTime.equals(LocalTime.MIDNIGHT) && endTime.isBefore(START_BUSINESS_TIME)) || startTime.isAfter(END_BUSINESS_TIME)) {
			return calcChargeBetweenTimes(startTime, endTime, AFTER_HOURS_RATE_PER_MIN);
		}
		
		if (isTimeOnOrAfter(startTime, START_BUSINESS_TIME) && isTimeOnOrBefore(endTime, END_BUSINESS_TIME)) {
			return calcChargeBetweenTimes(startTime, endTime, BUSINESS_HOURS_RATE_PER_MIN);
		}
		
		if (startTime.isBefore(START_BUSINESS_TIME) && isTimeOnOrBefore(endTime, END_BUSINESS_TIME)) {
			double outOfHours = calcChargeBetweenTimes(startTime, START_BUSINESS_TIME, AFTER_HOURS_RATE_PER_MIN);
			double businessHours = calcChargeBetweenTimes(START_BUSINESS_TIME, endTime, BUSINESS_HOURS_RATE_PER_MIN);
			return outOfHours + businessHours;
		}
		
		if (isTimeOnOrAfter(startTime, START_BUSINESS_TIME) && endTime.isAfter(END_BUSINESS_TIME)) {
			double businessHours = calcChargeBetweenTimes(startTime, END_BUSINESS_TIME, BUSINESS_HOURS_RATE_PER_MIN);
			double outOfHours = calcChargeBetweenTimes(END_BUSINESS_TIME, endTime, AFTER_HOURS_RATE_PER_MIN);
			return businessHours + outOfHours;
		}
		
		// Start time is before and end time after; general case
		double businessHours = calcChargeBetweenTimes(START_BUSINESS_TIME, END_BUSINESS_TIME, BUSINESS_HOURS_RATE_PER_MIN);
		double beforeHours = calcChargeBetweenTimes(startTime, START_BUSINESS_TIME, AFTER_HOURS_RATE_PER_MIN);
		double afterHours = calcChargeBetweenTimes(END_BUSINESS_TIME, endTime, AFTER_HOURS_RATE_PER_MIN);
		return beforeHours + businessHours + afterHours;
	}
	
	
	
	private static boolean isTimeOnOrAfter(LocalTime first, LocalTime second) {
		return first.equals(second) || first.isAfter(second);
	}
	
	
	
	// Midnight is considered after a time.
	private static boolean isTimeOnOrBefore(LocalTime first, LocalTime second) {
		return first != LocalTime.MIDNIGHT && (first.equals(second) || first.isBefore(second));
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
		
		if (endTime.equals(LocalTime.MIDNIGHT)) {
			startTimeRounded = startTimeRounded.withHour(24 - startTimeRounded.getHour());
			return Duration.between(endTimeRounded, startTimeRounded).toMinutes();
		}

		return Duration.between(startTimeRounded, endTimeRounded).toMinutes();
	}
	
	
	
	private static LocalDateTime toLocalDateTime(long millis) {
		return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	
	
}
