package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulatorListener;

public class SelectionButtonListenerStub extends AbstractStub implements
        SelectionButtonSimulatorListener {
    @Override
    public void pressed(SelectionButtonSimulator button) {
	recordCallTo("pressed");
    }
}
