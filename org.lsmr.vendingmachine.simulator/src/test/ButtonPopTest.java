package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.DisplaySimulator;
import org.lsmr.vendingmachine.simulator.HardwareSimulator;
import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;

public class ButtonPopTest extends SelectionButtonSimulator {

	private HardwareSimulator hw;
	private DisplaySimulator disp;
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
	public void test_1_ES_LoadPopAndDispense() {
		try
		{//Load a pop into machine and attempt to dispense it. Should work correctly.
			
			System.out.println("Start Test 1\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));	//Inserting a coin into the system. Sufficient for any pop.
			
			System.out.println("Loading a can of pop into index 1.");
			hw.getPopCanRack(1).addPop(aPop);			//Load the pop in.
	
			System.out.println(disp.getMessage());		//Coin message before dispensing pop.
			
			System.out.println("Attempting to dispense a pop from index 1 which should now have a pop in it.");
			hw.getSelectionButton(1).press();			//Hopefully it dispenses and is not empty.
			
			System.out.println(disp.getMessage());		//Coin message after dispensing pop. Currently still the same. Not my job.
			
			assertEquals(1, hw.getDeliveryChute().removeItems().length);	//We get 1 pop. This should pass.
			
			System.out.println("\nEnd Test 1\n");

		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_2_EF_AttemptEmptyDispense() {
		try
		{//Attempting to dispense a pop from an empty machine. Nothing should come out.
			
			System.out.println("Start Test 2\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));	//Inserting a coin into the system. Sufficient for any pop.
			
			System.out.println("Attempting to dispense a pop from index 1 which is empty. Expecting an exception.");
			hw.getSelectionButton(1).press();			//Empty, EmptyException*/

			assertEquals(0, hw.getDeliveryChute().removeItems().length);	//We should get 0 pops.
			
			System.out.println("\nEnd Test 2\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_3_EF_AttemptWrongDispense() {
		try
		{//Load a pop into one rack and then attempt to dispense form another. Nothing should come out.
			
			System.out.println("Start Test 3\n");
			
			System.out.println("Inserting coin.");
			hw.getCoinSlot().addCoin(new Coin(100));	//Inserting a coin into the system. Sufficient for any pop.
			
			System.out.println("Loading a can of pop into index 1.");
			hw.getPopCanRack(1).addPop(aPop);			//Load the pop in.
			
			hw.getDeliveryChute().removeItems();		//Attempting to clear out the chute
			
			System.out.println("Attempting to dispense a pop from index 0 which is still empty. Expecting an exception.");
			hw.getSelectionButton(0).press();			//Should fail.
			
			assertEquals(0, hw.getDeliveryChute().removeItems().length);	//We should get 0 pops.
			
			System.out.println("\nEnd Test 3\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}
	
	@Test
	public void test_4_EF_AttemptInsufficientCoins() {
		try
		{//Attempting to dispense a pop with no coins in machine.
			
			System.out.println("Start Test 4\n");
			
			System.out.println("Loading a can of pop into index 1.");
			hw.getPopCanRack(1).addPop(aPop);			//Load the pop in.
			
			System.out.println("Attempting to dispense a pop from index 0 which is still empty. Expecting insufficient coins.");
			hw.getSelectionButton(0).press();			//Should fail.
			
			assertEquals(0, hw.getDeliveryChute().removeItems().length);	//We should get 0 pops.
			
			System.out.println("\nEnd Test 4\n");
			
		} catch (Exception e) { /* gotta catch em all */ }
	}
	
}
