package test.bcccp.carpark;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import bcccp.carpark.Carpark;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicketDAO;

import static org.mockito.Mockito.*;
public class CarparkTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private IAdhocTicketDAO adhocTicketMock_ = mock(IAdhocTicketDAO.class);
	private ISeasonTicketDAO seasonTicketMock_ = mock(ISeasonTicketDAO.class);
	
	@Test
	public void TestCarparkNameNull() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark(null, 1, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	@Test
	public void TestCarparkNameEmpty() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark("", 1, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	@Test
	public void TestCarparkNameEmptySpace() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark(" ", 1, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	@Test
	public void TestCarparkNegativeCapacity() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("capacity"));
		new Carpark("Name", -1, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	@Test
	public void TestCarparkZeroCapacity() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("capacity"));
		new Carpark("Name", 0, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	@Test
	public void TestGetName() {
		Carpark carpark = new Carpark("Name", 1, this.adhocTicketMock_, this.seasonTicketMock_);
		assertEquals("Name", carpark.getName());
	}
	
	@Test
	public void TestGetCapacity() {
		Carpark carpark = new Carpark("Name", 5, this.adhocTicketMock_, this.seasonTicketMock_);
		assertEquals(5, carpark.getCapacity());
	}
}
