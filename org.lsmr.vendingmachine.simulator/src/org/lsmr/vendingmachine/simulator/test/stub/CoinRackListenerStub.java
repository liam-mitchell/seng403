package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinRackListener;
import org.lsmr.vendingmachine.simulator.CoinRackSimulator;

public class CoinRackListenerStub extends AbstractStub implements
        CoinRackListener {
    @Override
    public void coinsFull(CoinRackSimulator rack) {
	recordCallTo("coinsFull");
    }

    @Override
    public void coinsEmpty(CoinRackSimulator rack) {
	recordCallTo("coinsEmpty");
    }

    @Override
    public void coinAdded(CoinRackSimulator rack, Coin coin) {
	recordCallTo("coinAdded");
    }

    @Override
    public void coinRemoved(CoinRackSimulator rack, Coin coin) {
	recordCallTo("coinRemoved");
    }
}
