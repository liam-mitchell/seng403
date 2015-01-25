package org.lsmr.vendingmachine.simulator;

/**
 * A simple device that can be on or off as an indication to users. By default
 * it is initially off.  It ignores the enabled/disabled state.
 */
public class IndicatorLightSimulator extends
        AbstractHardware<IndicatorLightSimulatorListener> {
    private boolean on = false;

    /**
     * Permits the initial state of the light to be on (when active is true) or
     * off (when active is false) without causing events to be generated.
     */
    public void loadWithoutEvents(boolean active) {
	this.on = active;
    }

    /**
     * Turns the light on. Announces an "activated" event to its listeners.
     */
    public void activate() {
	on = true;

	notifyActivated();
    }

    /**
     * Turns the light off. Announces a "deactivated" event to its listeners.
     */
    public void deactivate() {
	on = false;

	notifyDeactivated();
    }

    /**
     * Returns whether the light is currently on (active). Causes no events.
     */
    public boolean isActive() {
	return on;
    }

    private void notifyActivated() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { IndicatorLightSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(IndicatorLightSimulatorListener.class, "activated", parameterTypes, args);
    }

    private void notifyDeactivated() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { IndicatorLightSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(IndicatorLightSimulatorListener.class, "deactivated", parameterTypes, args);
    }
}
