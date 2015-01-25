package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinReceptacleListener;
import org.lsmr.vendingmachine.simulator.CoinReceptacleSimulator;

public class CoinReceptacleListenerStub extends AbstractStub implements
        CoinReceptacleListener {
    @Override
    public void enabled(CoinReceptacleSimulator receptacle) {
	recordCallTo("enabled");
    }

    @Override
    public void disabled(CoinReceptacleSimulator receptacle) {
	recordCallTo("disabled");
    }

    @Override
    public void coinAdded(CoinReceptacleSimulator receptacle, Coin coin) {
	recordCallTo("coinAdded");
    }

    @Override
    public void coinsRemoved(CoinReceptacleSimulator receptacle) {
	recordCallTo("coinsRemoved");
    }

    @Override
    public void coinsFull(CoinReceptacleSimulator receptacle) {
	recordCallTo("coinsFull");
    }
}
