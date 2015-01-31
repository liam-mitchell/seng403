package org.lsmr.vendingmachine.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a standard configuration of the vending machine hardware:
 * <ul>
 * <li>one coin slot;</li>
 * <li>one coin receptacle (called the coin receptacle) to temporarily store
 * coins entered by the user;</li>
 * <li>one coin receptacle (called the storage bin) to store coins that have
 * been accepted as payment;</li>
 * <li>a set of zero or more coin racks (the number and the denomination of
 * coins stored by each is specified in the constructor);</li>
 * <li>one delivery chute used to deliver pop cans and to return coins;</li>
 * <li>a set of zero or more pop can racks (the number, cost, and pop name
 * stored in each is specified in the constructor);</li>
 * <li>one slot for accepting magnetic stripe cards (e.g., credit cards, debit
 * cards);</li>
 * <li>one textual display;</li>
 * <li>a set of zero or more selection buttons (exactly one per pop can rack);
 * and</li>
 * <li>two indicator lights: one to indicate that exact change should be used by
 * the user; the other to indicate that the machine is out of order.</li>
 * </ul>
 * <p>
 * The component devices are interconnected as follows:
 * <ul>
 * <li>the output of the coin slot is connected to the input of the coin
 * receptacle;</li>
 * <li>the outputs of the coin receptacle are connected to the inputs of the
 * coin racks (for valid coins to be stored therein), the delivery chute (for
 * invalid coins or other coins to be returned), and the storage bin (for coins
 * to be accepted that do not fit in the coin racks);</li>
 * <li>the output of each coin rack is connected to the delivery chute; and</li>
 * <li>the output of each pop can rack is connected to the delivery chute.</li>
 * </ul>
 * <p>
 * Each component device can be disabled to prevent any physical movements.
 * Other functionality is not affected by disabling a device; hence devices that
 * do not involve physical movements are not affected by "disabling" them.
 * <p>
 * Most component devices have some sort of maximum capacity (e.g., of the
 * number of pop cans that can be stored therein). In some cases, this is a
 * simplification of the physical reality for the sake of simulation.
 */
public class HardwareSimulator {
    private boolean safetyOn = false;

    private CoinSlotSimulator coinSlot;
    private CoinReceptacleSimulator receptacle, storageBin;
    private DeliveryChuteSimulator deliveryChute;
    private CoinRackSimulator[] coinRacks;
    private Map<Integer, CoinChannelSimulator> coinRackChannels;
    private PopCanRackSimulator[] popCanRacks;
    private CardSlotSimulator cardSlot;
    private DisplaySimulator display;
    private SelectionButtonSimulator[] buttons;
    private IndicatorLightSimulator exactChangeLight, outOfOrderLight;
    
    private MoneyManager moneyManager;

    protected static int deliveryChuteCapacity = 20;
    protected static int coinReceptableCapacity = 50;
    protected static int storageBinCapacity = 1000;
    protected static int coinRackCapacity = 20;
    protected static int popRackCapacity = 15;
    protected static int displayCharacters = 30;

    /**
     * Creates a standard arrangement for the vending machine. All the
     * components are created and interconnected.
     * 
     * @param coinValues
     *            The values (in cents) of each kind of coin. No specific check
     *            is made that these are all different, or arranged in any
     *            manner.
     * @param popCosts
     *            The cost (in cents) of each kind of pop. These can all be the
     *            same. The size of the array is used to indicate the number of
     *            pop racks and selection buttons; it must correspond to the pop
     *            names.
     * @param popNames
     *            The names of each kind of pop. These can all be the same. The
     *            size of the array must correspond to the size of the popCosts
     *            array.
     * @throws SimulationException
     *             if any of the arguments is null, or the size of popCosts and
     *             popNames differ.
     */
    public HardwareSimulator(int[] coinValues, int[] popCosts, String[] popNames) {
	if(coinValues == null || popCosts == null || popNames == null)
	    throw new SimulationException("Arguments may not be null");

	if(popCosts.length != popNames.length)
	    throw new SimulationException("Pop costs and names must be of same length");

	cardSlot = new CardSlotSimulator();
	display = new DisplaySimulator();

	coinSlot = new CoinSlotSimulator(coinValues);
	receptacle = new CoinReceptacleSimulator(coinReceptableCapacity);
	storageBin = new CoinReceptacleSimulator(storageBinCapacity);
	deliveryChute = new DeliveryChuteSimulator(deliveryChuteCapacity);
	coinRacks = new CoinRackSimulator[coinValues.length];
	coinRackChannels = new HashMap<Integer, CoinChannelSimulator>();
	for(int i = 0; i < coinValues.length; i++) {
	    coinRacks[i] = new CoinRackSimulator(coinRackCapacity);
	    coinRacks[i].connect(new CoinChannelSimulator(deliveryChute));
	    coinRackChannels.put(new Integer(coinValues[i]), new CoinChannelSimulator(coinRacks[i]));
	}

	popCanRacks = new PopCanRackSimulator[popNames.length];
	for(int i = 0; i < popNames.length; i++) {
	    popCanRacks[i] = new PopCanRackSimulator(popRackCapacity);
	    popCanRacks[i].connect(new PopCanChannelSimulator(deliveryChute));
	}

	buttons = new SelectionButtonSimulator[popNames.length];
	for(int i = 0; i < popNames.length; i++)
	    buttons[i] = new SelectionButtonSimulator();

	coinSlot.connect(new CoinChannelSimulator(receptacle), new CoinChannelSimulator(deliveryChute));
	receptacle.connect(coinRackChannels, new CoinChannelSimulator(deliveryChute), new CoinChannelSimulator(storageBin));

	exactChangeLight = new IndicatorLightSimulator();
	outOfOrderLight = new IndicatorLightSimulator();
	
	moneyManager = new MoneyManager(this);
    }

