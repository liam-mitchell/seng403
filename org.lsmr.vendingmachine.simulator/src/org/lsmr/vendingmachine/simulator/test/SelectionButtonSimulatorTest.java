package org.lsmr.vendingmachine.simulator.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;
import org.lsmr.vendingmachine.simulator.test.stub.SelectionButtonListenerStub;

public class SelectionButtonSimulatorTest {
    private SelectionButtonSimulator button;
    private SelectionButtonListenerStub listener;

    @Before
    public void setup() {
	button = new SelectionButtonSimulator();
	listener = new SelectionButtonListenerStub();
	button.register(listener);

	listener.init();
    }

    @After
    public void teardown() {
	button.deregisterAll();
	button = null;
	listener = null;
    }

    @Test
    public void testPress() {
	listener.expect("pressed");

	button.press();
	listener.assertProtocol();
    }
}
