package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a coin rack.
 */
public interface CoinRackListener extends AbstractHardwareListener {
    /**
     * Announces that the indicated coin rack is full of coins.
     */
    void coinsFull(CoinRackSimulator rack);

    /**
     * Announces that the indicated coin rack is empty of coins.
     */
    void coinsEmpty(CoinRackSimulator rack);

    /**
     * Announces that the indicated coin has been added to the indicated coin
     * rack.
     */
    void coinAdded(CoinRackSimulator rack, Coin coin);

    /**
     * Announces that the indicated coin has been added to the indicated coin
     * rack.
     */
    void coinRemoved(CoinRackSimulator rack, Coin coin);
}
