package org.lsmr.vendingmachine.simulator;

/**
 * Represents a simple device (like, say, a tube) that allows coins to move
 * between other devices.
 */
public class CoinChannelSimulator {
    private AbstractCoinAcceptor sink;

    /**
     * Constructs a new coin channel whose output is connected to the indicated
     * sink.
     */
    public CoinChannelSimulator(AbstractCoinAcceptor sink) {
	this.sink = sink;
    }

    /**
     * Moves the indicated coin to the sink. This method should be called by the
     * source device, and not by an external application.
     * 
     * @throws CapacityExceededException
     *             if the sink has no space for the coin.
     * @throws DisabledException
     *             if the sink is currently disabled.
     */
    public void deliver(Coin coin) throws CapacityExceededException,
	    DisabledException {
	getSink().acceptCoin(coin);
    }

    /**
     * Returns whether the sink has space for at least one more coin.
     */
    public boolean hasSpace() {
	return getSink().hasSpace();
    }

    /**
     * Returns the sink to which this channel is connected.
     */
    public AbstractCoinAcceptor getSink() {
	return sink;
    }
}
