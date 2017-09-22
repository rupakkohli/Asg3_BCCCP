package test.bcccp.carpark;

import bcccp.carpark.Carpark;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationController.STATE;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
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
	
	IAdhocTicketDAO adhocTicketMock_;
	

	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		mPUI = mock(PaystationUI.class);
		mCarpark = mock(Carpark.class);
		mPController = new PaystationController(mCarpark, mPUI);
		adhocTicketMock_ = mock(IAdhocTicketDAO.class);
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
	public void testConstructor() {
		// Check that initialization state is IDLE
		assertEquals(mPController.getState(), STATE.IDLE);
	}
	
	
	@Test 
	public void testTicketInsertedRejected() {
		mPController.setState(STATE.IDLE);
		mPController.ticketInserted("adsf1234"); // should be null
		assertEquals(mPController.getState(), STATE.REJECTED);
	}
	
	
	@Test
	public void testTicketTakenWaiting() {
		mPController.setState(STATE.WAITING);
		mPController.ticketTaken();
		assertEquals(mPController.getState(), STATE.IDLE);

	}
	
	@Test
	public void testTicketTakenRejected() {
		mPController.setState(STATE.REJECTED);
		mPController.ticketTaken();
		assertEquals(mPController.getState(), STATE.IDLE);
	}
	
	@Test
	public void testTicketTakenPaid() {
		mPController.setState(STATE.PAID);
		mPController.ticketTaken();
		assertEquals(mPController.getState(), STATE.IDLE);
	}
	
	@Test
	public void testTicketTakenIdle() {
		mPController.setState(STATE.IDLE);
		mPController.ticketTaken();
		assertEquals(mPController.getState(), STATE.IDLE);

	}
	
		
}
