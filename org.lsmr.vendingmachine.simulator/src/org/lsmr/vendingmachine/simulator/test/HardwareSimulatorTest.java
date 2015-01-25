package org.lsmr.vendingmachine.simulator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.Coin;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.HardwareSimulator;
import org.lsmr.vendingmachine.simulator.IndicatorLightSimulator;
import org.lsmr.vendingmachine.simulator.SimulationException;
import org.lsmr.vendingmachine.simulator.test.stub.CardSlotListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.CoinRackListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.CoinReceptacleListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.CoinSlotListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.DeliveryChuteListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.IndicatorLightListenerStub;
import org.lsmr.vendingmachine.simulator.test.stub.PopCanRackListenerStub;

public class HardwareSimulatorTest {
    private HardwareSimulator hardware;
    private Coin coin;

    private CoinSlotListenerStub coinSlotListener;
    private CoinReceptacleListenerStub coinReceptacleListener,
	    storageBinListener;
    private CardSlotListenerStub cardSlotListener;
    private DeliveryChuteListenerStub deliveryChuteListener;
    private CoinRackListenerStub[] coinRackListeners;
    private PopCanRackListenerStub[] popRackListeners;
    private IndicatorLightListenerStub outOfOrderListener, exactChangeListener;

    @Before
    public void setup() {
	hardware =
	        new HardwareSimulator(new int[] { 5, 10, 25, 100, 200 }, new int[] {
	                150, 150, 200, 200, 200, 200 }, new String[] {
	                "Aquafina", "Aquafina", "Coca-Cola", "Coca-Cola",
	                "Coke Zero", "7Up" });

	coin = new Coin(100);

	coinSlotListener = new CoinSlotListenerStub();
	coinReceptacleListener = new CoinReceptacleListenerStub();
	storageBinListener = new CoinReceptacleListenerStub();
	cardSlotListener = new CardSlotListenerStub();
	deliveryChuteListener = new DeliveryChuteListenerStub();
	coinRackListeners = new CoinRackListenerStub[5];
	for(int i = 0; i < 5; i++) {
	    coinRackListeners[i] = new CoinRackListenerStub();
	    hardware.getCoinRack(i).register(coinRackListeners[i]);
	}
	popRackListeners = new PopCanRackListenerStub[6];
	for(int i = 0; i < 6; i++) {
	    popRackListeners[i] = new PopCanRackListenerStub();
	    hardware.getPopCanRack(i).register(popRackListeners[i]);
	}
	outOfOrderListener = new IndicatorLightListenerStub();
	exactChangeListener = new IndicatorLightListenerStub();

	hardware.getCoinSlot().register(coinSlotListener);
	hardware.getCoinReceptacle().register(coinReceptacleListener);
	hardware.getStorageBin().register(storageBinListener);
	hardware.getCardSlot().register(cardSlotListener);
	hardware.getDeliveryChute().register(deliveryChuteListener);
	hardware.getExactChangeLight().register(exactChangeListener);
	hardware.getOutOfOrderLight().register(outOfOrderListener);
    }

    @After
    public void teardown() {
	hardware.getCoinSlot().deregister(coinSlotListener);
	hardware.getCoinReceptacle().deregister(coinReceptacleListener);
	hardware.getStorageBin().deregister(storageBinListener);
	hardware.getCardSlot().deregister(cardSlotListener);
	hardware.getDeliveryChute().deregister(deliveryChuteListener);
	hardware.getExactChangeLight().deregister(exactChangeListener);
	hardware.getOutOfOrderLight().deregister(outOfOrderListener);

	for(int i = 0; i < 5; i++) {
	    hardware.getCoinRack(i).deregister(coinRackListeners[i]);
	    coinRackListeners[i] = null;
	}

	for(int i = 0; i < 6; i++) {
	    hardware.getPopCanRack(i).deregister(popRackListeners[i]);
	    popRackListeners[i] = null;
	}

	hardware = null;
	coin = null;
	coinSlotListener = null;
	coinReceptacleListener = null;
	cardSlotListener = null;
	deliveryChuteListener = null;
	storageBinListener = null;
	coinRackListeners = null;
	popRackListeners = null;
	exactChangeListener = null;
	outOfOrderListener = null;
    }

    @Test(expected = SimulationException.class)
    public void testNullCoinValues() {
	hardware =
	        new HardwareSimulator(null, new int[] {
	                150, 150, 200, 200, 200, 200 }, new String[] {
	                "Aquafina", "Aquafina", "Coca-Cola", "Coca-Cola",
	                "Coke Zero", "7Up" });
    }

