package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.DeliveryChuteListener;
import org.lsmr.vendingmachine.simulator.DeliveryChuteSimulator;

public class DeliveryChuteListenerStub extends AbstractStub implements
        DeliveryChuteListener {
    @Override
    public void itemDelivered(DeliveryChuteSimulator chute) {
	recordCallTo("itemDelivered");
    }

    @Override
    public void doorOpened(DeliveryChuteSimulator chute) {
	recordCallTo("doorOpened");
    }

    @Override
    public void doorClosed(DeliveryChuteSimulator chute) {
	recordCallTo("doorClosed");
    }

    @Override
    public void chuteFull(DeliveryChuteSimulator chute) {
	recordCallTo("chuteFull");
    }
}
