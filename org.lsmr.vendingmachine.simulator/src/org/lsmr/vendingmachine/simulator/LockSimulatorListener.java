package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from a lock.
 */
public interface LockSimulatorListener extends AbstractHardwareListener {
    /**
     * An event that announces that the indicated lock has been locked.
     */
    void locked(LockSimulator lock);

    /**
     * An event that announces that the indicated lock has been unlocked.
     */
    void unlocked(LockSimulator lock);
}
