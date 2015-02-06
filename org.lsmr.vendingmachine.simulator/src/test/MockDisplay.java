package test;

import java.time.LocalTime;

import org.lsmr.vendingmachine.simulator.DisplaySimulator;

public class MockDisplay extends DisplaySimulator {
	private StringBuilder log;

	public MockDisplay() {
		super();
		log = new StringBuilder();
	}
	
	public void display(String message) {
		super.display(message);
		log.append("[" + LocalTime.now() + "]:");
		log.append(message + '\n');
	}
	
	public String getLog() {
		return log.toString();
	}
}
