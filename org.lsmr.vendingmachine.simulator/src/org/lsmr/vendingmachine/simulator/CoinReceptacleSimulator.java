package org.lsmr.vendingmachine.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * A temporary storage device for coins. A coin receptacle can be disabled to
 * prevent more coins from being placed within it. A coin receptacle has a
 * maximum capacity of coins that can be stored within it. A coin receptacle can
 * be connected to specialized channels depending on the denomination of each
 * coin (usually used for storing to coin racks) and another for the coin
 * return.
 */
public class CoinReceptacleSimulator extends AbstractHardware<CoinReceptacleListener> implements
        AbstractCoinAcceptor {
    private Vector<Coin> coinsEntered = new Vector<Coin>();
    private int maxCapacity;
    private CoinChannelSimulator coinReturn, other = null;
    private HashMap<Integer, CoinChannelSimulator> coinRacks =
	    new HashMap<Integer, CoinChannelSimulator>();

    /**
     * Creates a coin receptacle with the indicated capacity.
     * 
     * @throws SimulationException
     *             if the capacity is not a positive integer.
     */
    public CoinReceptacleSimulator(int capacity) {
	if(capacity <= 0)
	    throw new SimulationException("Capacity must be positive: " + capacity);

	maxCapacity = capacity;
    }

    /**
     * Connects the output channels for use by this receptacle. Causes no
     * events.
     * 
     * @param rackChannels
     *            One channel is expected for each valid denomination.
     * @param coinReturn
     *            This is used when coins are to be returned to the user.
     * @param other
     *            This is another channel that can be used to discard coins; it
     *            can be the same as the coin return channel.
     */
    public void connect(Map<Integer, CoinChannelSimulator> rackChannels,
	    CoinChannelSimulator coinReturn, CoinChannelSimulator other) {
	if(rackChannels == null)
	    this.coinRacks.clear();
	else
	    this.coinRacks.putAll(rackChannels);
	this.coinReturn = coinReturn;
	this.other = other;
    }

    /**
     * Loads the indicated coins into the receptacle without causing events to
     * be announced.
     * 
     * @throws SimulationException
     *             if the loading exceeds the capacity of the receptacle.
     */
    public void loadWithoutEvents(Coin... coins) throws SimulationException {
	if(maxCapacity < coinsEntered.size() + coins.length)
	    throw new SimulationException("Capacity exceeded by attempt to load");

	for(Coin coin : coins)
	    coinsEntered.add(coin);
    }

    /**
     * Causes the indicated coin to be added to the receptacle if it has space.
     * A successful addition causes a "coinAdded" event to be announced to its
     * listeners. If a successful addition causes the receptacle to become full,
     * it will also announce a "coinsFull" event to its listeners.
     * 
     * @throws CapacityExceededException
     *             if the receptacle has no space.
     * @throws DisabledException
     *             if the receptacle is disabled.
     */
    public void acceptCoin(Coin coin) throws CapacityExceededException, DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(coinsEntered.size() >= maxCapacity)
	    throw new CapacityExceededException();

	coinsEntered.add(coin);

	notifyCoinAdded(coin);

	if(coinsEntered.size() >= maxCapacity)
	    notifyCoinsFull();
    }

    /**
     * Causes the receptacle to attempt to move its coins to the coin racks. Any
     * coins that either do not fit in the coin racks or for which no coin rack
     * exists are delivered to the "other" channel, which might be another
     * permanent storage receptacle, a coin return, etc. A successful storage
     * will cause a "coinsRemoved" event to be announced to its listeners.
     * 
     * @throws CapacityExceededException
     *             if one of the output channels fails to accept the coin.
     * @throws DisabledException
     *             if the receptacle is disabled.
     */
    public void storeCoins() throws CapacityExceededException, DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	for(Coin coin : coinsEntered) {
	    CoinChannelSimulator ccs = coinRacks.get(new Integer(coin.getValue()));

	    if(ccs != null && ccs.hasSpace())
		ccs.deliver(coin);
	    else if(other != null) {
		if(other.hasSpace())
		    other.deliver(coin);
		else
		    throw new CapacityExceededException();
	    }
	    else
		throw new SimulationException("The 'other' output channel has not been defined, but it is needed for storage.");
	}

	if(!coinsEntered.isEmpty()) {
	    coinsEntered.clear();
	    notifyCoinsRemoved();
	}
    }

    /**
     * Instructs this coin receptacle to return all of its coins to the user. If
     * any coins are returned, a "coinsReturned" event will be announced to its
     * listeners.
     * 
     * @throws CapacityExceededException
     *             if the coin return is overfull.
     * @throws DisabledException
     *             if the receptacle is disabled.
     */
    public void returnCoins() throws CapacityExceededException, DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	for(Coin coin : coinsEntered)
	    coinReturn.deliver(coin);

	if(!coinsEntered.isEmpty()) {
	    coinsEntered.clear();

	    notifyCoinsRemoved();
	}
    }

    /**
     * Returns whether this coin receptacle has enough space to accept at least
     * one more coin. Causes no events.
     */
    @Override
    public boolean hasSpace() {
	return coinsEntered.size() < maxCapacity;
    }

    private void notifyCoinAdded(Coin coin) {
	Class<?>[] parameterTypes = new Class<?>[] { CoinReceptacleSimulator.class, Coin.class };
	Object[] args = new Object[] { this, coin };
	notifyListeners(CoinReceptacleListener.class, "coinAdded", parameterTypes, args);
    }

    private void notifyCoinsRemoved() {
	Class<?>[] parameterTypes = new Class<?>[] { CoinReceptacleSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CoinReceptacleListener.class, "coinsRemoved", parameterTypes, args);
    }

    private void notifyCoinsFull() {
	Class<?>[] parameterTypes = new Class<?>[] { CoinReceptacleSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CoinReceptacleListener.class, "coinsFull", parameterTypes, args);
    }
}
