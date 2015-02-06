package org.lsmr.vendingmachine.simulator;

import java.util.Arrays;

public class ExactChangeManager implements CoinReceptacleListener, CoinRackListener {
	
	private HardwareSimulator hardware;
	private IndicatorLightSimulator exactChangeLight;
	private int receptacleAmount = 0;
	private int[] receptacleCoins;
	private int[] coins;
	private int lowestPopCost;
	private int[] coinValues;
	
	public ExactChangeManager(HardwareSimulator hw) {
		hardware = hw;
		hardware.getCoinReceptacle().register(this);
		
		for(int a = 0; a < hardware.getNumberOfCoinRacks(); a++) {
			hardware.getCoinRack(a).register(this);
		}
		
		exactChangeLight = hardware.getExactChangeLight();
		lowestPopCost = hardware.getPopCosts()[0];
		
		for(int b = 0; b < hardware.getPopCosts().length; b++) {
			if(hardware.getCoinValues()[b] < lowestPopCost) {
				lowestPopCost = hardware.getPopCosts()[b];
			}
		}
		
		coinValues = hardware.getCoinValues();
		Arrays.sort(coinValues);
		
		coins = new int[hardware.getCoinValues().length];
		receptacleCoins = new int[hardware.getCoinValues().length];
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
	public void coinsFull(CoinRackSimulator rack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinsEmpty(CoinRackSimulator rack) {
		// TODO Auto-generated method stub

	}

	public int getCoinValueArrayIndex(Coin coin) {
		int index = 0;
		
		for(int a = 0; a < coinValues.length; a++) {
			if(coinValues[a] == coin.getValue()){
				index = a;
			}
		}
		
		return index;
	}
	
	@Override
	public void coinAdded(CoinRackSimulator rack, Coin coin) {
		coins[getCoinValueArrayIndex(coin)]++;
	}

	@Override
	public void coinRemoved(CoinRackSimulator rack, Coin coin) {
		coins[getCoinValueArrayIndex(coin)]--;
	}

	@Override
	public void coinAdded(CoinReceptacleSimulator receptacle, Coin coin) {
		receptacleAmount += coin.getValue();
		receptacleCoins[getCoinValueArrayIndex(coin)]++;
		coins[getCoinValueArrayIndex(coin)]++;
		
		if(receptacleAmount > lowestPopCost) {
			checkExactChangeLight();
		}
	}

	@Override
	public void coinsRemoved(CoinReceptacleSimulator receptacle) {
		for(int a = 0; a < receptacleCoins.length; a++) {
			for(int b = 0; b < receptacleCoins[a]; b++) {
				coins[a]--;
			}
			
			receptacleCoins[a] = 0;
		}
		receptacleAmount = 0;
		
		exactChangeLight.deactivate();
	}

	@Override
	public void coinsFull(CoinReceptacleSimulator receptacle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enabled(CoinReceptacleSimulator receptacle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(CoinReceptacleSimulator receptacle) {
		// TODO Auto-generated method stub

	}
	
	public void checkExactChangeLight() {
		int changeRequired = receptacleAmount - lowestPopCost;
		int[] tempCoins = new int[coins.length];
		System.arraycopy(coins, 0, tempCoins, 0, coins.length);
		
		for(int a = coins.length - 1; a >= 0; a--) {
			//for(int b = 0; b < coins[a]; b++) {
				//if((changeRequired - coinValues[a]) > 0) {
					//changeRequired -= coinValues[a];
				//}
			//}
			
			
			
			while (tempCoins[a] > 0 && changeRequired >= coinValues[a]) {
				changeRequired -= coinValues[a];
				tempCoins[a]--;
			}
		}
		
		if(changeRequired > 0) {
			exactChangeLight.activate();
			System.out.println("Exact Change Light ON");
		}else if(exactChangeLight.isActive()){
			exactChangeLight.deactivate();
			System.out.println("Exact Change Light OFF");
		}
	}
}
