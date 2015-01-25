package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.PopCanRackListener;
import org.lsmr.vendingmachine.simulator.PopCanRackSimulator;

public class PopCanRackListenerStub extends AbstractStub implements
        PopCanRackListener {
    @Override
    public void popAdded(PopCanRackSimulator popRack, PopCan pop) {
	recordCallTo("popAdded");
    }

    @Override
    public void popRemoved(PopCanRackSimulator popRack, PopCan pop) {
	recordCallTo("popRemoved");
    }

    @Override
    public void popFull(PopCanRackSimulator popRack) {
	recordCallTo("popFull");
    }

    @Override
    public void popEmpty(PopCanRackSimulator popRack) {
	recordCallTo("popEmpty");
    }
}
