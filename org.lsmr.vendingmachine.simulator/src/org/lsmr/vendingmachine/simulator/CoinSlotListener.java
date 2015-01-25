package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a coin slot.
 */
public interface CoinSlotListener extends AbstractHardwareListener {
    /**
     * An event announcing that the indicated, valid coin has been inserted and
     * successfully delivered to the storage device connected to the indicated
     * coin slot.
     */
    void validCoinInserted(CoinSlotSimulator coinSlotSimulator, Coin coin);

    /**
     * An event announcing that the indicated coin has been returned.
     */
    void coinRejected(CoinSlotSimulator coinSlotSimulator, Coin coin);
}
