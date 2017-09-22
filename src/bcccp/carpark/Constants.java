package bcccp.carpark;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final double MIN_IN_HOUR = 60;

	public static final double MINUTES_IN_DAY = MIN_IN_HOUR * 24;
	
	public static final double AFTER_HOURS_RATE_PER_MIN = 2.0 / MIN_IN_HOUR;

	public static final double BUSINESS_HOURS_RATE_PER_MIN = 5.0 / MIN_IN_HOUR;
	
	public static final LocalTime START_BUSINESS_TIME = LocalTime.of(7,  0);
	
	public static final LocalTime END_BUSINESS_TIME = LocalTime.of(19, 0);
	
	public static final List<DayOfWeek> BUSINESS_DAYS =
			Arrays.asList(new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
					DayOfWeek.THURSDAY, DayOfWeek.FRIDAY});
}
