package test.bcccp.carpark.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import bcccp.carpark.Carpark;
import bcccp.carpark.TimeProvider;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.carpark.paystation.PaystationController.STATE;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.SeasonTicket;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;

public class PaystationIntegrationTest {

	private Carpark myCarpark_;
	private AdhocTicketFactory myFactory_;
	private AdhocTicketDAO myAdhocDAO_;
	private SeasonTicketDAO mySeasonDAO_;
	private UsageRecordFactory myUsageFactory_;

	private PaystationUI myPUI_;
	private PaystationController myController_;
	private TimeProvider mTimeProvider_;
	
	@Before
	public void setUp() throws Exception {
		// Setting up AdhocDAO, AdhocTicketFactory
		myFactory_ = new AdhocTicketFactory();
		myAdhocDAO_ = new AdhocTicketDAO(myFactory_);
		
		myUsageFactory_ = new UsageRecordFactory();
		mySeasonDAO_ = new SeasonTicketDAO(myUsageFactory_);
		mTimeProvider_ = mock(TimeProvider.class);
		
	}
	
	
	
	@Test
	public void TestPaystationControllerIntegration(){
		
		// Setup carpark, UI and Controller
		
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		myPUI_ = new PaystationUI(0,0);
		myController_ = new PaystationController(myCarpark_, myPUI_);
		
		// First ticket created should be A1
		myCarpark_.issueAdhocTicket();
		myController_.ticketInserted("A1");
		
		// Test full state diagram from idle to paid to idle
		assertEquals(myController_.getState(), PaystationController.STATE.WAITING);
		myController_.ticketPaid();
		assertEquals(myController_.getState(), PaystationController.STATE.PAID);	
		myController_.ticketTaken();
		assertEquals(myController_.getState(), PaystationController.STATE.IDLE);

	}
	
	

	@Test
	public void TestPaystationControllerRejection(){
		// Setup carpark, UI and Controller
		
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		myPUI_ = new PaystationUI(0,0);
		myController_ = new PaystationController(myCarpark_, myPUI_);
		
		// First ticket created should be A1
		myCarpark_.issueAdhocTicket();
		
		//Test an invalid ticket
		
		myController_.ticketInserted("A3"); // Hasn't been created yet so invalid
		
		assertEquals(myController_.getState(), PaystationController.STATE.REJECTED);
		assertEquals(myPUI_.getDisplayText(), "Take Rejected Ticket");
		myController_.ticketTaken();
		assertEquals(myController_.getState(), PaystationController.STATE.IDLE);
		

	}
	
	
	
	@Test
	public void TestPaystationPaymentInvalidState() {
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		myPUI_ = new PaystationUI(0,0);
		myController_ = new PaystationController(myCarpark_, myPUI_);
		myCarpark_.issueAdhocTicket();
		myCarpark_.issueAdhocTicket();
		// Pay in invalid state
		myController_.ticketPaid();
		assertNotEquals(myController_.getState(), PaystationController.STATE.PAID);
		myController_.ticketInserted("A2");
		myController_.ticketPaid();
		assertEquals(myController_.getState(), PaystationController.STATE.PAID);	
		myController_.ticketTaken();
		assertEquals(myController_.getState(), PaystationController.STATE.IDLE);
	}
	
	
	
	
}
