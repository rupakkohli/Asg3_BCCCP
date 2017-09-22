package test.bcccp.carpark;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.Test;

import bcccp.carpark.ChargeCalculator;

public class ChargeCalculatorTest {
 
	// The allowable delta when comparing two doubles.
	private static final double DELTA = 0.001;
	
	@Test
	public void testOutOfHoursFullDay() {
		double outOfHours = ChargeCalculator.calcDayCharge(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, DayOfWeek.SATURDAY);
		assertEquals(outOfHours, 2 * 1440, DELTA);
	}
	
	@Test
	public void testOutOfHoursPartDay() {
		// 5 minutes, rounding down each time to nearest minute.
		double outOfHours = ChargeCalculator.calcDayCharge(LocalTime.of(1, 10, 27), LocalTime.of(19, 15, 17), DayOfWeek.SATURDAY);
		double minutesExpected = 18 * 60 + 5;
		assertEquals(outOfHours, minutesExpected * 2 , DELTA);
	}
	
	@Test
	public void testBeforeStartOfBusiness() {
		double beforeStart = ChargeCalculator.calcDayCharge(LocalTime.of(1,0,0), LocalTime.of(6, 59, 59), DayOfWeek.MONDAY);
		double minutes = 5 * 60 + 59;
		assertEquals(beforeStart, minutes * 2, DELTA);
	}
	
	@Test
	public void testAfterEndOfBusiness() {
		double beforeStart = ChargeCalculator.calcDayCharge(LocalTime.of(19, 0, 1), LocalTime.of(20, 0, 1), DayOfWeek.MONDAY);
		assertEquals(beforeStart, 60 * 2, DELTA);
	}
}