    @Test(expected = SimulationException.class)
    public void testNullPopCosts() {
	hardware =
	        new HardwareSimulator(new int[] { 5, 10, 25, 100, 200 }, null, new String[] {
	                "Aquafina", "Aquafina", "Coca-Cola", "Coca-Cola",
	                "Coke Zero", "7Up" });
    }

    @Test(expected = SimulationException.class)
    public void testNullPopNames() {
	hardware =
	        new HardwareSimulator(new int[] { 5, 10, 25, 100, 200 }, new int[] {
	                150, 150, 200, 200, 200, 200 }, null);
    }

    @Test(expected = SimulationException.class)
    public void testInconsistentPopData() {
	hardware =
	        new HardwareSimulator(new int[] { 5, 10, 25, 100, 200 }, new int[] {
	                150, 150, 200, 200, 200, 200 }, new String[] {
	                "Aquafina", "Coca-Cola", "Coca-Cola",
	                "Coke Zero", "7Up" });
    }

    @Test
    public void testCoinEntry() throws CapacityExceededException,
	    DisabledException {
	coinReceptacleListener.expect("coinAdded");
	coinSlotListener.expect("validCoinInserted");
	hardware.getCoinSlot().addCoin(coin);
	coinSlotListener.assertProtocol();
	coinReceptacleListener.assertProtocol();

	coinSlotListener.init();
	coinReceptacleListener.init();
	coinReceptacleListener.expect("coinsRemoved");
	deliveryChuteListener.expect("itemDelivered");
	hardware.getCoinReceptacle().returnCoins();
	coinReceptacleListener.assertProtocol();
	deliveryChuteListener.assertProtocol();

	coinReceptacleListener.init();
	deliveryChuteListener.init();
	deliveryChuteListener.expect("doorOpened", "doorClosed");
	Object[] items = hardware.getDeliveryChute().removeItems();
	assertTrue(items.length == 1);
	assertTrue(items[0] == coin);
	deliveryChuteListener.assertProtocol();
    }

    @Test
    public void testCoinEntryAndStoreToRack() throws CapacityExceededException,
	    DisabledException {
	coinSlotListener.expect("validCoinInserted");
	coinReceptacleListener.expect("coinAdded");
	hardware.getCoinSlot().addCoin(coin);
	coinSlotListener.assertProtocol();
	coinReceptacleListener.assertProtocol();

	coinSlotListener.init();
	coinReceptacleListener.init();
	coinReceptacleListener.expect("coinsRemoved");
	coinRackListeners[3].expect("coinAdded");
	hardware.getCoinReceptacle().storeCoins();
	coinReceptacleListener.assertProtocol();
	coinRackListeners[3].assertProtocol();
    }

    @Test
    public void testCoinEntryAndStoreToBin() throws CapacityExceededException,
	    DisabledException {
	int capacity = hardware.getCoinRack(3).getCapacity(); // loonies rack

	for(int i = 0; i < capacity; i++) {
	    coinSlotListener.expect("validCoinInserted");
	    coinReceptacleListener.expect("coinAdded");
	}

	for(int i = 0; i < capacity; i++) {
	    hardware.getCoinSlot().addCoin(coin);
	}

	coinSlotListener.assertProtocol();
	coinReceptacleListener.assertProtocol();

	coinSlotListener.init();
	coinReceptacleListener.init();
	coinSlotListener.expect("validCoinInserted");
	coinReceptacleListener.expect("coinAdded");
	hardware.getCoinSlot().addCoin(coin);
	coinSlotListener.assertProtocol();
	coinReceptacleListener.assertProtocol();

	coinSlotListener.init();
	coinReceptacleListener.init();
	coinReceptacleListener.expect("coinsRemoved");
	for(int i = 0; i < capacity; i++) {
	    coinRackListeners[3].expect("coinAdded");
	}
	coinRackListeners[3].expect("coinsFull");
	storageBinListener.expect("coinAdded");
	hardware.getCoinReceptacle().storeCoins();
	coinReceptacleListener.assertProtocol();
	coinRackListeners[3].assertProtocol();
	storageBinListener.assertProtocol();
    }

