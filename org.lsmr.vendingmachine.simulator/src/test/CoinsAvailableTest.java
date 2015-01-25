package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.funds.CoinsAvailable;
import org.lsmr.vendingmachine.funds.CoinsAvailableListener;
import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinChannelSimulator;
import org.lsmr.vendingmachine.simulator.CoinRackSimulator;
import org.lsmr.vendingmachine.simulator.CoinReceptacleSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;

public class CoinsAvailableTest {
    class CoinChannelStub extends CoinChannelSimulator {
	public Coin delivered = null;
	public boolean hasSpace = true;
	public boolean throwInDeliver = false;

	public CoinChannelStub() {
	    super(null);
	}

	public void deliver(Coin coin) throws CapacityExceededException, DisabledException {
	    delivered = coin;
	    if(throwInDeliver)
		throw new CapacityExceededException();
	}

	public boolean hasSpace() {
	    return hasSpace;
	}
    }

    class CoinRackStub extends CoinRackSimulator {
	public Coin added = null;
	public boolean releaseIsCalled = false;
	public boolean hasSpace = true;
	public boolean throwEmptyInRelease = false;
	public boolean throwCapacityExceeededInRelease = false;
	public boolean throwDisabledInRelease = false;

	public CoinRackStub() {
	    super(1);
	}

	@Override
	public void acceptCoin(Coin coin) throws CapacityExceededException, DisabledException {
	    added = coin;
	}

	@Override
	public void releaseCoin() throws CapacityExceededException, EmptyException,
	        DisabledException {
	    releaseIsCalled = true;
	    if(throwEmptyInRelease)
		throw new EmptyException();
	    if(throwCapacityExceeededInRelease)
		throw new CapacityExceededException();
	    if(throwDisabledInRelease)
		throw new DisabledException();
	}

	@Override
	public boolean hasSpace() {
	    return hasSpace;
	}
    }

    private CoinsAvailable ca;
    private CoinReceptacleSimulator receptacle;
    private int[] denominations;
    private CoinChannelStub[] racksChannels;
    private CoinChannelStub coinReturn, overflow;
    private CoinRackStub[] racks;

    @Before
    public void setup() {
	denominations = new int[] { 5, 10, 25, 100, 200 };
	receptacle = new CoinReceptacleSimulator(10);
	racksChannels = new CoinChannelStub[denominations.length];
	racks = new CoinRackStub[denominations.length];
	Map<Integer, CoinChannelSimulator> map = new HashMap<Integer, CoinChannelSimulator>();

	for(int i = 0; i < racksChannels.length; i++) {
	    racksChannels[i] = new CoinChannelStub();
	    map.put(new Integer(denominations[i]), racksChannels[i]);
	    racks[i] = new CoinRackStub();
	}
	coinReturn = new CoinChannelStub();
	overflow = new CoinChannelStub();

	receptacle.connect(map, coinReturn, overflow);

	ca = new CoinsAvailable(receptacle, denominations, racks);
    }

    @After
    public void teardown() {
	ca = null;
	receptacle = null;
	denominations = null;
	racks = null;
	racksChannels = null;
	coinReturn = null;
	overflow = null;
    }

    @Test
    public void testDisabledReceptacleWhileStoring() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	receptacle.acceptCoin(new Coin(5));

