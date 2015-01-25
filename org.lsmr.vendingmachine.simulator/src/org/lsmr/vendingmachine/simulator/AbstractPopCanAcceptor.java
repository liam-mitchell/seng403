package org.lsmr.vendingmachine.simulator;

/**
 * A simple interface to allow a device to communicate with another device that
 * accepts pop cans.
 */
public interface AbstractPopCanAcceptor {
    /**
     * Instructs the device to take the pop can as input.
     * 
     * @param pop
     *            The pop can to be taken as input.
     * @throws CapacityExceededException
     *             if the device does not have enough space for the pop can.
     * @throws DisabledException
     *             if the device is currently disabled.
     */
    public void acceptPop(PopCan pop) throws CapacityExceededException,
	    DisabledException;
}