    @Test
    public void testCoinEntryWhenDisabled() throws CapacityExceededException, DisabledException {
	coinSlotListener.expect("disabled");
	cardSlotListener.expect("disabled");
	deliveryChuteListener.expect("disabled");
	outOfOrderListener.expect("activated");
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].expect("disabled");
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].expect("disabled");
	
	hardware.enableSafety();
	
	coinSlotListener.assertProtocol();
	cardSlotListener.assertProtocol();
	outOfOrderListener.assertProtocol();
	deliveryChuteListener.assertProtocol();
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].assertProtocol();
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].assertProtocol();

	outOfOrderListener.init();
	coinSlotListener.init();
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].init();
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].init();
	try {
	    hardware.getCoinSlot().addCoin(coin);
	    fail();
	}
	catch(DisabledException e) {}

	hardware.getCoinReceptacle().returnCoins();

	coinReceptacleListener.init();
	deliveryChuteListener.init();
	deliveryChuteListener.expect("doorOpened", "doorClosed");
	Object[] items = hardware.getDeliveryChute().removeItems();
	assertTrue(items.length == 0);
	deliveryChuteListener.assertProtocol();
    }

    @Test
    public void testCoinEntryAndStoreToRackWhenDisabled()
	    throws CapacityExceededException, DisabledException {
	coinSlotListener.expect("disabled");
	cardSlotListener.expect("disabled");
	deliveryChuteListener.expect("disabled");
	outOfOrderListener.expect("activated");
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].expect("disabled");
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].expect("disabled");
	
	hardware.enableSafety();
	
	coinSlotListener.assertProtocol();
	cardSlotListener.assertProtocol();
	deliveryChuteListener.assertProtocol();
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].assertProtocol();
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].assertProtocol();

	coinSlotListener.init();
	outOfOrderListener.init();
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].init();
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].init();

	hardware.getCoinReceptacle().storeCoins();
    }

    @Test(expected = SimulationException.class)
    public void testBadEvent() {
	class DummyHardware extends AbstractHardware<AbstractHardwareListener> {
	    public void foo() {
		notifyListeners(AbstractHardwareListener.class, "foo", null, null);
	    }
	}

	new DummyHardware().foo();
    }

    @Test
    public void testHardwareDisabledAndReenabled() {
	coinSlotListener.expect("disabled", "enabled");
	cardSlotListener.expect("disabled", "enabled");
	deliveryChuteListener.expect("disabled", "enabled");
	outOfOrderListener.expect("activated", "deactivated");
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].expect("disabled", "enabled");
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].expect("disabled", "enabled");

	assertFalse(hardware.isSafetyEnabled());
	hardware.enableSafety();
	assertTrue(hardware.isSafetyEnabled());
	hardware.disableSafety();
	assertFalse(hardware.isSafetyEnabled());

	coinSlotListener.assertProtocol();
	cardSlotListener.assertProtocol();
	deliveryChuteListener.assertProtocol();
	for(int i = 0; i < coinRackListeners.length; i++)
	    coinRackListeners[i].assertProtocol();
	for(int i = 0; i < popRackListeners.length; i++)
	    popRackListeners[i].assertProtocol();
    }

    @Test
    public void testOutOfOrderLight() {
	IndicatorLightSimulator outOfOrderLight = hardware.getOutOfOrderLight();
	outOfOrderListener.expect("deactivated", "activated");

	outOfOrderLight.loadWithoutEvents(true);
	assertTrue(outOfOrderLight.isActive());
	outOfOrderLight.deactivate();
	assertFalse(outOfOrderLight.isActive());
	outOfOrderLight.activate();
	assertTrue(outOfOrderLight.isActive());

	outOfOrderListener.assertProtocol();
    }
    
    @Test
    public void testGetCoinRacks() {
	int count = hardware.getNumberOfCoinRacks();
	assertTrue(count == 5);
	assertFalse(hardware.getCoinRack(0) == null);
	assertFalse(hardware.getCoinRack(count - 1) == null);
	try {
	    hardware.getCoinRack(count);
	    fail();
	}
	catch(IndexOutOfBoundsException e) {}
    }
    
    @Test
    public void testGetPopRacks() {
	int count = hardware.getNumberOfPopCanRacks();
	assertTrue(count == 6);
	assertFalse(hardware.getPopCanRack(0) == null);
	assertFalse(hardware.getPopCanRack(count - 1) == null);
	try {
	    hardware.getPopCanRack(count);
	    fail();
	}
	catch(IndexOutOfBoundsException e) {}
    }
    
    @Test
    public void testGetSelectionButtons() {
	int count = hardware.getNumberOfSelectionButtons();
	assertTrue(count == 6);
	assertFalse(hardware.getSelectionButton(0) == null);
	assertFalse(hardware.getSelectionButton(count - 1) == null);
	try {
	    hardware.getSelectionButton(count);
	    fail();
	}
	catch(IndexOutOfBoundsException e) {}
    }
    
    @Test
    public void testGetDisplay() {
	assertFalse(hardware.getDisplay() == null);
    }
}
