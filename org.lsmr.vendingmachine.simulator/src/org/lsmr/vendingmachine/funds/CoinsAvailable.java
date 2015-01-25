package org.lsmr.vendingmachine.funds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinRackListener;
import org.lsmr.vendingmachine.simulator.CoinRackSimulator;
import org.lsmr.vendingmachine.simulator.CoinReceptacleListener;
import org.lsmr.vendingmachine.simulator.CoinReceptacleSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;

public class CoinsAvailable implements CoinReceptacleListener, CoinRackListener {
    private ArrayList<CoinsAvailableListener> listeners = new ArrayList<CoinsAvailableListener>();
    private CoinReceptacleSimulator receptacle;
    private CoinRackSimulator[] racks;
    private int[] rackDenominations;
    private int totalCoinsAvailable = 0;

    public CoinsAvailable(CoinReceptacleSimulator cr, int[] rackDenominations,
	    CoinRackSimulator[] racks) {
	if(rackDenominations == null || racks == null || rackDenominations.length != racks.length)
	    throw new IllegalArgumentException();

	receptacle = cr;
	this.racks = racks;
	this.rackDenominations = rackDenominations;

	receptacle.register(this);
	for(CoinRackSimulator rack : racks)
	    rack.register(this);
    }

    public void register(CoinsAvailableListener listener) {
	listeners.add(listener);
    }

    @Override
    public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {}

    @Override
    public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {}

    @Override
    public void coinsFull(CoinRackSimulator rack) {}

    @Override
    public void coinsEmpty(CoinRackSimulator rack) {}

    @Override
    public void coinAdded(CoinRackSimulator rack, Coin coin) {}

    @Override
    public void coinRemoved(CoinRackSimulator rack, Coin coin) {}

    @Override
    public void coinAdded(CoinReceptacleSimulator crs, Coin coin) {
	if(crs == receptacle) {
	    totalCoinsAvailable += coin.getValue();
	    notifyCoinsAdded(coin);
	}
	else
	    notifyHardwareFailure();
    }

    protected void notifyHardwareFailure() {
	for(CoinsAvailableListener listener : listeners)
	    listener.hardwareFailure(this);
    }

    protected void notifyCoinsAdded(Coin coin) {
	Currency curr =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(coin.getValue()));
	for(CoinsAvailableListener listener : listeners)
	    listener.coinsAdded(this, curr);
    }

    @Override
    public void coinsRemoved(CoinReceptacleSimulator receptacle) {}

    protected void notifyCoinsStored(int amount) {
	Currency curr =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(amount));
	for(CoinsAvailableListener listener : listeners)
	    listener.coinsStored(this, curr);
    }

    @Override
    public void coinsFull(CoinReceptacleSimulator receptacle) {}

    @Override
    public void enabled(CoinReceptacleSimulator receptacle) {}

    @Override
    public void disabled(CoinReceptacleSimulator receptacle) {}

    public Currency getCoinsAvailable() {
	return new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(totalCoinsAvailable));
    }

    public void storeCoins(Currency curr) {
	int amount = curr.getQuantity().intValue();

	if(amount > totalCoinsAvailable)
	    notifyHardwareFailure();
	else {
	    try {
		receptacle.storeCoins();
	    }
	    catch(CapacityExceededException e) {
		notifyHardwareFailure();
		return;
	    }
	    catch(DisabledException e) {
		notifyHardwareFailure();
		return;
	    }
	    totalCoinsAvailable -= amount;
	    notifyCoinsStored(amount);
	}
    }

    public void returnCoins(Currency curr) {
	int amount = curr.getQuantity().intValue();

	if(totalCoinsAvailable == amount)
	    try {
		receptacle.returnCoins();
	    }
	    catch(CapacityExceededException e) {
		notifyHardwareFailure();
		return;
	    }
	    catch(DisabledException e) {
		notifyHardwareFailure();
		return;
	    }
	else if(totalCoinsAvailable < amount) {
	    notifyHardwareFailure();
	    return;
	}
	else {
	    try {
		receptacle.storeCoins();
	    }
	    catch(CapacityExceededException e) {
		notifyHardwareFailure();
		return;
	    }
	    catch(DisabledException e) {
		notifyHardwareFailure();
		return;
	    }

	    int remainder = dispense(amount);
	    if(remainder != 0) {
		int totalReturned = amount - remainder;
		totalCoinsAvailable -= totalReturned;
		if(totalReturned > 0)
		    notifyCoinsReturned(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(totalReturned)));
		notifyHardwareFailure();
		return;
	    }
	}

	totalCoinsAvailable -= amount;
	notifyCoinsReturned(curr);
    }

    protected void notifyCoinsReturned(Currency curr) {
	for(CoinsAvailableListener l : listeners)
	    l.coinsReturned(this, curr);
    }

    /**
     * Attempts to return the requested amount, one coin at a time.
     * 
     * @param amount
     *            The amount in cents to be dispensed
     * @return Any residual amount that was not dispensable.
     */
    private int dispense(int amount) {
	for(int i = racks.length - 1; i >= 0; i--) {
	    CoinRackSimulator rack = racks[i];
	    int value = rackDenominations[i];

	    coinLoop: while(amount >= value) {
		try {
		    rack.releaseCoin();
		}
		catch(CapacityExceededException e) {
		    notifyHardwareFailure();
		    return amount;
		}
		catch(EmptyException e) {
		    break coinLoop;
		}
		catch(DisabledException e) {
		    notifyHardwareFailure();
		    return amount;
		}

		amount -= value;
	    }
	}

	return amount;
    }
}
