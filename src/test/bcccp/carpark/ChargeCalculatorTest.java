package test.bcccp.carpark;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import org.junit.rules.ExpectedException;

import bcccp.carpark.ChargeCalculator;

public class ChargeCalculatorTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
 
	// The allowable delta when comparing two doubles.
	private static final double DELTA = 0.001;
	
	
	@Test
	public void testIncorrectTimes() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("end time"));
		ChargeCalculator.calcDayCharge(LocalTime.of(3, 4), LocalTime.of(1, 2), DayOfWeek.SATURDAY);
	}
	
	
	@Test
	public void testOutOfHoursFullDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, DayOfWeek.SATURDAY);
		assertEquals(2 * 1440, charge, DELTA);
	}
	
	
	
	@Test
	public void testOutOfHoursPartDay() {
		// 5 minutes, rounding down each time to nearest minute.
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(1, 10, 27), LocalTime.of(19, 15, 17), DayOfWeek.SATURDAY);
		double minutesExpected = 18 * 60 + 5;
		assertEquals(minutesExpected * 2 , charge, DELTA);
	}
	
	
	
	@Test
	public void testBeforeStartOfBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(1,0,0), LocalTime.of(6, 59, 59), DayOfWeek.MONDAY);
		double minutes = 5 * 60 + 59;
		assertEquals(minutes * 2, charge, DELTA);
	}
	
	
	
	@Test
	public void testAfterEndOfBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(19, 0, 1), LocalTime.of(20, 0, 1), DayOfWeek.MONDAY);
		assertEquals(60 * 2, charge,DELTA);
	}
	
	
	@Test
	public void testExactlyDuringBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(7, 0, 0), LocalTime.of(19,  0, 0), DayOfWeek.MONDAY);
		assertEquals(12 * 60 * 5, charge, DELTA);
	}
	
	
	
	@Test
	public void testDuringBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(8, 5, 19), LocalTime.of(14, 3, 27), DayOfWeek.MONDAY);
		assertEquals((6 * 60 - 2) * 5, charge, DELTA);
	}
	
	
	@Test
	public void testStartBeforeEndBeforeBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(6, 45, 59), LocalTime.of(18, 0, 0), DayOfWeek.MONDAY);
		double outOfHours = 15 * 2;
		double business = 11 * 60 * 5;
		assertEquals(outOfHours + business, charge, DELTA);
	}
	
	
	
	@Test
	public void testStartAfterEndAfterBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(7, 45, 32), LocalTime.of(20, 32, 19), DayOfWeek.MONDAY);
		double outOfHours = (1 * 60 + 32) * 2;
		double business = (11 * 60 + 15) * 5;
		assertEquals(outOfHours + business, charge, DELTA);
	}
	
	
	
	@Test
	public void testStartBeforeEndAfterBusinessDay() {
		double charge = ChargeCalculator.calcDayCharge(LocalTime.of(4, 0, 3), LocalTime.of(23, 0, 0), DayOfWeek.MONDAY);
		double outOfHours = (3 * 60 * 2) + (4 * 60 * 2);
		double businessHours = 12 * 60 * 5;
		assertEquals(outOfHours + businessHours, charge, DELTA);
	}
	
	
	
	@Test
	public void testStartBeforeEndMidnightBusinessDay() {
		double charge = ChargeCalculator.DayCharge(LocalTime.of(4, 0, 3), LocalTime.MIDNIGHT, DayOfWeek.MONDAY)
	}
	
	
}
