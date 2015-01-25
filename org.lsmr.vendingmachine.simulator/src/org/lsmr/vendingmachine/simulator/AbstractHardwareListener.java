package org.lsmr.vendingmachine.simulator;

/**
 * This class represents the abstract interface for all hardware listeners. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the instance affected.
 */
public interface AbstractHardwareListener {
    /**
     * Announces that the indicated hardware has been enabled.
     */
    public void enabled(AbstractHardware<AbstractHardwareListener> hardware);

    /**
     * Announces that the indicated hardware has been disabled.
     */
    public void disabled(AbstractHardware<AbstractHardwareListener> hardware);
}
