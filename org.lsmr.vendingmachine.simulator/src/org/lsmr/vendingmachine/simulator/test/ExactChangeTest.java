package org.lsmr.vendingmachine.simulator.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.DisplaySimulator;
import org.lsmr.vendingmachine.simulator.HardwareSimulator;

public class ExactChangeTest {
	
	private HardwareSimulator hardware;
	private DisplaySimulator disp;
	
	@Before
	public void setUp(){
		int [] coinValues = { 5, 10, 25, 100, 200 };
		int [] popCosts = { 100, 100, 100 };
		String [] popNames = { "Coke", "Pepsi", "7-up" };
		hardware = new HardwareSimulator(coinValues, popCosts, popNames);
		disp = hardware.getDisplay();
	}
	
	@Test
	public void testExactChangeLight1() {
		try {
			//Initially total of 0
			
			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(!hardware.getExactChangeLight().isActive());

			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(!hardware.getExactChangeLight().isActive());

			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(!hardware.getExactChangeLight().isActive());

			//Add 10 cents
			hardware.getCoinSlot().addCoin(new Coin(10));
			System.out.println(disp.getMessage());
			assertTrue(!hardware.getExactChangeLight().isActive());
			
			//Add 10 cents
			hardware.getCoinSlot().addCoin(new Coin(10));
			System.out.println(disp.getMessage());
			assertTrue(!hardware.getExactChangeLight().isActive());
			
			//Add 10 cents
			hardware.getCoinSlot().addCoin(new Coin(10));
			System.out.println(disp.getMessage());
			assertTrue(hardware.getExactChangeLight().isActive());
			
		} catch (DisabledException e) {
			
		}
	}
	
	@Test
	public void testExactChangeLight2() {
		try {
			//Initially total of 0
			
			//Add 200 cents
			hardware.getCoinSlot().addCoin(new Coin(200));
			System.out.println(disp.getMessage());
			assertTrue(hardware.getExactChangeLight().isActive());
			
			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(hardware.getExactChangeLight().isActive());
			
			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(hardware.getExactChangeLight().isActive());
			
			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertTrue(hardware.getExactChangeLight().isActive());
			
			//Add 25 cents
			hardware.getCoinSlot().addCoin(new Coin(25));
			System.out.println(disp.getMessage());
			assertFalse(hardware.getExactChangeLight().isActive());
			

		} catch (DisabledException e) {
			
		}
	}

}
