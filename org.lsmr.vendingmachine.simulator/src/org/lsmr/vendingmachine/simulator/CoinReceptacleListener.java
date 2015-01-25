package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a coin receptacle.
 */
public interface CoinReceptacleListener extends AbstractHardwareListener {
    /**
     * An event that announces that the indicated coin has been added to the
     * indicated receptacle.
     */
    void coinAdded(CoinReceptacleSimulator receptacle, Coin coin);

    /**
     * An event that announces that all coins have been removed from the
     * indicated receptacle.
     */
    void coinsRemoved(CoinReceptacleSimulator receptacle);

    /**
     * An event that announces that the indicated receptacle is now full.
     */
    void coinsFull(CoinReceptacleSimulator receptacle);

    /**
     * An event that announces that the indicated receptacle is now enabled.
     */
    void enabled(CoinReceptacleSimulator receptacle);

    /**
     * An event that announces that the indicated receptacle is now disabled.
     */
    void disabled(CoinReceptacleSimulator receptacle);
}
