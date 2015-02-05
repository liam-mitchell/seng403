package org.lsmr.vendingmachine.simulator;

import java.text.NumberFormat;

public class MoneyManager implements CoinReceptacleListener {
	private int sum;
	private HardwareSimulator hardware;

	public MoneyManager(HardwareSimulator hw) {
		hardware = hw;
		hardware.getCoinReceptacle().register(this);
		hardware.getStorageBin().register(this);
		sum = 0;
		displaySum();
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
		if (isReceptacle(receptacle)) {
			sum += coin.getValue();
			displaySum();
		}
	}

	@Override
	public void coinsRemoved(CoinReceptacleSimulator receptacle) {
		if (isReceptacle(receptacle)) {
			sum = 0;
			displaySum();
		}
		else if (isStorageBin(receptacle)) {
			if (hardware.getOutOfOrderLight().isActive()) {
				hardware.getOutOfOrderLight().deactivate();
			}
		}
	}

	@Override
	public void coinsFull(CoinReceptacleSimulator receptacle) {
		if (isStorageBin(receptacle)) {
			hardware.getOutOfOrderLight().activate();
		}
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
	
	private boolean isReceptacle(CoinReceptacleSimulator receptacle) {
		return receptacle.equals(hardware.getCoinReceptacle());
	}
	
	private boolean isStorageBin(CoinReceptacleSimulator receptacle) {
		return receptacle.equals(hardware.getStorageBin());
	}
}
