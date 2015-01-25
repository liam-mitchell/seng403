package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.IndicatorLightSimulator;
import org.lsmr.vendingmachine.simulator.IndicatorLightSimulatorListener;

public class IndicatorLightListenerStub extends AbstractStub implements
        IndicatorLightSimulatorListener {
    @Override
    public void activated(IndicatorLightSimulator light) {
	recordCallTo("activated");
    }

    @Override
    public void deactivated(IndicatorLightSimulator light) {
	recordCallTo("deactivated");
    }
}
