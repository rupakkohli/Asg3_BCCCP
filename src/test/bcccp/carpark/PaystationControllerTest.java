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
	
	private static PaystationController mPController_;
	private static Carpark mCarpark_;
	private static PaystationUI mPUI_;
	
	

	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		mPUI_ = mock(PaystationUI.class);
		mCarpark_ = mock(Carpark.class);
		mPController_ = new PaystationController(mCarpark_, mPUI_);
	}
	
	
	
	@Test
	public void PayStationControllerUINull()
	{
		 expectedException.expect(RuntimeException.class);
		 expectedException.expectMessage(containsString("UI"));
		 new PaystationController(mCarpark_, null);
	}
	
	
	
	@Test
	public void PayStationControllerCarparkNull() 
	{
		 expectedException.expect(RuntimeException.class);
		 expectedException.expectMessage(containsString("Carpark"));
		 new PaystationController(null, mPUI_);
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
		assertEquals(mPController_.getState(), STATE.IDLE);
	}
	
	
	
	@Test 
	public void testTicketInsertedRejected() {
		mPController_.setState(STATE.IDLE);
		mPController_.ticketInserted("adsf1234"); // should be null
		assertEquals(mPController_.getState(), STATE.REJECTED);
	}
	
	
	
	@Test
	public void testTicketTakenWaiting() {
		mPController_.setState(STATE.WAITING);
		mPController_.ticketTaken();
		assertEquals(mPController_.getState(), STATE.IDLE);

	}
	
	
	
	@Test
	public void testTicketTakenRejected() {
		mPController_.setState(STATE.REJECTED);
		mPController_.ticketTaken();
		assertEquals(mPController_.getState(), STATE.IDLE);
	}
	
	
	
	@Test
	public void testTicketTakenPaid() {
		mPController_.setState(STATE.PAID);
		mPController_.ticketTaken();
		assertEquals(mPController_.getState(), STATE.IDLE);
	}
	
	
	
	@Test
	public void testTicketTakenIdle() {
		mPController_.setState(STATE.IDLE);
		mPController_.ticketTaken();
		assertEquals(mPController_.getState(), STATE.IDLE);

	}
	
		
	
}
