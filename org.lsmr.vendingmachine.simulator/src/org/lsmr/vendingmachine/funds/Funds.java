package org.lsmr.vendingmachine.funds;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Funds implements CoinsAvailableListener, DebitAvailableListener {
    private ArrayList<FundsListener> listeners = new ArrayList<FundsListener>();
    private CoinsAvailable coins;
    private DebitAvailable debit;

    public Funds(CoinsAvailable coins, DebitAvailable debit) {
	this.coins = coins;
	this.debit = debit;
	coins.register(this);
	debit.register(this);
    }

    public boolean storeFunds(Currency curr, String pin) {
	int amount = curr.getQuantity().intValue();
	int coinsAvailable = coins.getCoinsAvailable().getQuantity().intValue();

	if(amount > coinsAvailable) {
	    int request = amount - coinsAvailable;

	    if(debit.requestFunds(new Currency(curr.getKind(), new BigDecimal(request)), pin)) {
		coins.storeCoins(new Currency(curr.getKind(), new BigDecimal(coinsAvailable)));
		return true;
	    }
	    else
		return false;
	}
	else {
	    coins.storeCoins(curr);
	    return true;
	}
    }

    public void releaseRemainingFunds() {
	coins.returnCoins(coins.getCoinsAvailable());
	debit.returnCard();
    }

    public void register(FundsListener listener) {
	listeners.add(listener);
    }

    @Override
    public void hardwareFailure(DebitAvailable da) {
	notifyHardwareFailure();
    }

    private void notifyHardwareFailure() {
	for(FundsListener l : listeners)
	    l.hardwareFailure(this);
    }

    @Override
    public void coinsAdded(CoinsAvailable fa, Currency curr) {}

    @Override
    public void coinsStored(CoinsAvailable fa, Currency curr) {}

    @Override
    public void coinsReturned(CoinsAvailable fa, Currency curr) {}

    @Override
    public void hardwareFailure(CoinsAvailable fa) {
	notifyHardwareFailure();
    }
}
