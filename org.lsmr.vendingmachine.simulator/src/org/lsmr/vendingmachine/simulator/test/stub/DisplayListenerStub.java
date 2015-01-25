package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.DisplaySimulator;
import org.lsmr.vendingmachine.simulator.DisplaySimulatorListener;

public class DisplayListenerStub extends AbstractStub implements
        DisplaySimulatorListener {
    @Override
    public void messageChange(DisplaySimulator display, String oldMsg,
	    String newMsg) {
	recordCallTo("messageChange");
    }
}
