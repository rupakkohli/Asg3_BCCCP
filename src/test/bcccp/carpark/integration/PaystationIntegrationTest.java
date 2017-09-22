package test.bcccp.carpark.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import bcccp.carpark.Carpark;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.carpark.paystation.PaystationController.STATE;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.adhoc.IAdhocTicketDAO;

public class PaystationIntegrationTest {

	private Carpark myCarpark;
	private AdhocTicketFactory myFactory;
	private AdhocTicketDAO myAdhocDAO;
	
	
	
	private PaystationUI myPUI;
	private PaystationController myController;
	
	@Before
	public void setUp() throws Exception {
		// Setting up AdhocDAO, AdhocTicketFactory
		myFactory = new AdhocTicketFactory();
		myAdhocDAO = new AdhocTicketDAO(myFactory);
	}
	
	@Test
	public void testTicketInsertedSuccess() {
		
		// Setup carpark, UI and Controller
		
		myCarpark = new Carpark("Carpark", 2, myAdhocDAO, null);
		myPUI = new PaystationUI(0,0);
		myController = new PaystationController(myCarpark, myPUI);
		
		// First ticket created should be A1
		myCarpark.issueAdhocTicket();
		myController.ticketInserted("A1");
		
		assertEquals(myController.getState(), STATE.WAITING);
		myController.ticketPaid();
		assertEquals(myController.getState(), STATE.PAID);	
		myController.ticketTaken();
		assertEquals(myController.getState(), STATE.IDLE);

	}
	
	@Test
	public void testTicketInsertedRejected() {
		myCarpark = new Carpark("Carpark", 2, myAdhocDAO, null);
		myPUI = new PaystationUI(0,0);
		myController = new PaystationController(myCarpark, myPUI);
		
		myCarpark.issueAdhocTicket();
		myController.ticketInserted("A3"); // Hasn't been created yet so invalid
		
		assertEquals(myController.getState(), STATE.REJECTED);
		myController.ticketTaken();
		assertEquals(myController.getState(), STATE.IDLE);
	}


	
	
	
	
	
}
