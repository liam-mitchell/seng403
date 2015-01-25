package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.funds.DebitAvailable;
import org.lsmr.vendingmachine.funds.DebitAvailableListener;
import org.lsmr.vendingmachine.simulator.Card;
import org.lsmr.vendingmachine.simulator.CardSlotNotEmptyException;
import org.lsmr.vendingmachine.simulator.CardSlotSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;
import org.lsmr.vendingmachine.simulator.Card.CardType;

public class DebitAvailableTest {
    private DebitAvailable da;
    private CardSlotStub slot;

    class CardSlotStub extends CardSlotSimulator {
	public boolean throwEmptyInRead = false;
	
	public void insertCard(Card cs) throws CardSlotNotEmptyException, DisabledException {
	    super.insertCard(cs);
	}

	public void ejectCard() throws EmptyException, DisabledException {
	    super.ejectCard();
	}
	
	@Override
	public Card readCardData() throws EmptyException {
	    if(throwEmptyInRead)
		throw new EmptyException();
	    return super.readCardData();
	}
    }

    @Before
    public void setup() {
	slot = new CardSlotStub();
	da = new DebitAvailable(slot);
    }

    @After
    public void teardown() {
	slot = null;
	da = null;
    }

    @Test
    public void testIsDebitAvailableTrue() throws CardSlotNotEmptyException, DisabledException {
	slot.insertCard(new Card(CardType.DEBIT, "123", "FOO", "123", 0));
	assertEquals(true, da.isDebitAvailable());
    }

    @Test
    public void testIsDebitAvailableEmpty() throws CardSlotNotEmptyException, DisabledException {
	assertEquals(false, da.isDebitAvailable());
    }

    @Test
    public void testIsDebitAvailableCredit() throws CardSlotNotEmptyException, DisabledException {
	slot.insertCard(new Card(CardType.CREDIT, "123", "FOO", "123", 0));
	assertEquals(false, da.isDebitAvailable());
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
    public void testReturnCardEmpty() {
	final MutableBoolean flag = new MutableBoolean(false);

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.returnCard();
	assertEquals(true, flag.value);
    }

    @Test
    public void testReturnCardDisabledEmpty() {
	final MutableBoolean flag = new MutableBoolean(false);
	slot.disable();

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.returnCard();
	assertEquals(true, flag.value);
    }

    @Test
    public void testReturnCardDisabled() throws CardSlotNotEmptyException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	slot.insertCard(new Card(CardType.DEBIT, "123", "FOO", "123", 0));
	slot.disable();

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.returnCard();
	assertEquals(true, flag.value);
    }

    @Test
    public void testReturnCardGood() throws CardSlotNotEmptyException, DisabledException {
	slot.insertCard(new Card(CardType.CREDIT, "123", "FOO", "123", 0));
	slot.enable();

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		throw new RuntimeException();
	    }
	});

	da.returnCard();
    }

    @Test
    public void testCardInsertionBadSlot() {
	final MutableBoolean flag = new MutableBoolean(false);

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.cardInserted(null);
	assertEquals(true, flag.value);
    }

    @Test
    public void testBadCardInsertion() {
	final MutableBoolean flag = new MutableBoolean(false);

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.cardInserted(slot);
	assertEquals(true, flag.value);
    }

    @Test
    public void testBadCardEjection() {
	final MutableBoolean flag = new MutableBoolean(false);

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.cardEjected(null);
	assertEquals(true, flag.value);
    }

    @Test
    public void testBadRequest() {
	final MutableBoolean flag = new MutableBoolean(false);

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	da.requestFunds(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0)), "");
	assertEquals(true, flag.value);
    }

    @Test
    public void testRequestEmpty() throws CardSlotNotEmptyException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	slot.insertCard(new Card(CardType.DEBIT, "123", "FOO", "123", 0));
	slot.throwEmptyInRead = true;

	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		flag.set();
	    }
	});

	assertEquals(false, da.requestFunds(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0)), ""));
	assertEquals(true, flag.value);
    }

    @Test
    public void testRequestBadPin() throws CardSlotNotEmptyException, DisabledException {
	slot.insertCard(new Card(CardType.DEBIT, "123", "FOO", "123", 0));
	
	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		throw new RuntimeException();
	    }
	});

	assertEquals(false, da.requestFunds(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0)), ""));
    }

    @Test
    public void testRequestGoodPin() throws CardSlotNotEmptyException, DisabledException {
	slot.insertCard(new Card(CardType.DEBIT, "123", "FOO", "123", 0));
	
	da.register(new DebitAvailableListener() {
	    @Override
	    public void hardwareFailure(DebitAvailable da) {
		throw new RuntimeException();
	    }
	});

	assertEquals(true, da.requestFunds(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(0)), "123"));
    }
}
