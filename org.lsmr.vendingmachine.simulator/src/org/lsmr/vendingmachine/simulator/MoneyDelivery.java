package org.lsmr.vendingmachine.simulator;

public class MoneyDelivery implements SelectionButtonSimulatorListener
{
	private HardwareSimulator hardware;
	private MoneyManager moneyManager;
	private CoinReceptacle receptacle;
	private CoinRackSimulator connectedCoinRack;
	private int cost;

	public MoneyDelivery(HardwareSimulator hw, int pcost, int index) {
		hardware = hw;
		cost = pcost;
		hardware.getCoinReceptacle().register(this);
		receptacle = hardware.getCoinReceptacle();
		moneyManager = hardware.getMoneyManager();

		hw.getSelectionButton(index).register(this);	//Register to appropriate button
		
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
	public void pressed(SelectionButtonSimulator button) {
		int sum = moneyManager.getSum();
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