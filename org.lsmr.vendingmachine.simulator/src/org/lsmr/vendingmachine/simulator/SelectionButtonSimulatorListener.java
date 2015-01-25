package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a selection button.
 */
public interface SelectionButtonSimulatorListener extends
        AbstractHardwareListener {
    /**
     * An event that is announced to the listener when the indicated button has
     * been pressed.
     */
    void pressed(SelectionButtonSimulator button);
}
