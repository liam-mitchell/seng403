package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;




import org.junit.Test;
import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.funds.RequiresCurrencyExchangeException;


public class CurrencyTest {
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBothNull() {
	new Currency(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorValueNull() {
	new Currency(java.util.Currency.getInstance(Locale.CANADA), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeValue() {
	new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(-1));
    }

    @Test
    public void testConstructorZeroValue() {
	new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0));
    }

    @Test
    public void testAddZeroAndZero() throws RequiresCurrencyExchangeException {
	Currency c1 =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0));
	Currency c2 =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0));
	c1.add(c2);
	assertEquals(0, c1.getQuantity().intValue());
    }

    @Test(expected = RequiresCurrencyExchangeException.class)
    public void testBadAdd() throws RequiresCurrencyExchangeException {
	Currency c1 =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0));
	Currency c2 = new Currency(java.util.Currency.getInstance(Locale.US), new BigDecimal(0));
	c1.add(c2);
    }

    @Test
    public void testGetKind() {
	java.util.Currency kind = java.util.Currency.getInstance(Locale.CANADA);
	Currency c1 = new Currency(kind, new BigDecimal(0));
	assertEquals(kind, c1.getKind());
    }

    @Test
    public void testSetQuantityZero() {
	java.util.Currency kind = java.util.Currency.getInstance(Locale.CANADA);
	Currency c1 = new Currency(kind, new BigDecimal(0));
	c1.setQuantity(new BigDecimal(0));
	assertEquals(0, c1.getQuantity().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetQuantityNegative() {
	java.util.Currency kind = java.util.Currency.getInstance(Locale.CANADA);
	Currency c1 = new Currency(kind, new BigDecimal(0));
	c1.setQuantity(new BigDecimal(-1));
    }

    @Test
    public void testSubtract() throws RequiresCurrencyExchangeException {
	java.util.Currency kind = java.util.Currency.getInstance(Locale.CANADA);
	Currency c1 = new Currency(kind, new BigDecimal(1));
	Currency c2 = new Currency(kind, new BigDecimal(1));
	c1.subtract(c2);
	assertEquals(0, c1.getQuantity().intValue());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSubtractTooMuch() throws RequiresCurrencyExchangeException {
	java.util.Currency kind = java.util.Currency.getInstance(Locale.CANADA);
	Currency c1 = new Currency(kind, new BigDecimal(1));
	Currency c2 = new Currency(kind, new BigDecimal(2));
	c1.subtract(c2);
    }

    @Test(expected = RequiresCurrencyExchangeException.class)
    public void testBadSubtract() throws RequiresCurrencyExchangeException {
	Currency c1 =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(1));
	Currency c2 = new Currency(java.util.Currency.getInstance(Locale.US), new BigDecimal(1));
	c1.subtract(c2);
    }
}