	receptacle.disable();
	
	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }
	    
	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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
	
	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));
	assertEquals(true, flag.value);
    }

    @Test
    public void testDisabledReceptacleWhileReturning() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	receptacle.acceptCoin(new Coin(5));

	receptacle.disable();
	
	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }
	    
	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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
	
	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));
	assertEquals(true, flag.value);
    }

    @Test
    public void testDisabledReceptacleWhileReturningLess() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	receptacle.acceptCoin(new Coin(10));

	receptacle.disable();
	
	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }
	    
	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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
	
	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));
	assertEquals(true, flag.value);
    }

    @Test
    public void testDisabledReceptacleWhileStoringLess() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	receptacle.acceptCoin(new Coin(10));

	receptacle.disable();
	
	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }
	    
	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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
	
	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));
	assertEquals(true, flag.value);
	assertEquals(10, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testOverflowWhileStoring() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	overflow.hasSpace = true;
	overflow.throwInDeliver = true;

	for(CoinChannelStub channel : racksChannels)
	    channel.hasSpace = false;

	receptacle.acceptCoin(new Coin(5));

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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

	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
    }

    @Test
    public void testOverflowWhileStoringLess() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	overflow.hasSpace = true;
	overflow.throwInDeliver = true;

	for(CoinChannelStub channel : racksChannels)
	    channel.hasSpace = false;

	receptacle.acceptCoin(new Coin(10));

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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

	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
	assertEquals(10, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testOverflowWhileReturning() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);
	coinReturn.hasSpace = true;
	coinReturn.throwInDeliver = true;

	for(CoinChannelStub channel : racksChannels)
	    channel.hasSpace = false;

	receptacle.acceptCoin(new Coin(5));

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
    }

    @Test
    public void testReturningLess() throws CapacityExceededException, DisabledException {
	final MutableInteger count = new MutableInteger(0);
	racks[0].throwEmptyInRelease = true;
	
	for(CoinChannelStub channel : racksChannels)
	    channel.hasSpace = false;

	receptacle.acceptCoin(new Coin(10));
	receptacle.acceptCoin(new Coin(25));

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		count.incr();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		count.incr();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(15)));

	assertEquals(2, count.value);
	assertEquals(25, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturningLessButRacksAreFull() throws CapacityExceededException, DisabledException {
	final MutableInteger count = new MutableInteger(0);
	racks[0].throwEmptyInRelease = true;
	overflow.hasSpace = false;
	
	for(CoinChannelStub channel : racksChannels)
	    channel.hasSpace = false;

	receptacle.acceptCoin(new Coin(10));
	receptacle.acceptCoin(new Coin(25));

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		count.incr();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
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

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(15)));

	assertEquals(1, count.value);
	assertEquals(35, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSetup() {
	new CoinsAvailable(receptacle, null, racks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSetup2() {
	new CoinsAvailable(receptacle, denominations, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSetup3() {
	new CoinsAvailable(receptacle, new int[] {}, racks);
    }

    @Test
    public void testRegister() {
	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {}

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {}

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {}

	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {}
	});
    }

    @Test
    public void testIgnoredEvents() {
	ca.enabled((CoinReceptacleSimulator)null);
	ca.disabled((CoinReceptacleSimulator)null);
	ca.enabled((AbstractHardware<AbstractHardwareListener>)null);
	ca.disabled((AbstractHardware<AbstractHardwareListener>)null);
	ca.coinsFull((CoinReceptacleSimulator)null);
	ca.coinsFull((CoinRackSimulator)null);
	ca.coinsEmpty(null);
	ca.coinAdded((CoinRackSimulator)null, null);
	ca.coinRemoved((CoinRackSimulator)null, null);
	ca.coinsRemoved(null);
    }

    @Test
    public void testCoinAdded() {
	ca.coinAdded(receptacle, new Coin(1));
	assertEquals(1, ca.getCoinsAvailable().getQuantity().intValue());
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

    static class MutableInteger {
	public int value;

	public MutableInteger(int value) {
	    this.value = value;
	}

	public void incr() {
	    value++;
	}
    }

    @Test
    public void testBadCoinAdded() {
	final MutableBoolean flag = new MutableBoolean(false);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});
	ca.coinAdded((CoinReceptacleSimulator)null, new Coin(1));
	assertEquals(true, flag.value);
    }

    @Test
    public void testCoinAdded2() {
	final MutableBoolean flag = new MutableBoolean(false);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		flag.set();
	    }
	});
	ca.coinAdded(receptacle, new Coin(1));
	assertEquals(true, flag.value);
    }

    @Test
    public void testRemoveFundsExact() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		flag.set();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(40)));

	assertEquals(true, flag.value);
	assertEquals(coins[0], racksChannels[0].delivered);
	assertEquals(coins[1], racksChannels[1].delivered);
	assertEquals(coins[2], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(null, coinReturn.delivered);
	assertEquals(0, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testRemoveFundsLess() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		flag.set();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(35)));

	assertEquals(true, flag.value);
	assertEquals(coins[0], racksChannels[0].delivered);
	assertEquals(coins[1], racksChannels[1].delivered);
	assertEquals(coins[2], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(null, coinReturn.delivered);
	assertEquals(5, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testRemoveFundsMore() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.storeCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(45)));
	assertEquals(true, flag.value);
	assertEquals(null, racksChannels[0].delivered);
	assertEquals(null, racksChannels[1].delivered);
	assertEquals(null, racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(null, coinReturn.delivered);
	assertEquals(40, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsExact() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		flag.set();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(40)));

	assertEquals(true, flag.value);
	assertEquals(null, racksChannels[0].delivered);
	assertEquals(null, racksChannels[1].delivered);
	assertEquals(null, racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertNotNull(coinReturn.delivered);
	assertEquals(0, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsLess() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		flag.set();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
	assertEquals(coins[0], racksChannels[0].delivered);
	assertEquals(coins[1], racksChannels[1].delivered);
	assertEquals(coins[2], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(true, racks[0].releaseIsCalled);
	assertEquals(35, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsLessWhenDisabled() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);
	racks[0].throwDisabledInRelease = true;

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
	assertEquals(coins[0], racksChannels[0].delivered);
	assertEquals(coins[1], racksChannels[1].delivered);
	assertEquals(coins[2], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(true, racks[0].releaseIsCalled);
	assertEquals(40, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsLessWhenOverflow() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);
	racks[0].throwCapacityExceeededInRelease = true;

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
	assertEquals(coins[0], racksChannels[0].delivered);
	assertEquals(coins[1], racksChannels[1].delivered);
	assertEquals(coins[2], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(true, racks[0].releaseIsCalled);
	assertEquals(40, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsMore() throws CapacityExceededException, DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(5), new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	receptacle.acceptCoin(coins[2]);

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(45)));

	assertEquals(true, flag.value);
	assertEquals(null, racksChannels[0].delivered);
	assertEquals(null, racksChannels[1].delivered);
	assertEquals(null, racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(false, racks[0].releaseIsCalled);
	assertEquals(false, racks[1].releaseIsCalled);
	assertEquals(false, racks[2].releaseIsCalled);
	assertEquals(40, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsLessWhenInsufficientChange() throws CapacityExceededException,
	    DisabledException {
	final MutableBoolean flag = new MutableBoolean(false);

	Coin[] coins = new Coin[] { new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	racks[0].throwEmptyInRelease = true;

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		flag.set();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(5)));

	assertEquals(true, flag.value);
	assertEquals(null, racksChannels[0].delivered);
	assertEquals(coins[0], racksChannels[1].delivered);
	assertEquals(coins[1], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(true, racks[0].releaseIsCalled);
	assertEquals(false, racks[1].releaseIsCalled);
	assertEquals(false, racks[2].releaseIsCalled);
	assertEquals(35, ca.getCoinsAvailable().getQuantity().intValue());
    }

    @Test
    public void testReturnFundsLessWhenInsufficientChange2() throws CapacityExceededException,
	    DisabledException {
	final MutableInteger count = new MutableInteger(0);

	Coin[] coins = new Coin[] { new Coin(10), new Coin(25) };
	receptacle.acceptCoin(coins[0]);
	receptacle.acceptCoin(coins[1]);
	racks[0].throwEmptyInRelease = true;

	ca.register(new CoinsAvailableListener() {
	    @Override
	    public void hardwareFailure(CoinsAvailable fa) {
		count.incr();
	    }

	    @Override
	    public void coinsReturned(CoinsAvailable fa, Currency curr) {
		if(curr.getQuantity().intValue() == 10)
		    count.incr();
	    }

	    @Override
	    public void coinsStored(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }

	    @Override
	    public void coinsAdded(CoinsAvailable fa, Currency curr) {
		throw new RuntimeException();
	    }
	});

	ca.returnCoins(new Currency(java.util.Currency.getInstance(Locale.CANADA), new BigDecimal(15)));

	assertEquals(2, count.value);
	assertEquals(null, racksChannels[0].delivered);
	assertEquals(coins[0], racksChannels[1].delivered);
	assertEquals(coins[1], racksChannels[2].delivered);
	assertEquals(null, racksChannels[3].delivered);
	assertEquals(null, racksChannels[4].delivered);
	assertEquals(null, overflow.delivered);
	assertEquals(true, racks[0].releaseIsCalled);
	assertEquals(true, racks[1].releaseIsCalled);
	assertEquals(false, racks[2].releaseIsCalled);
	assertEquals(25, ca.getCoinsAvailable().getQuantity().intValue());
    }
}
