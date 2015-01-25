package org.lsmr.vendingmachine.simulator;

import java.util.Vector;

/**
 * Represents a simple delivery chute device. The delivery chute has a finite
 * capacity of objects (pop cans or coins) that it can hold. This is obviously
 * not a realistic element of the simulation, but sufficient here.
 */
public class DeliveryChuteSimulator extends
        AbstractHardware<DeliveryChuteListener> implements
        AbstractCoinAcceptor, AbstractPopCanAcceptor {
    private Vector<Object> chute = new Vector<Object>();
    private int maxCapacity;

    /**
     * Creates a delivery cute with the indicated maximum capacity of pop cans
     * and/or coins.
     * 
     * @throws SimulationException
     *             if the capacity is not a positive integer.
     */
    public DeliveryChuteSimulator(int capacity) {
	if(capacity <= 0)
	    throw new SimulationException("Capacity must be a positive value: " + capacity);

	this.maxCapacity = capacity;
    }

    /**
     * Returns the maximum capacity of this delivery chute in number of pop cans
     * and/or coins that it can hold. Causes no events.
     */
    public int getCapacity() {
	return maxCapacity;
    }

    /**
     * Tells this delivery chute to deliver the indicated pop can. If the
     * delivery is successful, an "itemDelivered" event is announced to its
     * listeners. If the successful delivery causes the chute to become full, a
     * "chuteFull" event is announced to its listeners.
     * 
     * @throws CapacityExceededException
     *             if the chute is already full and the pop can cannot be
     *             delivered.
     * @throws DisabledException
     *             if the chute is currently disabled.
     */
    @Override
    public void acceptPop(PopCan pop) throws CapacityExceededException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(chute.size() >= maxCapacity)
	    throw new CapacityExceededException();

	chute.add(pop);

	notifyItemDelivered();

	if(chute.size() >= maxCapacity)
	    notifyChuteFull();
    }

    /**
     * Tells this delivery chute to deliver the indicated coin. If the delivery
     * is successful, an "itemDelivered" event is announced to its listeners. If
     * the successful delivery causes the chute to become full, a "chuteFull"
     * event is announced to its listeners.
     * 
     * @throws CapacityExceededException
     *             if the chute is already full and the coin cannot be
     *             delivered.
     * @throws DisabledException
     *             if the chute is currently disabled.
     */
    @Override
    public void acceptCoin(Coin coin) throws CapacityExceededException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();
	
	if(chute.size() >= maxCapacity)
	    throw new CapacityExceededException();

	chute.add(coin);

	notifyItemDelivered();

	if(chute.size() >= maxCapacity)
	    notifyChuteFull();
    }

    /**
     * Simulates the opening of the door of the delivery chute and the removal
     * of all items therein. Announces a "doorOpened" event to its listeners
     * before the items are removed, and a "doorClosed" event after the items
     * are removed.  Disabling the chute does not prevent this.
     * 
     * @return The items that were in the delivery chute.
     */
    public Object[] removeItems() {
	notifyDoorOpened();
	Object[] items = chute.toArray();
	chute.clear();
	notifyDoorClosed();
	return items;
    }

    /**
     * Determines whether this delivery chute has space for at least one more
     * item.  Causes no events.
     */
    @Override
    public boolean hasSpace() {
	return chute.size() < maxCapacity;
    }

    /**
     * Permits the delivery chute to be loaded with items without causing events
     * to be announced.
     * 
     * @throws SimulationException
     *             if the coins added exceed the capacity of the delivery chute.
     */
    public void loadWithoutEvents(Coin... coins) throws SimulationException {
	if(maxCapacity < chute.size() + coins.length)
	    throw new SimulationException("Capacity exceeded by attempt to load");

	for(Coin coin : coins) {
	    chute.add(coin);
	}
    }

    /**
     * Permits the delivery chute to be loaded with items without causing events
     * to be announced.
     * 
     * @throws SimulationException
     *             if the pop cans added exceed the capacity of the delivery
     *             chute.
     */
    public void loadWithoutEvents(PopCan... pops) throws SimulationException {
	if(maxCapacity < chute.size() + pops.length)
	    throw new SimulationException("Capacity exceeded by attempt to load");

	for(PopCan pop : pops) {
	    chute.add(pop);
	}
    }

    private void notifyItemDelivered() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { DeliveryChuteSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(DeliveryChuteListener.class, "itemDelivered", parameterTypes, args);
    }

    private void notifyDoorOpened() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { DeliveryChuteSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(DeliveryChuteListener.class, "doorOpened", parameterTypes, args);
    }

    private void notifyDoorClosed() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { DeliveryChuteSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(DeliveryChuteListener.class, "doorClosed", parameterTypes, args);
    }

    private void notifyChuteFull() {
	Class<?>[] parameterTypes =
	        new Class<?>[] { DeliveryChuteSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(DeliveryChuteListener.class, "chuteFull", parameterTypes, args);
    }
}
