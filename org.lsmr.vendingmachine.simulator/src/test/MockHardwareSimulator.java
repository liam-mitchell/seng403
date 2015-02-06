package test;

import org.lsmr.vendingmachine.simulator.HardwareSimulator;

public class MockHardwareSimulator extends HardwareSimulator {

	public MockHardwareSimulator(int[] coinValues, int[] popCosts, String[] popNames) {
		super(coinValues, popCosts, popNames);
		
		setDisplay(new MockDisplay());
	}
}
