package test.bcccp.carpark;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarparkObserver;
import bcccp.carpark.ITimeProvider;
import bcccp.tickets.adhoc.IAdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicketDAO;

import static org.mockito.Mockito.*;

public class CarparkTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private IAdhocTicketDAO adhocTicketDaoMock_ = mock(IAdhocTicketDAO.class);
	private ISeasonTicketDAO seasonTicketDaoMock_ = mock(ISeasonTicketDAO.class);
	private ITimeProvider timeProvider_ = mock(ITimeProvider.class);
	
	@Test
	public void testCarparkNameNull() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark(null, 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
	}
	
	
	
	@Test
	public void testCarparkNameEmpty() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark("", 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
	}
	
	
	
	@Test
	public void testCarparkNameEmptySpace() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("name"));
		new Carpark(" ", 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
	}
	
	
	
	@Test
	public void testCarparkNegativeCapacity() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("capacity"));
		new Carpark("Name", -1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
	}
	
	
	
	@Test
	public void testCarparkZeroCapacity() {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("capacity"));
		new Carpark("Name", 0, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
	}
	
	
	
	@Test
	public void testGetName() {
		Carpark carpark = new Carpark("Name", 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
		assertEquals("Name", carpark.getName());
	}
	
	
	
	@Test
	public void testIsFull() {
		Carpark carpark = new Carpark("Name", 2, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
		// Should not be full at the start
		assertFalse(carpark.isFull());
		
		// Should not be full until capacity is reached
		carpark.recordAdhocTicketEntry();
		assertFalse(carpark.isFull());
		
		// Should be full at this point.
		carpark.recordAdhocTicketEntry();
		assertTrue(carpark.isFull());
	}
	
	
	
	@Test
	public void testIssueAdhocTicketWhenFull() {
		Carpark carpark = new Carpark("Name", 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
		carpark.recordAdhocTicketEntry();
		
		// Carpark is now full. Should not issue ticket.
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(containsString("full"));
		IAdhocTicket ticket = carpark.issueAdhocTicket();
		ticket.isCurrent();
	}
	
	
	
	@Test
	public void testGetValidAdhocTicket() {
		IAdhocTicket adhocTicket = mock(IAdhocTicket.class);
		when(adhocTicket.getBarcode()).thenReturn("barcode");
	
		IAdhocTicketDAO adhocTicketDaoMock = mock(IAdhocTicketDAO.class);
		when(adhocTicketDaoMock.findTicketByBarcode("barcode")).thenReturn(adhocTicket);
		
		Carpark carpark = new Carpark("carpark", 2, adhocTicketDaoMock, this.seasonTicketDaoMock_, timeProvider_);
		IAdhocTicket result = carpark.getAdhocTicket("barcode");
		assertEquals(result.getBarcode(), "barcode");
	}
	
	
	
	@Test
	public void testRecordAdhocTicketExit() {
		Carpark carpark = new Carpark("Name", 1, this.adhocTicketDaoMock_, this.seasonTicketDaoMock_, timeProvider_);
		ICarparkObserver observerMock = mock(ICarparkObserver.class);
		carpark.register(observerMock);
		
		carpark.recordAdhocTicketEntry();
		assertTrue(carpark.isFull());
		carpark.recordAdhocTicketEntry();
		carpark.recordAdhocTicketExit();
		
		// The notify carpark event should be called 
		verify(observerMock).notifyCarparkEvent();
	}
}
