package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.product.ProductKind;
import org.lsmr.vendingmachine.product.ProductKindListener;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;
import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.PopCanChannelSimulator;
import org.lsmr.vendingmachine.simulator.PopCanRackSimulator;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;


public class ProductKindTest {
    private ProductKind pk;
    private PopCanRackStub rack;
    private SelectionButtonSimulator button;

    class PopCanRackStub extends PopCanRackSimulator {
	public boolean throwEmptyInDispense;
	public boolean throwCapacityExceededInDispense;
	
	public PopCanRackStub(int capacity) {
	    super(capacity);
        }
	
	@Override
	public void dispensePop() throws EmptyException, DisabledException, CapacityExceededException {
	    if(throwEmptyInDispense)
		throw new EmptyException();
	    
	    if(throwCapacityExceededInDispense)
		throw new CapacityExceededException();
	    
	    super.dispensePop();
	}
    }
    
    class PopCanChannelStub extends PopCanChannelSimulator {
	public PopCan received = null;

	public PopCanChannelStub() {
	    super(null);
	}

	public void acceptPop(PopCan pop) throws CapacityExceededException, DisabledException {
	    received = pop;
	}
    }

    @Before
    public void setup() {
	button = new SelectionButtonSimulator();
	rack = new PopCanRackStub(10);
	rack.connect(new PopCanChannelStub());
	pk = new ProductKind("Foo", new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(200)), rack, button);
    }

    @After
    public void teardown() {
	rack = null;
	pk = null;
    }

    @Test
    public void testGetters() {
	assertEquals(0, pk.getQuantityAvailable());
	assertEquals("Foo", pk.getName());
	assertEquals(200, pk.getStandardCost().getQuantity().intValue());
    }

    static class MutableBoolean {
	public boolean value;

	public MutableBoolean(boolean value) {
	    this.value = value;
	}

	public void set() {
	    value = true;
	}
    }

    @Test
    public void testDispense() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	rack.addPop(new PopCan());

	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		flag.set();
	    }

	    @Override
	    public void hardwareFailure(ProductKind pk) {
		throw new RuntimeException();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});

	assertEquals(1, pk.getQuantityAvailable());
	pk.dispense();
	assertEquals(0, pk.getQuantityAvailable());
	assertEquals(true, flag.value);
    }

    @Test
    public void testDispenseEmptyRack() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	rack.addPop(new PopCan());
	rack.throwEmptyInDispense = true;

	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }

	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});

	assertEquals(1, pk.getQuantityAvailable());
	pk.dispense();
	assertEquals(0, pk.getQuantityAvailable());
	assertEquals(true, flag.value);
    }

    @Test
    public void testDispenseOverfullChute() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	rack.addPop(new PopCan());
	rack.throwCapacityExceededInDispense = true;

	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }

	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});

	assertEquals(1, pk.getQuantityAvailable());
	pk.dispense();
	assertEquals(1, pk.getQuantityAvailable());
	assertEquals(true, flag.value);
    }

    @Test
    public void testDispenseDisabled() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	rack.addPop(new PopCan());
	rack.disable();

	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }

	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});

	assertEquals(1, pk.getQuantityAvailable());
	pk.dispense();
	assertEquals(1, pk.getQuantityAvailable());
	assertEquals(true, flag.value);
    }

    @Test
    public void testBadDispense() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }

	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});

	assertEquals(0, pk.getQuantityAvailable());
	pk.dispense();
	assertEquals(true, flag.value);
    }
    
    @Test
    public void testEventsToIgnore() {
	pk.popFull(null);
	pk.enabled(null);
	pk.disabled(null);
    }
    
    @Test
    public void testBadPopAdd() {
	final MutableBoolean flag = new MutableBoolean(false);
	
	pk.register(new ProductKindListener() {
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }

	    @Override
            public void selected(ProductKind pk) {
		throw new RuntimeException();
            }
	});
	
	pk.popAdded(null, null);
	assertEquals(true, flag.value);
    }
    
    @Test
    public void testSelected() {
	final MutableBoolean flag = new MutableBoolean(false);
	
	pk.register(new ProductKindListener() {
	    @Override
	    public void selected(ProductKind pk) {
		flag.set();
	    }
	    
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void hardwareFailure(ProductKind pk) {
		throw new RuntimeException();
	    }
	});
	
	button.press();
	assertEquals(true, flag.value);
    }
    
    @Test
    public void testSelectedBadButton() {
	final MutableBoolean flag = new MutableBoolean(false);
	
	pk.register(new ProductKindListener() {
	    @Override
	    public void selected(ProductKind pk) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void outOfStock(ProductKind pk) {
		throw new RuntimeException();
	    }
	    
	    @Override
	    public void hardwareFailure(ProductKind pk) {
		flag.set();
	    }
	});
	
	pk.pressed(null);
	assertEquals(true, flag.value);
    }
}
