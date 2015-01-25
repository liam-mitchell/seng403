package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinSlotListener;
import org.lsmr.vendingmachine.simulator.CoinSlotSimulator;

public class CoinSlotListenerStub extends AbstractStub implements
        CoinSlotListener {
    @Override
    public void validCoinInserted(CoinSlotSimulator coinSlotSimulator, Coin coin) {
	recordCallTo("validCoinInserted");
    }

    @Override
    public void coinRejected(CoinSlotSimulator coinSlotSimulator, Coin coin) {
	recordCallTo("coinRejected");
    }
}
