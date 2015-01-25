package org.lsmr.vendingmachine.simulator;

/**
 * Listens for events emanating from an indicator light.
 */
public interface IndicatorLightSimulatorListener extends
        AbstractHardwareListener {
    /**
     * An event that is announced when the indicated light has been activated
     * (turned on).
     */
    void activated(IndicatorLightSimulator light);

    /**
     * An event that is announced when the indicated light has been deactivated
     * (turned off).
     */
    void deactivated(IndicatorLightSimulator light);
}
