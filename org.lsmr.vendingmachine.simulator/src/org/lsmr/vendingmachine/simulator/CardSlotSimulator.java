package org.lsmr.vendingmachine.simulator;

/**
 * Represents a device that accepts and reads magnetic stripe cards (credit
 * cards, debit cards, etc.).
 */
public class CardSlotSimulator extends AbstractHardware<CardSlotListener> {
    private Card card = null;

    /**
     * Inserts a card into the card slot. If successful, announces a
     * "cardInserted" event to its listeners.
     * 
     * @throws CardSlotNotEmptyException
     *             if the card slot already contains a card.
     * @throws DisabledException
     *             if the card slot is currently disabled.
     */
    public void insertCard(Card cs) throws CardSlotNotEmptyException,
	    DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(card != null)
	    throw new CardSlotNotEmptyException();

	card = cs;

	notifyCardInserted();
    }

    /**
     * Ejects the card from the card slot. If successful, announces a
     * "cardEjected" event to its listeners.
     * 
     * @throws EmptyException
     *             if the card slot does not contain a card.
     * @throws DisabledException
     *             if the card slot is currently disabled.
     */
    public void ejectCard() throws EmptyException, DisabledException {
	if(isDisabled())
	    throw new DisabledException();

	if(card == null)
	    throw new EmptyException();

	card = null;

	notifyCardEjected();
    }

    /**
     * Reads the data stored on the card that is currently inserted. Causes no
     * events to be announced.
     * 
     * @throws EmptyException
     *             if there is no card currently in the card slot.
     */
    public Card readCardData() throws EmptyException {
	if(card == null)
	    throw new EmptyException();

	return card;
    }

    /**
     * Allows a card to be loaded into the slot without causing events to be
     * announced.
     * 
     * @param card
     */
    public void loadWithoutEvents(Card card) {
	this.card = card;
    }

    private void notifyCardInserted() {
	Class<?>[] parameterTypes = new Class<?>[] { CardSlotSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CardSlotListener.class, "cardInserted", parameterTypes, args);
    }

    private void notifyCardEjected() {
	Class<?>[] parameterTypes = new Class<?>[] { CardSlotSimulator.class };
	Object[] args = new Object[] { this };
	notifyListeners(CardSlotListener.class, "cardEjected", parameterTypes, args);
    }
}
