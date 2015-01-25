package org.lsmr.vendingmachine.simulator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DeliveryChuteSimulator;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.SimulationException;
import org.lsmr.vendingmachine.simulator.test.stub.DeliveryChuteListenerStub;

public class DeliveryChuteSimulatorTest {
    private DeliveryChuteSimulator chute;
    private Coin coin;
    private PopCan pop;
    private DeliveryChuteListenerStub listener;

    @Before
    public void setup() {
	chute = new DeliveryChuteSimulator(2);
	coin = new Coin(1);
	pop = new PopCan();
	listener = new DeliveryChuteListenerStub();
	chute.register(listener);

	listener.init();
    }

    @After
    public void teardown() {
	chute.deregisterAll();
	chute = null;
	coin = null;
	pop = null;
	listener = null;
    }
    
    @Test
    public void testCapacity() {
	assertTrue(chute.getCapacity() == 2);	
    }

    @Test
    public void testDeliverPopDisabled() throws CapacityExceededException {
	chute.setDisabledWithoutEvents(true);
	
	try {
	    chute.acceptPop(pop);
	    fail();
	}
	catch(DisabledException e) {}
    }

    @Test
    public void testDeliverCoinDisabled() throws CapacityExceededException {
	chute.setDisabledWithoutEvents(true);
	
	try {
	    chute.acceptCoin(coin);
	    fail();
	}
	catch(DisabledException e) {}
    }
    
    @Test
    public void testDeliverBecomingFull1() throws CapacityExceededException, DisabledException {
	listener.expect("itemDelivered", "itemDelivered", "chuteFull");

	assertTrue(chute.hasSpace());
	chute.acceptCoin(coin);
	assertTrue(chute.hasSpace());
	chute.acceptPop(pop);
	assertFalse(chute.hasSpace());
	listener.assertProtocol();
    }

    @Test
    public void testDeliverBecomingFull2() throws CapacityExceededException, DisabledException {
	listener.expect("itemDelivered", "itemDelivered", "chuteFull");

	assertTrue(chute.hasSpace());
	chute.acceptPop(pop);
	assertTrue(chute.hasSpace());
	chute.acceptCoin(coin);
	assertFalse(chute.hasSpace());
	listener.assertProtocol();
    }

    @Test
    public void testDeliverNotBecomingFull() throws CapacityExceededException, DisabledException {
	listener.expect("itemDelivered");

	assertTrue(chute.hasSpace());
	chute.acceptCoin(coin);
	assertTrue(chute.hasSpace());

	listener.assertProtocol();
    }

    @Test
    public void testDeliverFull() throws DisabledException {
	chute.loadWithoutEvents(coin);
	chute.loadWithoutEvents(pop);
	
	try {
	    chute.acceptCoin(coin);
	    fail();
	}
	catch(CapacityExceededException e) {}
	listener.assertProtocol();

	try {
	    listener.init();
	    chute.acceptPop(pop);
	    fail();
	}
	catch(CapacityExceededException e) {}
	listener.assertProtocol();
    }

    @Test
    public void testDeliverEmptyButChecked() {
	chute.loadWithoutEvents(coin);
	chute.loadWithoutEvents(pop);

	listener.expect("doorOpened", "doorClosed");
	chute.removeItems();
	listener.assertProtocol();
    }

    @Test(expected = SimulationException.class)
    public void testOverloadWithCoins() {
	chute.loadWithoutEvents(coin, coin, coin);
    }

    @Test(expected = SimulationException.class)
    public void testOverloadWithPops() {
	chute.loadWithoutEvents(pop, pop, pop);
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup1() {
	chute = new DeliveryChuteSimulator(0);
    }
    
    @Test(expected = SimulationException.class)
    public void testBadSetup2() {
	chute = new DeliveryChuteSimulator(-1);
    }
}
