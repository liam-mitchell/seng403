package org.lsmr.vendingmachine.funds;

import java.math.BigDecimal;

public class Currency {
    private java.util.Currency kind;
    private BigDecimal quantity;

    public Currency(java.util.Currency kind, BigDecimal value) throws IllegalArgumentException {
	if(kind == null || value == null || value.compareTo(BigDecimal.ZERO) < 0)
	    throw new IllegalArgumentException();
	
	this.kind = kind;
	quantity = value;
    }

    public void add(Currency other) throws RequiresCurrencyExchangeException {
	if(other.kind != kind)
	    throw new RequiresCurrencyExchangeException();
	
	quantity = quantity.add(other.quantity);
    }

    public void subtract(Currency other) throws RequiresCurrencyExchangeException {
	if(other.kind != kind)
	    throw new RequiresCurrencyExchangeException();
	
	if(other.quantity.compareTo(quantity) > 0)
	    throw new IllegalArgumentException();
	
	quantity = quantity.subtract(other.quantity);
    }
    
    public BigDecimal getQuantity() {
	return quantity;
    }
    
    public void setQuantity(BigDecimal q) throws IllegalArgumentException {
	if(q.compareTo(BigDecimal.ZERO) < 0)
	    throw new IllegalArgumentException();
	quantity = q;
    }
    
    public java.util.Currency getKind() {
	return kind;
    }
}
