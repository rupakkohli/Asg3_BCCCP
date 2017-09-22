package test.bcccp.carpark;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import bcccp.carpark.ChargeCalculator;

public class ChargeCalculatorDatesTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static final double DELTA = 0.001;
	
	@Test
	public void testEndBeforeStart() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage("The exit date time must be after the entry date time.");
		// Start with a weekday.
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-18T03:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		ZonedDateTime endDateTime = startDateTime.minusMinutes(1);
		new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
				endDateTime.toInstant().toEpochMilli());
	}
	
	
	
	@Test
	public void testSameDay() {
		// Start with a weekday.
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-18T03:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		ZonedDateTime endDateTime = startDateTime.plusHours(12);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();
		
		float beforeHours = 4 * 60 * 2.0f / 60;
		float businessHours = 8 * 60 * 5.0f / 60;
		
		assertEquals(beforeHours + businessHours, charge, DELTA);
	}
	
	
	
	@Test
	public void testMultipleDays() {
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-18T03:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		
		ZonedDateTime endDateTime = startDateTime.plusHours(12).plusDays(3);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();
		
		float fullDayCharges = 2 * getOneDayBusinessCharge();
		float firstDay = (4 * 60 * 2.0f / 60) + (12 * 60 * 5.0f / 60) + (5 * 60 * 2.0f / 60 );
		float lastDay = (7 * 60 * 2.0f / 60) + (8 * 60 * 5.0f / 60);
		
		assertEquals(fullDayCharges + firstDay + lastDay, charge, DELTA);
	}
	
	
	
	@Test
	public void testWeekend() {
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-16T00:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		
		ZonedDateTime endDateTime = startDateTime.plusDays(2);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();

		float weekendCharge = 24 * 60 * 2 * 2.0f/60;
		assertEquals(weekendCharge, charge, DELTA);
	}
	
	
	
	@Test
	public void testWeekdays() {
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-18T00:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		
		ZonedDateTime endDateTime = startDateTime.plusDays(5);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();

		float weekdaysCharge = 5 * getOneDayBusinessCharge();
		assertEquals(weekdaysCharge, charge, DELTA);
	}
	
	
	
	@Test
	public void testFullWeek() {
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-18T00:00:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		
		ZonedDateTime endDateTime = startDateTime.plusDays(7);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();

		float weekdaysCharge = 5 * getOneDayBusinessCharge();
		float weekendCharge = 24 * 60 * 2 * 2.0f / 60;
		assertEquals(weekdaysCharge + weekendCharge, charge, DELTA);
	}
	
	
	
	@Test
	public void testAcrossWeekendBoundary() {
		ZonedDateTime startDateTime = ZonedDateTime.parse(
				"2017-09-15T05:45:00+10:00" ,
			    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()));
		
		ZonedDateTime endDateTime = startDateTime.plusDays(1).plusHours(7);
		
		ChargeCalculator calculator = new ChargeCalculator(startDateTime.toInstant().toEpochMilli(),
					endDateTime.toInstant().toEpochMilli());
		float charge = calculator.calcParkingCharge();

		float firstDay = (1.25f * 60 * 2.0f / 60) + (12 * 60 * 5.0f / 60) + (5 * 60 * 2.0f / 60 );
		float lastDay = 12.75f * 60 * 2.0f / 60;
		assertEquals(firstDay + lastDay, charge, DELTA);
	}
	
	
	
	private static float getOneDayBusinessCharge() {
		float beforeAfter = 12 * 60 * 2.0f / 60;
		float business = 12 * 60 * 5.0f / 60;
		return beforeAfter + business;
	}
	
}

