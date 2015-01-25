package org.lsmr.vendingmachine.simulator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a storage rack for pop cans within the vending machine. More than
 * one would typically exist within the same vending machine. The pop can rack
 * has finite, positive capacity. A pop can rack can be disabled, which prevents
 * it from dispensing pop cans.
 */
public class PopCanRackSimulator extends AbstractHardware<PopCanRackListener> {
    private int maxCapacity;
    private Queue<PopCan> queue = new LinkedList<PopCan>();
    private PopCanChannelSimulator sink;

    /**
     * Creates a new pop can rack with the indicated maximum capacity. The pop
     * can rack initially is empty.
     * 
     * @param capacity
     *            Positive integer indicating the maximum capacity of the rack.
     * @throws SimulationException
     *             if the indicated capacity is not positive.
     */
    public PopCanRackSimulator(int capacity) {
	if(capacity <= 0)
	    throw new SimulationException("Capacity cannot be non-positive: " + capacity);

	this.maxCapacity = capacity;
    }

    /**
     * Returns the maximum capacity of this pop can rack. Causes no events.
     */
    public int getCapacity() {
	return maxCapacity;
    }

    /**
     * Connects the pop can rack to an outlet channel, such as the delivery
     * chute. Causes no events.
     * 
     * @param sink
     *            The channel to be used as the outlet for dispensed pop cans.
     */
    public void connect(PopCanChannelSimulator sink) {
	this.sink = sink;
    }

    /**
     * Adds the indicated pop can to this pop can rack if there is sufficient
     * space available. If the pop can is successfully added to this pop can
     * rack, a "popAdded" event is announced to its listeners. If, as a result
     * of adding this pop can, this pop can rack has become full, a "popFull"
     * event is announced to its listeners.
     * 
     * @param pop
     *            The pop can to be added.
     * @throws CapacityExceededException
     *             if the pop can rack is already full.
     * @throws DisabledException
     *             if the pop can rack is currently disabled.
     */
    public void addPop(PopCan pop) throws CapacityExceededException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(queue.size() >= maxCapacity)
	    throw new CapacityExceededException();

	queue.add(pop);

	notifyPopAdded(pop);

	if(queue.size() >= maxCapacity)
	    notifyPopFull();
    }

    /**
     * Causes one pop can to be removed from this pop can rack, to be placed in
     * the output channel to which this pop can rack is connected. If a pop can
     * is removed from this pop can rack, a "popRemoved" event is announced to
     * its listeners. If the removal of the pop can causes this pop can rack to
     * become empty, a "popEmpty" event is announced to its listeners.
     * 
     * @throws DisabledException
     *             if this pop can rack is currently disabled.
     * @throws EmptyException
     *             if no pop cans are currently contained in this pop can rack.
     * @throws CapacityExceededException
     *             if the output channel cannot accept the dispensed pop can.
     */
    public void dispensePop() throws DisabledException, EmptyException,
	    CapacityExceededException {
	if(isDisabled())
	    throw new DisabledException();

	if(queue.isEmpty())
	    throw new EmptyException();

	PopCan pop = queue.remove();
	notifyPopRemoved(pop);

	if(sink == null)
	    throw new SimulationException("The output channel is not connected");

	sink.acceptPop(pop);

	if(queue.isEmpty())
	    notifyPopEmpty();
    }

    /**
     * Allows pop cans to be loaded into the pop can rack without causing events
     * to occur on its listeners. This permits a simple initialization. Note
     * that any existing pop cans in the rack are not removed.
     * 
     * @param pops
     *            One or more pop cans to be loaded into this pop can rack.
     * @throws SimulationException
     *             if the number of cans to be loaded exceeds the capacity of
     *             this pop can rack.
     */
    public void loadWithoutEvents(PopCan... pops) throws SimulationException {
	if(maxCapacity < queue.size() + pops.length)
	    throw new SimulationException("Capacity exceeded by attempt to load");

	for(PopCan pop : pops) {
	    queue.add(pop);
	}
    }

    private void notifyPopAdded(PopCan pop) {
	Class<?>[] parameterTypes =
	        new Class<?>[] { PopCanRackSimulator.class, PopCan.class };
	Object[] args = new Object[] { this, pop };
	notifyListeners(PopCanRackListener.class, "popAdded", parameterTypes, args);
    }

    private void notifyPopFull() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { PopCanRackSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(PopCanRackListener.class, "popFull", parameterTypes, args);
    }

    private void notifyPopEmpty() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { PopCanRackSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(PopCanRackListener.class, "popEmpty", parameterTypes, args);
    }

    private void notifyPopRemoved(PopCan pop) {
	Class<?>[] parameterTypes =
	        new Class<?>[] { PopCanRackSimulator.class, PopCan.class };
	Object[] args = new Object[] { this, pop };
	notifyListeners(PopCanRackListener.class, "popRemoved", parameterTypes, args);
    }
}
