package test.bcccp.carpark.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import bcccp.carpark.CarSensor;
import bcccp.carpark.Carpark;
import bcccp.carpark.Gate;
import bcccp.carpark.TimeProvider;
import bcccp.carpark.entry.EntryController;
import bcccp.carpark.entry.EntryController.STATE;
import bcccp.carpark.entry.EntryUI;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;

public class EntryControllerIntegration {

	private Carpark myCarpark_;
	private AdhocTicketFactory myFactory_;
	private AdhocTicketDAO myAdhocDAO_;
	private SeasonTicketDAO mySeasonDAO_;
	private UsageRecordFactory myUsageFactory_;
	private TimeProvider mTimeProvider_;
	
	private CarSensor eos_;
	private CarSensor eis_;
	private Gate entryGate_;
	private EntryUI myEntryUI_;
	
	private EntryController mEntryController_;
	
	
	
	@Before
	public void setUp() throws Exception {
		// Setting up AdhocDAO, AdhocTicketFactory
		myFactory_ = new AdhocTicketFactory();
		myAdhocDAO_ = new AdhocTicketDAO(myFactory_);
		
		myUsageFactory_ = new UsageRecordFactory();
		mySeasonDAO_ = new SeasonTicketDAO(myUsageFactory_);
		mTimeProvider_ = mock(TimeProvider.class);
		
		eos_ = new CarSensor("Entry Outside Sensor", 20, 100);
		entryGate_ = new Gate(20, 320);
		eis_ = new CarSensor("Entry Inside Sensor", 20, 440);
		myEntryUI_ = new EntryUI(320, 100);	
		
	}
	
	
	
	@Test
	public void EntryControllerIntegrationTest() {
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		mEntryController_ = new EntryController(myCarpark_, entryGate_, eos_, eis_, myEntryUI_);
		
		assertEquals(mEntryController_.getState(), EntryController.STATE.IDLE);
		
		// Button being pushed (Entry Outside Sensor)
		eos_.setCarDetected(true);
		// No Car Inside
		eis_.setCarDetected(false);
		mEntryController_.carEventDetected("Entry Outside Sensor", true);
		assertEquals(mEntryController_.getState(), EntryController.STATE.WAITING);
		mEntryController_.buttonPushed();
		assertEquals(mEntryController_.getState(), EntryController.STATE.ISSUED);
		mEntryController_.ticketTaken();
		assertEquals(mEntryController_.getState(), EntryController.STATE.TAKEN);
		
		// Car entering
		eos_.setCarDetected(true);
		eis_.setCarDetected(true);
		
		mEntryController_.carEventDetected("Entry Inside Sensor", true);
		assertEquals(mEntryController_.getState(), EntryController.STATE.ENTERING);
		
		// Car entered
		eos_.setCarDetected(false);
		eis_.setCarDetected(true);
		
		mEntryController_.carEventDetected("Entry Outside Sensor", false);
		assertEquals(mEntryController_.getState(), EntryController.STATE.ENTERED);
		
		eos_.setCarDetected(false);
		eis_.setCarDetected(false);
		
		mEntryController_.carEventDetected("Entry Inside Sensor", false);
		assertEquals(mEntryController_.getState(), EntryController.STATE.IDLE);
		
	}
	
	
	
	@Test
	public void EntryControllerBlockedTest() {
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		mEntryController_ = new EntryController(myCarpark_, entryGate_, eos_, eis_, myEntryUI_);
		
		// Car inside and outside
		eos_.setCarDetected(true);
		eis_.setCarDetected(true);
		
		mEntryController_.carEventDetected("Entry Inside Sensor", true);
		mEntryController_.carEventDetected("Entry Outside Sensor", true);
		assertEquals(mEntryController_.getState(), EntryController.STATE.BLOCKED);

	}
	
	
	
	@Test
	public void EntryControllerFullTest() {
		myCarpark_ = new Carpark("Carpark", 2, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		mEntryController_ = new EntryController(myCarpark_, entryGate_, eos_, eis_, myEntryUI_);
		
		// Button being pushed (Entry Outside Sensor)

		// No Car Inside
		
		eis_.setCarDetected(false);
		eos_.setCarDetected(true);
		// Record two adhoc tickets to fill carpark up
		myCarpark_.recordAdhocTicketEntry();
		myCarpark_.recordAdhocTicketEntry();
		
		
		assertEquals(mEntryController_.getState(), EntryController.STATE.IDLE);

		mEntryController_.carEventDetected("Entry Outside Sensor", true);
		mEntryController_.carEventDetected("Entry Inside Sensor", false);
		assertEquals(mEntryController_.getState(), EntryController.STATE.WAITING);
		
		
		mEntryController_.buttonPushed();

		// Carpark is full
		assertEquals(mEntryController_.getState(), EntryController.STATE.FULL);
		
	}
	
	@Test
	public void EntryControllerSeasonTicket() {
		myCarpark_ = new Carpark("Carpark", 10, myAdhocDAO_, mySeasonDAO_, mTimeProvider_);
		mEntryController_ = new EntryController(myCarpark_, entryGate_, eos_, eis_, myEntryUI_);
		
		eis_.setCarDetected(false);
		eos_.setCarDetected(true);
		
		assertEquals(mEntryController_.getState(), EntryController.STATE.IDLE);

		mEntryController_.carEventDetected("Entry Outside Sensor", true);
		mEntryController_.carEventDetected("Entry Inside Sensor", false);
		assertEquals(mEntryController_.getState(), EntryController.STATE.WAITING);
		
		mEntryController_.ticketInserted("adsf");
		assertEquals(mEntryController_.getState(), EntryController.STATE.REJECTED);
		
	}
	
	
}
