package org.lsmr.vendingmachine.simulator;

/**
 * A simple data class representing the information read from a magnetic stripe
 * card.
 * <p>
 * The vending machine is only capable of recognizing 3 kinds of cards: Visa and
 * Mastercard credit cards, and prepaid cards. All others will register as
 * "unknown".
 */
public class Card {
    /**
     * The types of cards recognized by the vending machine, plus "unknown".
     */
    public enum CardType {
	CREDIT, PREPAID, DEBIT, UNKNOWN
    }

    private String number, name, pin;
    private CardType type;
    private int maxAmount;

    /**
     * Basic constructor.
     * 
     * @throws SimulationException
     *             if any of the arguments is null.
     */
    public Card(CardType type, String number, String name, String pin, int maxAmount) {
	if(type == null || number == null || name == null || pin == null)
	    throw new SimulationException("The arguments may not be null");

	this.type = type;
	this.number = number;
	this.name = name;
	this.pin = pin;
	this.maxAmount = maxAmount;
    }

    /**
     * Returns the card type of the card.
     */
    public CardType getType() {
	return type;
    }

    /**
     * Returns the number recorded on the card.
     */
    public String getNumber() {
	return number;
    }

    /**
     * Returns the name recorded on the card.
     */
    public String getName() {
	return name;
    }

    /**
     * Tests whether a given PIN conforms to what is stored on the card.
     */
    public boolean checkPin(String pin) {
	if(this.pin.equals(pin))
	    return true;

	return false;
    }

    /**
     * Request the indicated amount from the card. If the card is a credit or
     * debit card, this simulates contacting the banking authority and
     * requesting the amount; if it is a prepaid card, the money is deducted
     * from the total on the card. Credit and debit cards use a PIN check as
     * well; this is ignored for prepaid cards.
     * 
     * @param amountInCents
     *            The amount requested, in cents.
     * @return true if and only if the amount requested is available.
     */
    public boolean requestFunds(int amountInCents, String pin) {
	switch(type) {
	case CREDIT:
	case DEBIT:
	    if(amountInCents <= maxAmount && checkPin(pin))
		return true;
	    else
		return false;
	case PREPAID:
	    if(amountInCents <= maxAmount) {
		maxAmount -= amountInCents;
		return true;
	    }
	    else
		return false;
	default:
	    return false;
	}
    }
}
