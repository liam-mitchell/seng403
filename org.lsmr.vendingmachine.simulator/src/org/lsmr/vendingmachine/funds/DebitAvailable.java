package org.lsmr.vendingmachine.funds;

import java.util.ArrayList;

import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.Card;
import org.lsmr.vendingmachine.simulator.CardSlotListener;
import org.lsmr.vendingmachine.simulator.CardSlotSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;
import org.lsmr.vendingmachine.simulator.Card.CardType;

public class DebitAvailable implements CardSlotListener {
    private ArrayList<DebitAvailableListener> listeners = new ArrayList<DebitAvailableListener>();
    private boolean cardIsPresent = false;
    private CardSlotSimulator slot;

    public DebitAvailable(CardSlotSimulator slot) {
	this.slot = slot;
	slot.register(this);
    }

    public void register(DebitAvailableListener listener) {
	listeners.add(listener);
    }

    public boolean isDebitAvailable() {
	return cardIsPresent;
    }
    
    public void returnCard() {
	try {
	    slot.ejectCard();
        }
        catch(EmptyException e) {
	    notifyHardwareFailure();
        }
        catch(DisabledException e) {
            notifyHardwareFailure();
        }
    }
    
    @Override
    public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {}

    @Override
    public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {}

    @Override
    public void cardInserted(CardSlotSimulator slot) {
	if(slot != this.slot) {
	    notifyHardwareFailure();
	    return;
	}

	try {
	    if(slot.readCardData().getType() == CardType.DEBIT)
	        cardIsPresent = true;
        }
        catch(EmptyException e) {
	    notifyHardwareFailure();
        }
    }

    public boolean requestFunds(Currency amount, String pin) {
	if(!cardIsPresent) {
	    notifyHardwareFailure();
	    return false;
	}

	try {
	    Card card = slot.readCardData();
	    
	    return card.requestFunds(amount.getQuantity().intValue(), pin);
        }
        catch(EmptyException e) {
	    notifyHardwareFailure();
	    return false;
        }
    }
    
    @Override
    public void cardEjected(CardSlotSimulator slot) {
	if(slot != this.slot) {
	    notifyHardwareFailure();
	    return;
	}
	cardIsPresent = false;
    }
    
    private void notifyHardwareFailure() {
	for(DebitAvailableListener l : listeners)
	    l.hardwareFailure(this);
    }
}
