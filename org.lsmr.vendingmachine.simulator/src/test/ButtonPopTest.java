package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DisplaySimulator;
import org.lsmr.vendingmachine.simulator.HardwareSimulator;
import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;

public class ButtonPopTest extends SelectionButtonSimulator {

	private HardwareSimulator hw;
	private DisplaySimulator disp;
	private Object Items[];
	PopCan aPop;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		try {	
			int [] coinValues = { 5, 10, 25, 100, 200 };
			int [] popCosts = { 100, 100, 100 };
			String [] popNames = { "Coke", "Pepsi", "7-up" };
			hw = new HardwareSimulator(coinValues, popCosts, popNames);
			disp = hw.getDisplay();

			aPop = new PopCan();	//This is a can of pop.
			
		} catch (Exception e) { /* gotta catch em all */ }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_ES_LoadPopAndDispense() {
		try
		{
			System.out.println("Start Test 1\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));
			
			System.out.println("Loading a can of pop into index 1.");
			hw.getPopCanRack(1).addPop(aPop);	//Load the pop in.
	
			System.out.println("Attempting to dispense a pop from index 1 which should now have a pop in it.");
			hw.getSelectionButton(1).press();				//Hopefully it dispenses and is not empty.
			
			assertEquals(aPop, (PopCan)hw.getDeliveryChute().removeItems()[0] );
			
			System.out.println("\nEnd Test 1\n");

		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_EF_AttemptEmptyDispense() {
		try
		{
			System.out.println("Start Test 2\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));
			
			System.out.println("Attempting to dispense a pop from index 1 which is empty. Expecting an exception.");
			hw.getSelectionButton(1).press();	//Empty, EmptyException*/

			assertEquals(aPop, (PopCan)hw.getDeliveryChute().removeItems()[0] );
			
			System.out.println("\nEnd Test 2\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_EF_AttemptWrongDispense() {
		try
		{
			System.out.println("Start Test 3\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));
			
			System.out.println("Loading a can of pop into index 1.");
			PopCan aPop = new PopCan();			//This is a can of pop.
			hw.getPopCanRack(1).addPop(aPop);	//Load it in.
			
			System.out.println("Attempting to dispense a pop from index 0 which is still empty. Expecting an exception.");
			hw.getSelectionButton(0).press();				//Should fail.
			
			assertEquals(hw.getDisplay().getMessage(), "OUT OF STOCK");
			
			System.out.println("\nEnd Test 3\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_EF_AttemptInsufficientCoins() {
		try
		{
			System.out.println("Start Test 4\n");
			
			System.out.println("Loading a can of pop into index 1.");
			PopCan aPop = new PopCan();			//This is a can of pop.
			hw.getPopCanRack(1).addPop(aPop);	//Load it in.
			
			System.out.println("Attempting to dispense a pop from index 0 which is still empty. Expecting insufficient coins.");
			hw.getSelectionButton(0).press();				//Should fail.

			assertEquals(aPop, (PopCan)hw.getDeliveryChute().removeItems()[0] );
			
			System.out.println("\nEnd Test 4\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}

}
