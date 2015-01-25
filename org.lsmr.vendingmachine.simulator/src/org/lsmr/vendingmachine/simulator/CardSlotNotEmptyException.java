package org.lsmr.vendingmachine.simulator;

/**
 * Represents the situation when an attempt is made to insert a card when the
 * slot is already occupied.
 */
@SuppressWarnings("serial")
public class CardSlotNotEmptyException extends Exception {}
