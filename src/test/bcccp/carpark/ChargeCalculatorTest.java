package test.bcccp.carpark;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.Test;

import bcccp.carpark.ChargeCalculator;

public class ChargeCalculatorTest {
	
	@Test
	public void testOutOfHoursFullDay() {
		double outOfHours = ChargeCalculator.calcDayCharge(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, DayOfWeek.SATURDAY);
		assertEquals(outOfHours, 2 * 1440, 0.01);
	}
	
	@Test
	public void testOutOfHoursPartDay() {
		// 4 minutes, rounding down
		double outOfHours = ChargeCalculator.calcDayCharge(LocalTime.of(1, 10, 27), LocalTime.of(19, 15, 17), DayOfWeek.SATURDAY);
		double minutesExpected = 18 * 60 + 4;
		assertEquals(outOfHours, minutesExpected * 2 , 0.01);
	}
	
}
