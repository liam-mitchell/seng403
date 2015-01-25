package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.AbstractCoinAcceptor;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;

public class AbstractCoinAcceptorStub extends AbstractStub implements
        AbstractCoinAcceptor {
    private boolean hasSpaceResult;

    public AbstractCoinAcceptorStub(boolean hasSpaceResult) {
	this.hasSpaceResult = hasSpaceResult;
    }

    @Override
    public void acceptCoin(Coin coin) throws CapacityExceededException {
	recordCallTo("acceptCoin");
    }

    @Override
    public boolean hasSpace() {
	recordCallTo("hasSpace");
	return hasSpaceResult;
    }
}