    /**
     * Disables all the components of the hardware that involve physical
     * movements. Activates the out of order light.
     */
    public void enableSafety() {
	safetyOn = true;
	coinSlot.disable();
	cardSlot.disable();
	deliveryChute.disable();

	for(int i = 0; i < popCanRacks.length; i++)
	    popCanRacks[i].disable();

	for(int i = 0; i < coinRacks.length; i++)
	    coinRacks[i].disable();

	outOfOrderLight.activate();
    }

    /**
     * Enables all the components of the hardware that involve physical
     * movements. Deactivates the out of order light.
     */
    public void disableSafety() {
	safetyOn = false;
	coinSlot.enable();
	cardSlot.enable();
	deliveryChute.enable();

	for(int i = 0; i < popCanRacks.length; i++)
	    popCanRacks[i].enable();

	for(int i = 0; i < coinRacks.length; i++)
	    coinRacks[i].enable();

	outOfOrderLight.deactivate();
    }

    /**
     * Returns whether the safety is currently engaged.
     */
    public boolean isSafetyEnabled() {
	return safetyOn;
    }

    /**
     * Returns the exact change light.
     */
    public IndicatorLightSimulator getExactChangeLight() {
	return exactChangeLight;
    }

    /**
     * Returns the out of order light.
     */
    public IndicatorLightSimulator getOutOfOrderLight() {
	return outOfOrderLight;
    }

    /**
     * Returns a selection button at the indicated index.
     * 
     * @param index
     *            The index of the desired selection button.
     * @throws IndexOutOfBoundsException
     *             if the index < 0 or the index >= number of selection buttons.
     */
    public SelectionButtonSimulator getSelectionButton(int index) {
	return buttons[index];
    }

    /**
     * Returns the number of selection buttons.
     */
    public int getNumberOfSelectionButtons() {
	return buttons.length;
    }

    /**
     * Returns the coin slot.
     */
    public CoinSlotSimulator getCoinSlot() {
	return coinSlot;
    }

    /**
     * Returns the coin receptacle.
     */
    public CoinReceptacleSimulator getCoinReceptacle() {
	return receptacle;
    }

    /**
     * Returns the storage bin.
     */
    public CoinReceptacleSimulator getStorageBin() {
	return storageBin;
    }

    /**
     * Returns the delivery chute.
     */
    public DeliveryChuteSimulator getDeliveryChute() {
	return deliveryChute;
    }

    /**
     * Returns the number of coin racks.
     */
    public int getNumberOfCoinRacks() {
	return coinRacks.length;
    }

    /**
     * Returns a coin rack at the indicated index.
     * 
     * @param index
     *            The index of the desired coin rack.
     * @throws IndexOutOfBoundsException
     *             if the index < 0 or the index >= number of coin racks.
     */
    public CoinRackSimulator getCoinRack(int index) {
	return coinRacks[index];
    }

    /**
     * Returns the number of pop can racks.
     */
    public int getNumberOfPopCanRacks() {
	return popCanRacks.length;
    }

    /**
     * Returns a pop can rack at the indicated index.
     * 
     * @param index
     *            The index of the desired pop can rack.
     * @throws IndexOutOfBoundsException
     *             if the index < 0 or the index >= number of pop can rack.
     */
    public PopCanRackSimulator getPopCanRack(int index) {
	return popCanRacks[index];
    }

    /**
     * Returns the slot for magnetic stripe cards.
     */
    public CardSlotSimulator getCardSlot() {
	return cardSlot;
    }

    /**
     * Returns the textual display.
     */
    public DisplaySimulator getDisplay() {
	return display;
    }

	public MoneyManager getMoneyManager() {
		return moneyManager;
	}

	public void setMoneyManager(MoneyManager moneyManager) {
		this.moneyManager = moneyManager;
	}
	
	public static void main(String [] args) {
		try {	
			int [] coinValues = { 5, 10, 25, 100, 200 };
			int [] popCosts = { 100, 100, 100 };
			String [] popNames = { "Coke", "Pepsi", "7-up" };
			HardwareSimulator hw = new HardwareSimulator(coinValues, popCosts, popNames);
			DisplaySimulator disp = hw.getDisplay();

			System.out.println(disp.getMessage());
			hw.getCoinSlot().addCoin(new Coin(100));
			System.out.println(disp.getMessage());

		} catch (Exception e) { /* gotta catch em all */ }
	}
}
