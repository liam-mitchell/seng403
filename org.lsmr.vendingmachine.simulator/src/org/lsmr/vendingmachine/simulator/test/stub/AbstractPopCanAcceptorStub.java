package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.AbstractPopCanAcceptor;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.PopCan;

public class AbstractPopCanAcceptorStub extends AbstractStub implements
        AbstractPopCanAcceptor {
    @Override
    public void acceptPop(PopCan pop) throws CapacityExceededException {
	recordCallTo("acceptPop");
    }
}
