package org.lsmr.vendingmachine.funds;

public interface CoinsAvailableListener {
    public void coinsAdded(CoinsAvailable fa, Currency curr);

    public void coinsStored(CoinsAvailable fa, Currency curr);

    public void coinsReturned(CoinsAvailable fa, Currency curr);

    public void hardwareFailure(CoinsAvailable fa);
}
