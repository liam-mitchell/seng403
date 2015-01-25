package org.lsmr.vendingmachine.simulator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a device that stores coins of a particular denomination to
 * dispense them as change.
 * <p>
 * Coin racks can receive coins from other sources. To simplify the simulation,
 * no check is performed on the value of each coin, meaning it is an external
 * responsibility to ensure the correct routing of coins.
 */
public class CoinRackSimulator extends AbstractHardware<CoinRackListener>
        implements AbstractCoinAcceptor {
    private int maxCapacity;
    private Queue<Coin> queue = new LinkedList<Coin>();
    private CoinChannelSimulator sink;

    /**
     * Creates a coin rack with the indicated maximum capacity.
     * 
     * @throws SimulationException
     *             if capacity is not positive.
     */
    public CoinRackSimulator(int capacity) {
	if(capacity <= 0)
	    throw new SimulationException("Capacity must be positive: " + capacity);
	this.maxCapacity = capacity;
    }

    /**
     * Allows a set of coins to be loaded into the rack without events being
     * announced. Existing coins in the rack are not removed.
     * 
     * @throws SimulationException
     *             if the number of coins to be loaded exceeds the capacity of
     *             the rack.
     */
    public void loadWithoutEvents(Coin... coins) throws SimulationException {
	if(maxCapacity < queue.size() + coins.length)
	    throw new SimulationException("Capacity of rack is exceeded by load");

	for(Coin coin : coins)
	    queue.add(coin);
    }

    /**
     * Connects an output channel to this coin rack. Any existing output
     * channels are disconnected.  Causes no events to be announced.
     */
    public void connect(CoinChannelSimulator sink) {
	this.sink = sink;
    }

    /**
     * Returns the maximum capacity of this coin rack.
     */
    public int getCapacity() {
	return maxCapacity;
    }

    /**
     * Causes the indicated coin to be added into the rack. If successful, a
     * "coinAdded" event is announced to its listeners. If a successful coin
     * addition causes the rack to become full, a "coinsFull" event is announced
     * to its listeners.
     * 
     * @throws DisabledException
     *             if the coin rack is currently disabled.
     * @throws CapacityExceededException
     *             if the coin rack is already full.
     */
    @Override
    public void acceptCoin(Coin coin) throws CapacityExceededException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(queue.size() >= maxCapacity)
	    throw new CapacityExceededException();

	queue.add(coin);
	notifyCoinAdded(coin);

	if(queue.size() >= maxCapacity)
	    notifyCoinsFull();
    }

    /**
     * Releases a single coin from this coin rack. If successful, a
     * "coinRemoved" event is announced to its listeners. If a successful coin
     * removal causes the rack to become empty, a "coinsEmpty" event is
     * announced to its listeners.
     * 
     * @throws CapacityExceededException
     *             if the output channel is unable to accept another coin.
     * @throws EmptyException
     *             if no coins are present in the rack to release.
     * @throws DisabledException
     *             if the rack is currently disabled.
     */
    public void releaseCoin() throws CapacityExceededException, EmptyException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(queue.size() == 0)
	    throw new EmptyException();

	Coin coin = queue.remove();

	notifyCoinRemoved(coin);
	sink.deliver(coin);

	if(queue.isEmpty())
	    notifyCoinsEmpty();
    }

    /**
     * Returns whether this coin rack has enough space to accept at least one
     * more coin. Announces no events.
     */
    @Override
    public boolean hasSpace() {
	return queue.size() < maxCapacity;
    }

    private void notifyCoinAdded(Coin coin) {
	Class<?>[] parameterTypes =
	        new Class<?>[] { CoinRackSimulator.class, Coin.class };
	Object[] args = new Object[] { this, coin };
	notifyListeners(CoinRackListener.class, "coinAdded", parameterTypes, args);
    }

    private void notifyCoinRemoved(Coin coin) {
	Class<?>[] parameterTypes =
	        new Class<?>[] { CoinRackSimulator.class, Coin.class };
	Object[] args = new Object[] { this, coin };
	notifyListeners(CoinRackListener.class, "coinRemoved", parameterTypes, args);
    }

    private void notifyCoinsFull() {
	Class<?>[] parameterTypes = new Class<?>[] { CoinRackSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CoinRackListener.class, "coinsFull", parameterTypes, args);
    }

    private void notifyCoinsEmpty() {
	Class<?>[] parameterTypes = new Class<?>[] { CoinRackSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CoinRackListener.class, "coinsEmpty", parameterTypes, args);
    }
}
