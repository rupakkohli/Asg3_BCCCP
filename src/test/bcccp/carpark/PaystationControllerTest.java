package test.bcccp.carpark;

import bcccp.carpark.Carpark;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationController.STATE;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicketDAO;
import bcccp.carpark.paystation.PaystationUI;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;


public class PaystationControllerTest {
	private static PaystationController mPController;
	private static Carpark mCarpark;
	private static PaystationUI mPUI;
	
	private IAdhocTicketDAO adhocTicketMock_ = mock(IAdhocTicketDAO.class);
	private ISeasonTicketDAO seasonTicketMock_ = mock(ISeasonTicketDAO.class);
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		mPUI = mock(PaystationUI.class);
		mCarpark = new Carpark("Carpark", 10, this.adhocTicketMock_, this.seasonTicketMock_);
	}
	
	
	@Test
	public void PayStationControllerUINull()
	{
		 expectedException.expect(RuntimeException.class);
		 expectedException.expectMessage(containsString("UI"));
		 new PaystationController(mCarpark, null);
	}
	
	
	@Test
	public void PayStationControllerCarparkNull() 
	{
		 expectedException.expect(RuntimeException.class);
		 expectedException.expectMessage(containsString("Carpark"));
		 new PaystationController(null, mPUI);
	}
	
	@Test
	public void PayStationControllerBothNull() {
		 expectedException.expect(RuntimeException.class);
		 expectedException.expectMessage(containsString("Both"));
		 new PaystationController(null, null);
	}
	
	
	
	
	@Test 
	public void testWaiting() {
		mPController = new PaystationController(mCarpark, mPUI);
		mPController.setState(STATE.IDLE);
		mPController.ticketInserted("");
		assertEquals(mPController.getState(), STATE.WAITING);
	}
		
}
