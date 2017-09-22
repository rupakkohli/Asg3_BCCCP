package bcccp.carpark;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ChargeCalculator{
	
	private LocalDateTime entryDateTime;

	private LocalDateTime exitDateTime;
	
	public ChargeCalculator(long entryDateTimeMillis, long exitDateTimeMillis) {
		LocalDateTime entryDateTime = Utilities.toLocalDateTime(entryDateTimeMillis);
		LocalDateTime exitDateTime = Utilities.toLocalDateTime(exitDateTimeMillis);
		if (exitDateTime.isBefore(entryDateTime)) {
			throw new RuntimeException("The exit date time must be after the entry date time.");
		}
		
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;		
	}
	
	
	
	public float calcParkingCharge() {
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
		
		if (currentStartTime.equals(this.exitDateTime.toLocalTime())) {
			return (float) charge;
		}
		
		charge += calcDayCharge(currentStartTime, this.exitDateTime.toLocalTime(), currentDate.getDayOfWeek());
		return (float) charge;
	}
	
	
	
	// Calculate the charge for a single day.
	public static double calcDayCharge(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {	
		
		if (!areTimesValid(startTime, endTime)) {
			throw new RuntimeException("The end time should be after the start time.");
		}
		
		return Constants.BUSINESS_DAYS.contains(dayOfWeek) ? calcBusinessDayCharge(startTime, endTime)
					: calcChargeBetweenTimes(startTime, endTime, Constants.AFTER_HOURS_RATE_PER_MIN);
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
				
		if ((!endTime.equals(LocalTime.MIDNIGHT) && endTime.isBefore(Constants.START_BUSINESS_TIME)) || startTime.isAfter(Constants.END_BUSINESS_TIME)) {
			return calcChargeBetweenTimes(startTime, endTime, Constants.AFTER_HOURS_RATE_PER_MIN);
		}
		
		if (Utilities.isTimeOnOrAfter(startTime, Constants.START_BUSINESS_TIME) 
				&& Utilities.isTimeOnOrBefore(endTime, Constants.END_BUSINESS_TIME)) {
			
			return calcChargeBetweenTimes(startTime, endTime, Constants.BUSINESS_HOURS_RATE_PER_MIN);
		}
		
		if (startTime.isBefore(Constants.START_BUSINESS_TIME) 
				&& Utilities.isTimeOnOrBefore(endTime, Constants.END_BUSINESS_TIME)) {
			
			double outOfHours = calcChargeBetweenTimes(startTime, Constants.START_BUSINESS_TIME, Constants.AFTER_HOURS_RATE_PER_MIN);
			double businessHours = calcChargeBetweenTimes(Constants.START_BUSINESS_TIME, endTime, Constants.BUSINESS_HOURS_RATE_PER_MIN);
			return outOfHours + businessHours;
		}
		
		if (Utilities.isTimeOnOrAfter(startTime, Constants.START_BUSINESS_TIME) && 
				endTime.isAfter(Constants.END_BUSINESS_TIME)) {
			
			double businessHours = calcChargeBetweenTimes(startTime, Constants.END_BUSINESS_TIME, Constants.BUSINESS_HOURS_RATE_PER_MIN);
			double outOfHours = calcChargeBetweenTimes(Constants.END_BUSINESS_TIME, endTime, Constants.AFTER_HOURS_RATE_PER_MIN);
			return businessHours + outOfHours;
		}
		
		// Start time is before and end time after; general case
		double businessHours = calcChargeBetweenTimes(Constants.START_BUSINESS_TIME, Constants.END_BUSINESS_TIME, Constants.BUSINESS_HOURS_RATE_PER_MIN);
		double beforeHours = calcChargeBetweenTimes(startTime, Constants.START_BUSINESS_TIME, Constants.AFTER_HOURS_RATE_PER_MIN);
		double afterHours = calcChargeBetweenTimes(Constants.END_BUSINESS_TIME, endTime, Constants.AFTER_HOURS_RATE_PER_MIN);
		return beforeHours + businessHours + afterHours;
	}
	
	
	
	private static double calcChargeBetweenTimes(LocalTime startTime, LocalTime endTime, double charge) {
		
		// For the entire day
		if (startTime.equals(LocalTime.MIDNIGHT) && endTime.equals(LocalTime.MIDNIGHT)) {
			return Constants.MINUTES_IN_DAY * charge;
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
}
