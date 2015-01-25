package org.lsmr.vendingmachine.simulator.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.DisplaySimulator;
import org.lsmr.vendingmachine.simulator.test.stub.DisplayListenerStub;

public class DisplaySimulatorTest {
    private DisplaySimulator display;
    private DisplayListenerStub listener;

    @Before
    public void setup() {
	display = new DisplaySimulator();
	listener = new DisplayListenerStub();
	display.register(listener);

	listener.init();
    }

    @After
    public void teardown() {
	display.deregisterAll();
	display = null;
	listener = null;
    }

    @Test
    public void testSetMessage() {
	listener.expect("messageChange");

	display.display("message");
	listener.assertProtocol();
    }

    @Test
    public void testChangeMessage() {
	listener.expect("messageChange");
	display.loadWithoutEvents("message");

	display.display("new message");
	listener.assertProtocol();
    }
}
