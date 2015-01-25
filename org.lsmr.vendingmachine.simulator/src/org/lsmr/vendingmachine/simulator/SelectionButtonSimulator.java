package org.lsmr.vendingmachine.simulator;

/**
 * Represents a simple push button on the vending machine. It ignores the
 * enabled/disabled state.
 */
public class SelectionButtonSimulator extends
        AbstractHardware<SelectionButtonSimulatorListener> {
    /**
     * Simulates the pressing of the button. Notifies its listeners of a
     * "pressed" event.
     */
    public void press() {
	notifyPressed();
    }

    private void notifyPressed() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { SelectionButtonSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(SelectionButtonSimulatorListener.class, "pressed", parameterTypes, args);
    }
}
