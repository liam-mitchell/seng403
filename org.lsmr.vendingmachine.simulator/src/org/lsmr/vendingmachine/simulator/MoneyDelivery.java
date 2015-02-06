package org.lsmr.vendingmachine.simulator;

import java.text.NumberFormat
public class MoneyDelivery
	implements SelectionButtonSimulatorListener, CoinReceptacle, MoneyManager, PopSelector
{
	private HardwareSimulator hardware;
	private MoneyManager moneyManager;
	private PopSelector popSelector;
	private CoinReceptacle receptacle;
	private CoinRackSimulator connectedCoinRack;

	public MoneyDelivery(HardwareSimulator hw) {
		hardware = hw;
		hardware.getCoinReceptacle().register(this);
		receptacle = hardware.getCoinReceptacle();
		moneyManager = hardware.getMoneyManager();

		for (i = 0; i < hardware.getNumberOfSelectionButtons(); i++) {
			hardware.getSelectionButton(i).register(this);
		}
	}

	@Override
	public void pressed(SelectionButtonSimulator button) {
		int sum = moneyManager.getSum();
		int cost = popSelector.getPopCost();
		int difference;
		int change;
		if (hardware.getOutOfOrderLight().isActive) {
			receptacle.returnCoins();
		}
		else if (sum > cost) {
			receptacle.storeCoins();
			difference = sum - cost;
			for (int i = hardware.getNumberOfCoinRacks() - 1; i >= 0; i--) {
				connectedCoinRack = hardware.getCoinRack(i)
				change = difference/coinValues[i];
				for (int j = 0; j < change; j++)
					connectedCoinRack.releaseCoin();
				difference = difference - (change * coinValues[i]);
			}
		}
		else if (sum = cost)
			receptacle.storeCoins();
		else if (hardware.getExactChangeLight().isActive)
			hardware.getDisplay().display("Insufficient Funds");
		
		moneyManager.coinsRemoved(receptacle);
	}

	public void returnPressed() {
		receptacle.returnCoins();
		moneyManager.coinsRemoved(receptacle)
	}