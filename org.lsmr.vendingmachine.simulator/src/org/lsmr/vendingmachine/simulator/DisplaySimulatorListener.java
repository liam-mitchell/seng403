package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a display device.
 */
public interface DisplaySimulatorListener extends AbstractHardwareListener {
    /**
     * Event that announces that the message on the indicated display has
     * changed.
     */
    void messageChange(DisplaySimulator display, String oldMsg, String newMsg);
}
