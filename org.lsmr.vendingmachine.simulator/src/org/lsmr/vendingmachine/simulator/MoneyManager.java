package org.lsmr.vendingmachine.simulator;

import java.text.NumberFormat;

public class MoneyManager implements CoinReceptacleListener {
	private int sum;
	private HardwareSimulator hardware;

	public MoneyManager(HardwareSimulator hw) {
		hardware = hw;
		hardware.getCoinReceptacle().register(this);
		sum = 0;
	}
	
	@Override
	public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinAdded(CoinReceptacleSimulator receptacle, Coin coin) {
		sum += coin.getValue();
		displaySum();
	}

	@Override
	public void coinsRemoved(CoinReceptacleSimulator receptacle) {
		sum = 0;
		displaySum();
	}

	@Override
	public void coinsFull(CoinReceptacleSimulator receptacle) {
		hardware.getOutOfOrderLight().activate();
	}

	@Override
	public void enabled(CoinReceptacleSimulator receptacle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(CoinReceptacleSimulator receptacle) {
		// TODO Auto-generated method stub

	}
	
	public int getSum() {
		return sum;
	}

	private void displaySum() {
		NumberFormat fmt = NumberFormat.getCurrencyInstance();
		hardware.getDisplay().display("CURRENT TOTAL: " + fmt.format((float)sum / 100));	
	}
}
