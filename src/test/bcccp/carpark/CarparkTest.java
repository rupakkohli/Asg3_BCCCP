package test.bcccp.carpark;

import org.junit.Test;

import bcccp.carpark.Carpark;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicketDAO;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class CarparkTest {

	private IAdhocTicketDAO adhocTicketMock_ = mock(IAdhocTicketDAO.class);
	private ISeasonTicketDAO seasonTicketMock_ = mock(ISeasonTicketDAO.class);
	
	@Test(expected=RuntimeException.class)
	public void TestCarparkNameNull() {
		new Carpark(null, 1, this.adhocTicketMock_, this.seasonTicketMock_);
		fail();
	}
	
	@Test(expected=RuntimeException.class)
	public void TestCarparkNameEmpty() {
		new Carpark("", 1, this.adhocTicketMock_, this.seasonTicketMock_);
		fail();
	}
	
	@Test(expected=RuntimeException.class)
	public void TestCarparkNameEmptySpace() {
		new Carpark(" ", 1, this.adhocTicketMock_, this.seasonTicketMock_);
		fail();
	}
}
