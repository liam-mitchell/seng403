package org.lsmr.vendingmachine.simulator;

/**
 * A simple device that displays a string. How it does this is not part of the
 * simulation. A very long string might scroll continuously, for example.
 */
public class DisplaySimulator extends
        AbstractHardware<DisplaySimulatorListener> {
    private String message = null;

    /**
     * Tells the display to start displaying the indicated message. Announces a
     * "messageChange" event to its listeners.
     */
    public void display(String msg) {
	String oldMsg = message;
	message = msg;
	notifyMessageChange(oldMsg, msg);
    }

    /**
     * Permits the display message to be set without causing events to be
     * announced.
     */
    public void loadWithoutEvents(String message) {
	this.message = message;
    }

    private void notifyMessageChange(String oldMsg, String newMsg) {
	Class<?>[] parameterTypes =
	        new Class<?>[] { DisplaySimulator.class, String.class,
	                String.class };
	Object[] args = new Object[] { this, oldMsg, newMsg };
	notifyListeners(DisplaySimulatorListener.class, "messageChange", parameterTypes, args);
    }
}
