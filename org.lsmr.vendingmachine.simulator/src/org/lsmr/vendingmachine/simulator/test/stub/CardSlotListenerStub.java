package org.lsmr.vendingmachine.simulator.test.stub;

import org.lsmr.vendingmachine.simulator.CardSlotListener;
import org.lsmr.vendingmachine.simulator.CardSlotSimulator;

public class CardSlotListenerStub extends AbstractStub implements
        CardSlotListener {
    @Override
    public void cardInserted(CardSlotSimulator slot) {
	recordCallTo("cardInserted");
    }

    @Override
    public void cardEjected(CardSlotSimulator slot) {
	recordCallTo("cardEjected");
    }
}
