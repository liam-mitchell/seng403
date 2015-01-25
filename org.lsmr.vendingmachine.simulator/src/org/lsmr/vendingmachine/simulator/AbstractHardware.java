package org.lsmr.vendingmachine.simulator;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * The abstract base class for all hardware devices involved in the vending
 * machine simulator.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * appropriate register method, but each must define its own notifyXXX methods.
 * The notifyListener method is provided to minimize the work of subclasses.
 * <p>
 * Each hardware device must possess an appropriate listener, which extends
 * AbstractHardwareListener; the type parameter T represents this listener.
 * <p>
 * Any hardware can be disabled, which means it will not permit physical
 * movements. Any method that would cause a physical movement (potentially) will
 * declare that it throws DisabledException.
 */
public abstract class AbstractHardware<T extends AbstractHardwareListener> {
    protected Vector<T> listeners = new Vector<T>();

    /**
     * Locates the indicated listener and removes it such that it will no longer
     * be informed of events from this device. If the listener is not currently
     * registered with this device, calls to this method will return false, but
     * otherwise have no effect.
     * 
     * @param listener
     *            The listener to remove.
     * @return true if the listener was found and removed, false otherwise.
     */
    public final boolean deregister(T listener) {
	return listeners.remove(listener);
    }

    /**
     * All listeners registered with this device are removed. If there are none,
     * calls to this method have no effect.
     */
    public final void deregisterAll() {
	listeners.clear();
    }

    /**
     * Registers the indicated listener to receive event notifications from this
     * device.
     * 
     * @param listener
     *            The listener to be added.
     */
    public final void register(T listener) {
	listeners.add(listener);
    }

    /**
     * Notifies all listeners registered with this device of the indicated
     * event. If the event is unknown, a SimulationException is thrown.
     * <p>
     * An example implementation within a subclass is:
     * 
     * <pre>
     * private void notifyEventXXX() {
     *     Class&lt;?&gt;[] parameterTypes = new Class&lt;?&gt;[] { MyDevice.class };
     *     Object[] args = new Object[] { this };
     *     notifyListeners(MyDeviceListener.class, &quot;eventXXX&quot;, parameterTypes, args);
     * }
     * </pre>
     * 
     * @param listenerClass
     *            The class of the listener for which the event is to be called.
     * @param eventNotificationMethodName
     *            The name of the event notification method.
     * @param parameterTypes
     *            The parameter classes required of the event notification
     *            method; the first must be this device's class.
     * @param args
     *            The arguments to be passed to the event notification method;
     *            the first must be this device.
     * @throws SimulationException
     *             If the notification does not succeed for any reason.
     */
    protected final void notifyListeners(Class<?> listenerClass,
	    String eventNotificationMethodName, Class<?>[] parameterTypes,
	    Object[] args) {
	try {
	    Method m =
		    listenerClass.getMethod(eventNotificationMethodName, parameterTypes);
	    for(T listener : listeners) {
		m.invoke(listener, args);
	    }
	}
	catch(Exception e) {
	    throw new SimulationException(e);
	}
    }

    private boolean disabled = false;

    /**
     * Permits the disabled/enabled status of this hardware to be set without
     * causing events to be announced.
     */
    public final void setDisabledWithoutEvents(boolean disabled) {
	this.disabled = disabled;
    }

    /**
     * Disables this hardware from permitting any physical movements.
     */
    public final void disable() {
	disabled = true;

	Class<?>[] parameterTypes = new Class<?>[] { AbstractHardware.class };
	Object[] args = new Object[] { this };
	notifyListeners(AbstractHardwareListener.class, "disabled", parameterTypes, args);
    }

    /**
     * Enables this hardware for permitting physical movements.
     */
    public final void enable() {
	disabled = false;

	Class<?>[] parameterTypes = new Class<?>[] { AbstractHardware.class };
	Object[] args = new Object[] { this };
	notifyListeners(AbstractHardwareListener.class, "enabled", parameterTypes, args);
    }

    /**
     * Returns whether this hardware is currently disabled from permitting
     * physical movements.
     */
    public final boolean isDisabled() {
	return disabled;
    }
}