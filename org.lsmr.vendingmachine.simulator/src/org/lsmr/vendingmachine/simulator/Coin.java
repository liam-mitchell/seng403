package org.lsmr.vendingmachine.simulator;

/**
 * A simple representation of a coin.
 */
public class Coin {
    private int value;

    /**
     * Creates a coin with the specified value in cents.
     * 
     * @throws SimulationException
     *             if the value is not positive.
     */
    public Coin(int value) {
	if(value <= 0)
	    throw new SimulationException("The value of the coin must be positive: " + value);

	this.value = value;
    }

    /**
     * Returns the value of the coin in cents.
     */
    public int getValue() {
	return value;
    }
}
