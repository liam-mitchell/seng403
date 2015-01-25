package org.lsmr.vendingmachine.simulator;

/**
 * A simple lock device to prevent or to permit the interior of the vending
 * machine to be accessed. The lock does not directly act on the hardware
 * otherwise. By default the lock is initially locked. It ignores the
 * enabled/disabled state.
 */
public class LockSimulator extends AbstractHardware<LockSimulatorListener> {
    private boolean locked = true;

    /**
     * Permits the lock to be initially locked or unlocked without causing
     * events.
     */
    public void loadWithoutEvents(boolean locked) {
	this.locked = locked;
    }

    /**
     * Causes the lock to become locked. Announces a "locked" event to its
     * listeners.
     */
    public void lock() {
	locked = true;

	notifyLocked();
    }

    /**
     * Causes the lock to become unlocked. Announces an "unlocked" event to its
     * listeners.
     */
    public void unlock() {
	locked = false;

	notifyUnlocked();
    }

    /**
     * Returns whether the lock is currently locked. Causes no events.
     */
    public boolean isLocked() {
	return locked;
    }

    private void notifyLocked() {
	Class<?>[] parameterTypes = new Class<?>[] { LockSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(LockSimulatorListener.class, "locked", parameterTypes, args);
    }

    private void notifyUnlocked() {
	Class<?>[] parameterTypes = new Class<?>[] { LockSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(LockSimulatorListener.class, "unlocked", parameterTypes, args);
    }
}
