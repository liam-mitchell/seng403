package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.LockSimulator;
import org.lsmr.vendingmachine.simulator.LockSimulatorListener;

public class LockListenerStub extends AbstractStub implements
        LockSimulatorListener {
    @Override
    public void locked(LockSimulator lock) {
	recordCallTo("locked");
    }

    @Override
    public void unlocked(LockSimulator lock) {
	recordCallTo("unlocked");
    }
}
