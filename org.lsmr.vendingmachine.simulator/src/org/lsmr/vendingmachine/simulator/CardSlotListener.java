package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a card slot.
 */
public interface CardSlotListener extends AbstractHardwareListener {
    /**
     * Announces that the indicated card slot has had a card inserted into it.
     */
    void cardInserted(CardSlotSimulator slot);

    /**
     * Announces that the indicated card slot has had a card ejected from it.
     */
    void cardEjected(CardSlotSimulator slot);
}
