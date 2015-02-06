package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.lsmr.vendingmachine.simulator.HardwareSimulator;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;

public class EndToEndTest {
	private HardwareSimulator hardware;

	private static final int [] coinValues = { 5, 10, 25, 100, 200 };
	private static final int [] popCosts = { 100, 100, 100 };
	private static final String [] popNames = { "Coke", "Pepsi", "7-up" };

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		hardware = new HardwareSimulator(coinValues, popCosts, popNames);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
	}

	@Test
	public void testCorrectSumDisplayed() throws DisabledException {
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.00");

		hardware.getCoinSlot().addCoin(new Coin(5));
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.05");
		
		hardware.getCoinSlot().addCoin(new Coin(10));
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.15");
		
		hardware.getCoinSlot().addCoin(new Coin(25));
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.40");
		
		hardware.getCoinSlot().addCoin(new Coin(100));
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $1.40");
		
		hardware.getCoinSlot().addCoin(new Coin(200));
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $3.40");
	}
	
	@Test
	public void testOutOfOrderWhenFull() throws DisabledException {
		// Buy ten pops to fill the storage bin (capacity 1000)
		for (int i = 0; i < 1000; ++i) {
			hardware.getCoinSlot().addCoin(new Coin(1));
			if (i % 100 == 0) {
				hardware.getSelectionButton(1).press();
			}
		}
		
		assertEquals(hardware.getOutOfOrderLight().isActive(), true);
	}
	
	@Test
	public void testCoinReturn()
			throws DisabledException, CapacityExceededException
	{
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.00");
		hardware.getCoinReceptacle().returnCoins();
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.00");
		
		hardware.getCoinSlot().addCoin(new Coin(100));
		hardware.getCoinSlot().addCoin(new Coin(25));
		hardware.getCoinSlot().addCoin(new Coin(10));
		
		hardware.getCoinReceptacle().returnCoins();
		Object [] coins = hardware.getDeliveryChute().removeItems();
		
		assertEquals(new Coin(100).getValue(), ((Coin)coins[0]).getValue());
		assertEquals(new Coin(25).getValue(), ((Coin)coins[1]).getValue());
		assertEquals(new Coin(10).getValue(), ((Coin)coins[2]).getValue());
		
		assertEquals(hardware.getDisplay().getMessage(), "CURRENT TOTAL: $0.00");
	}
	
	@Test
	public void testDisplayErrorWhenNoPop()
			throws DisabledException, CapacityExceededException
	{
		hardware = new MockHardwareSimulator(coinValues, popCosts, popNames);
		
		hardware.getCoinSlot().addCoin(new Coin(100));
		hardware.getSelectionButton(1).press();
		
		String log = ((MockDisplay)hardware.getDisplay()).getLog();
		System.out.println("DISPLAY LOG: " + log);
		
		assertTrue(log.contains("OUT OF STOCK"));
	}

}
