package org.lsmr.vendingmachine.simulator;

/**
 * An exception that can be raised when the behaviour within the simulator makes
 * no sense, typically when it has not been configured correctly. This is
 * different from an exception being raised because the preconditions of a
 * component are violated. For example, it makes no sense to move a coin from
 * the coin slot to the delivery chute if the delivery chute does not exist or
 * is not connected to the coin slot; this would cause a SimulationException.
 * <p>
 * Sometimes, the SimulationException wraps another exception, called the nested
 * exception.
 */

@SuppressWarnings("serial")
public class SimulationException extends RuntimeException {
    private String nested;

    public SimulationException(Exception nested) {
	this.nested = nested.toString();
    }

    public SimulationException(String msg) {
	nested = msg;
    }

    public String toString() {
	return "Nested exception: " + nested;
    }
}
