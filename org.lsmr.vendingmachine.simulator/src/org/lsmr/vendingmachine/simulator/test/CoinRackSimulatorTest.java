package org.lsmr.vendingmachine.simulator.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.CoinChannelSimulator;
import org.lsmr.vendingmachine.simulator.CoinRackSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;
import org.lsmr.vendingmachine.simulator.SimulationException;
import org.lsmr.vendingmachine.simulator.test.stub.AbstractCoinAcceptorStub;
import org.lsmr.vendingmachine.simulator.test.stub.CoinRackListenerStub;

public class CoinRackSimulatorTest {
    private CoinRackSimulator rack;
    private Coin coin;
    private CoinRackListenerStub listener;
    private AbstractCoinAcceptorStub deliveryChute;

    @Before
    public void setup() {
	coin = new Coin(1);
	rack = new CoinRackSimulator(2);

	deliveryChute = new AbstractCoinAcceptorStub(true);
	rack.connect(new CoinChannelSimulator(deliveryChute));

	listener = new CoinRackListenerStub();
	rack.register(listener);

	listener.init();
    }

    @After
    public void teardown() {
	rack.deregisterAll();
	rack = null;
	coin = null;
	listener = null;
	deliveryChute = null;
    }

    @Test
    public void testRackAcceptCoinEmpty() throws CapacityExceededException,
	    DisabledException {
	listener.expect("coinAdded");
	rack.acceptCoin(coin);
	listener.assertProtocol();
    }

    @Test
    public void testRackAcceptCoinAlmostFull()
	    throws CapacityExceededException, DisabledException {
	rack.loadWithoutEvents(coin);
	listener.expect("coinAdded", "coinsFull");

	rack.acceptCoin(coin);
	listener.assertProtocol();
    }

    @Test
    public void testRackAcceptCoinDisabled() throws CapacityExceededException {
	rack.loadWithoutEvents(coin);
	listener.expect("disabled");

	rack.disable();
	try {
	    rack.acceptCoin(coin);
	    fail();
	}
	catch(DisabledException e) {}
	listener.assertProtocol();
    }

    @Test
    public void testRackAcceptCoinFull() throws DisabledException {
	rack.loadWithoutEvents(coin, coin);

	try {
	    rack.acceptCoin(coin);
	    fail();
	}
	catch(CapacityExceededException e) {}
	listener.assertProtocol();
    }

    @Test
    public void testRackDispense() throws CapacityExceededException,
	    EmptyException, DisabledException {
	rack.loadWithoutEvents(coin);
	deliveryChute.expect("acceptCoin");
	listener.expect("coinRemoved", "coinsEmpty");

	rack.releaseCoin();
	listener.assertProtocol();
	deliveryChute.assertProtocol();
    }

    @Test
    public void testRackDispenseDisabled() throws CapacityExceededException,
	    EmptyException {
	rack.loadWithoutEvents(coin);
	listener.expect("disabled");

	rack.disable();

	try {
	    assertTrue(rack.isDisabled());

	    rack.releaseCoin();
	    fail();
	}
	catch(DisabledException e) {}
	listener.assertProtocol();
    }

    @Test
    public void testRackDispenseEmpty() throws CapacityExceededException,
	    DisabledException {
	try {
	    rack.releaseCoin();
	    fail();
	}
	catch(EmptyException e) {}
	listener.assertProtocol();
    }

    @Test(expected = SimulationException.class)
    public void testRackOverload() {
	rack.loadWithoutEvents(coin, coin, coin);
    }

    @Test
    public void testRackDispenseCoinNotBecomingEmpty()
	    throws CapacityExceededException, EmptyException, DisabledException {
	rack.loadWithoutEvents(coin, coin);
	deliveryChute.expect("acceptCoin");
	listener.expect("coinRemoved");

	rack.releaseCoin();

	deliveryChute.assertProtocol();
	listener.assertProtocol();
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup1() {
	rack = new CoinRackSimulator(0);
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup2() {
	rack = new CoinRackSimulator(-1);
    }
}
