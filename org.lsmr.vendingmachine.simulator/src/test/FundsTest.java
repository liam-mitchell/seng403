package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.funds.CoinsAvailable;
import org.lsmr.vendingmachine.funds.CoinsAvailableListener;
import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.funds.DebitAvailable;
import org.lsmr.vendingmachine.funds.Funds;
import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.Card;
import org.lsmr.vendingmachine.simulator.CardSlotListener;
import org.lsmr.vendingmachine.simulator.CardSlotNotEmptyException;
import org.lsmr.vendingmachine.simulator.CardSlotSimulator;
import org.lsmr.vendingmachine.simulator.CoinRackSimulator;
import org.lsmr.vendingmachine.simulator.CoinReceptacleSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.Card.CardType;

public class FundsTest {
    private Funds funds;
    private CardSlotSimulator slot;
    private CoinsAvailableStub cas;
    private DebitAvailableStub das;

    class CoinsAvailableStub extends CoinsAvailable {
	public Currency coinsAvailable;
	public boolean throwInStore = false;
	
	public CoinsAvailableStub(CoinReceptacleSimulator cr, int[] rackDenominations,
	        CoinRackSimulator[] racks) {
	    super(cr, rackDenominations, racks);
	}

	@Override
	public void storeCoins(Currency curr) {
	    if(throwInStore)
		throw new RuntimeException();
	    
	    super.notifyCoinsStored(curr.getQuantity().intValue());
	}
	
	@Override
	public void returnCoins(Currency curr) {
	    super.notifyCoinsReturned(curr);
	}
	
	@Override
	public Currency getCoinsAvailable() {
	    return coinsAvailable;
	}
    }

    class DebitAvailableStub extends DebitAvailable {
	public boolean throwInRequest = false;
	public boolean requestResult = false;
	public boolean passOnRequest = false;

	public DebitAvailableStub(CardSlotSimulator slot) {
	    super(slot);
	}

	@Override
	public boolean requestFunds(Currency amount, String pin) {
	    if(throwInRequest)
		throw new RuntimeException();

	    if(passOnRequest)
		return super.requestFunds(amount, pin);

	    return requestResult;
	}
    }

    @Before
    public void setup() {
	slot = new CardSlotSimulator();
	cas =
	        new CoinsAvailableStub(new CoinReceptacleSimulator(1), new int[] { 5 }, new CoinRackSimulator[] { new CoinRackSimulator(1) });
	das = new DebitAvailableStub(slot);

	funds = new Funds(cas, das);
    }

    @After
    public void teardown() {
	cas = null;
	das = null;
	funds = null;
    }

    @Test
    public void testStoreFundsCoinsExact() {
	final int amount = 10;
	final Currency currency =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(amount));
	cas.coinsAvailable = currency;
	das.throwInRequest = true;

	cas.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		if(amount != curr.getQuantity().intValue())
		    throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	funds.storeFunds(currency, "");
    }

    class MutableBoolean {
	public boolean value;

	public MutableBoolean(boolean v) {
	    value = v;
	}

	public void set() {
	    value = true;
	}
    }

    @Test
    public void testStoreFundsMoreGood() throws CardSlotNotEmptyException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	final int amount = 10, fullAmount = 15;
	final Currency currency =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(amount)), fullCurrency =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(fullAmount));
	cas.coinsAvailable = currency;
	das.passOnRequest = true;
	slot.insertCard(new Card(CardType.DEBIT, "123", "Foo", "123", 10));

	cas.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		flag.set();
		if(amount != curr.getQuantity().intValue())
		    throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	assertEquals(true, funds.storeFunds(fullCurrency, "123"));
	assertEquals(true, flag.value);
    }

    @Test
    public void testStoreFundsMoreBad() throws CardSlotNotEmptyException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	final int amount = 10, fullAmount = 15;
	final Currency currency =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(amount)), fullCurrency =
	        new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(fullAmount));
	cas.coinsAvailable = currency;
	das.passOnRequest = true;
	slot.insertCard(new Card(CardType.DEBIT, "123", "Foo", "123", 4));

	cas.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		flag.set();
		if(amount != curr.getQuantity().intValue())
		    throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	assertEquals(false, funds.storeFunds(fullCurrency, "123"));
	assertEquals(false, flag.value);
    }
    
    @Test
    public void testReleaseRemainder() throws CardSlotNotEmptyException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	cas.coinsAvailable = new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0));
	slot.insertCard(new Card(CardType.DEBIT, "", "", "", 0));
	
	cas.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		if(curr.getQuantity().intValue() != 0)
		    throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});
	slot.register(new CardSlotListener() {
	    @Override
	    public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void cardInserted(CardSlotSimulator slot) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void cardEjected(CardSlotSimulator slot) {
		flag.set();
	    }
	});

	funds.releaseRemainingFunds();
	assertEquals(true, flag.value);
    }
}
