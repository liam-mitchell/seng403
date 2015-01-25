package org.lsmr.vendingmachine.simulator.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinChannelSimulator;
import org.lsmr.vendingmachine.simulator.CoinSlotSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.SimulationException;
import org.lsmr.vendingmachine.simulator.test.stub.AbstractCoinAcceptorStub;
import org.lsmr.vendingmachine.simulator.test.stub.CoinSlotListenerStub;

public class CoinSlotSimulatorTest {
    private CoinSlotSimulator slot;
    private CoinSlotListenerStub listener;
    private Coin coin, invalidCoin;
    private AbstractCoinAcceptorStub receptacle, deliveryChute;

    @Before
    public void setup() {
	slot = new CoinSlotSimulator(new int[] { 1 });
	coin = new Coin(1);
	invalidCoin = new Coin(2);
	listener = new CoinSlotListenerStub();
	slot.register(listener);

	receptacle = new AbstractCoinAcceptorStub(true);
	deliveryChute = new AbstractCoinAcceptorStub(true);
	slot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));

	listener.init();
	receptacle.init();
	deliveryChute.init();
    }

    @After
    public void teardown() {
	slot.connect(null, null);
	slot.deregisterAll();
	slot = null;
	coin = null;
	invalidCoin = null;
	listener = null;
	receptacle = null;
	deliveryChute = null;
    }

    @Test
    public void testCoinSlotValid() throws DisabledException {
	listener.expect("validCoinInserted");
	receptacle.expect("hasSpace", "acceptCoin");

	slot.addCoin(coin);
	listener.assertProtocol();
	receptacle.assertProtocol();
    }

    @Test
    public void testCoinSlotValidButFull() throws DisabledException {
	receptacle = new AbstractCoinAcceptorStub(false);
	slot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));

	listener.expect("coinRejected");
	receptacle.expect("hasSpace");
	deliveryChute.expect("hasSpace", "acceptCoin");

	slot.addCoin(coin);

	listener.assertProtocol();
	receptacle.assertProtocol();
	deliveryChute.assertProtocol();
    }

    @Test
    public void testCoinSlotValidButFullAndChuteFull() throws DisabledException {
	receptacle = new AbstractCoinAcceptorStub(false);
	deliveryChute = new AbstractCoinAcceptorStub(false);
	slot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));

	receptacle.expect("hasSpace");
	deliveryChute.expect("hasSpace");

	try {
	    slot.addCoin(coin);
	    fail();
	}
	catch(SimulationException e) {}

	listener.assertProtocol();
	receptacle.assertProtocol();
	deliveryChute.assertProtocol();
    }

    @Test
    public void testCoinSlotValidButReceptacleMisbehaves()
	    throws DisabledException {
	class MisbehavingCoinAcceptorStub extends AbstractCoinAcceptorStub {
	    public MisbehavingCoinAcceptorStub() {
		super(true);
	    }

	    public void acceptCoin(Coin coin) throws CapacityExceededException {
		recordCallTo("acceptCoin");
		throw new CapacityExceededException();
	    }
	}
	receptacle = new MisbehavingCoinAcceptorStub();
	slot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));

	listener.expect("validCoinInserted");
	receptacle.expect("hasSpace", "acceptCoin");

	try {
	    slot.addCoin(coin);
	    fail();
	}
	catch(SimulationException e) {}

	listener.assertProtocol();
	receptacle.assertProtocol();
    }

    @Test
    public void testCoinSlotInvalid() throws DisabledException {
	listener.expect("coinRejected");
	deliveryChute.expect("hasSpace", "acceptCoin");

	slot.addCoin(invalidCoin);

	listener.assertProtocol();
	deliveryChute.assertProtocol();
    }

    @Test
    public void testCoinSlotInvalidButDeliveryChuteMisbehaves()
	    throws DisabledException {
	class MisbehavingCoinAcceptorStub extends AbstractCoinAcceptorStub {
	    public MisbehavingCoinAcceptorStub() {
		super(true);
	    }

	    public void acceptCoin(Coin coin) throws CapacityExceededException {
		recordCallTo("acceptCoin");
		throw new CapacityExceededException();
	    }
	}
	deliveryChute = new MisbehavingCoinAcceptorStub();
	slot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));

	listener.expect("coinRejected");
	deliveryChute.expect("hasSpace", "acceptCoin");

	try {
	    slot.addCoin(invalidCoin);
	    fail();
	}
	catch(SimulationException e) {
	    // for the sake of coverage
	    e.toString();
	}

	listener.assertProtocol();
	deliveryChute.assertProtocol();
    }

    @Test
    public void testCoinSlotDisabled() {
	listener.expect("disabled");

	slot.disable();
	assertTrue(slot.isDisabled());
	listener.assertProtocol();

	try {
	    listener.init();
	    slot.addCoin(coin);
	    fail();
	}
	catch(DisabledException e) {}
	listener.assertProtocol();
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup1() {
	coin = new Coin(0);
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup2() {
	coin = new Coin(-1);
    }
}
