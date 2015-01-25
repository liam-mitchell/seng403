package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a pop can rack.
 */
public interface PopCanRackListener extends AbstractHardwareListener {
    /**
     * An event announced when the indicated pop can is added to the indicated
     * pop can rack.
     */
    void popAdded(PopCanRackSimulator popRack, PopCan pop);

    /**
     * An event announced when the indicated pop can is removed from the
     * indicated pop can rack.
     */
    void popRemoved(PopCanRackSimulator popRack, PopCan pop);

    /**
     * An event announced when the indicated pop can rack becomes full.
     */
    void popFull(PopCanRackSimulator popRack);

    /**
     * An event announced when the indicated pop can rack becomes empty.
     */
    void popEmpty(PopCanRackSimulator popRack);
}
