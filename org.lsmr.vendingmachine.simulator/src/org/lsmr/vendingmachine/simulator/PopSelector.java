package org.lsmr.vendingmachine.simulator;

public class PopSelector implements SelectionButtonSimulatorListener {
	private PopCanRackSimulator connectedPopRack;	//The pop rack for this button
	private int popCost;							//Cost of this pop
	private MoneyManager moneyManager;				//Money manager for knowing if we have enough money
	private int indexNumber;						//Index number of the PopSelector (and the button and pop rack it is connected to.);
	private DisplaySimulator disp;
	private CoinReceptacleSimulator receptacle;
	private HardwareSimulator hardware;
	private CoinRackSimulator connectedCoinRack;
	private int[] cValues;

	public PopSelector(HardwareSimulator hw, int[] coinValues, int cost, int index) {
		connectedPopRack = hw.getPopCanRack(index);		//Link this to the associated pop rack
		hw.getSelectionButton(index).register(this);	//Register to appropriate button
		disp = hw.getDisplay();
		receptacle = hw.getCoinReceptacle();
		hardware = hw;
		cValues = coinValues;
		
		popCost = cost;									//Cost of this pop
		moneyManager = hw.getMoneyManager();			//Get the money manager
		indexNumber = index;							//Set indexNumber
		disp = hw.getDisplay();							//Get display
	}
	
	//Default constructor
	public PopSelector() {
		connectedPopRack = null;
		popCost = 0;
		moneyManager = null;
		indexNumber = -1;
	}
	
	@Override
	public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(SelectionButtonSimulator button) {
		// TODO Auto-generated method stub
		
		if (moneyManager.getSum() >= popCost)
		{//Check if we have enough money to dispense the pop.
			
			try
			{
				int sum = moneyManager.getSum();
				int difference;
				int change;
				if (hardware.getOutOfOrderLight().isActive()) {
					moneyManager.coinsRemoved(receptacle);
				}
				else if (moneyManager.getSum() > popCost) {
					receptacle.storeCoins();
					difference = sum - popCost;
					for (int i = hardware.getNumberOfCoinRacks() - 1; i >= 0; i--) {
						connectedCoinRack = hardware.getCoinRack(i);
						change = difference/cValues[i];
						for (int j = 0; j < change; j++)
							connectedCoinRack.releaseCoin();
						difference = difference - (change * cValues[i]);
					}
				}
				else if (sum == popCost)
					receptacle.storeCoins();
				else if (hardware.getExactChangeLight().isActive())
					hardware.getDisplay().display("Insufficient Funds");
				
				moneyManager.coinsRemoved(receptacle);
			
				connectedPopRack.dispensePop();	//Try to dispense the pop
				
				System.out.println("Pop successfully dispensed from index " + indexNumber);
			}
			catch (DisabledException e)
			{//Catch exception for pop rack being disabled.
				
				System.out.println("DisabledException in PopSelector while dispensing pop from index " + indexNumber);
				e.printStackTrace();
			}
			catch (EmptyException e)
			{//Catch exception for when pop rack selected does not have pop, i.e. it is empty/out of stock.
				
				System.out.println("EmptyException in PopSelector while dispensing pop from index " + indexNumber);
				e.printStackTrace();
				
				String oldMessage = disp.getMessage();	//Save old message
				disp.display("OUT OF STOCK");		//Display an out of stock message
				System.out.println(disp.getMessage());
				
				try {	//Wait for 5 seconds
					Thread.sleep(5000);
				}
				catch (Exception eb) {/*Catch all*/}
				
				disp.display(oldMessage);		//Restore old message
				System.out.println(disp.getMessage());
				
			}
			catch (CapacityExceededException e)
			{//Catch exception for exceeding capacity. I think when too many pops are loaded into the rack.
				
				System.out.println("CapacityExceededException in PopSelector while dispensing pop from index " + indexNumber);
				e.printStackTrace();
			}
		}
		else {//Else where it falls down where there is insufficient cost.
			
			System.out.println("Insufficient coins for pop.");
			
			String oldMessage = disp.getMessage();								//Save old message
			
			disp.display("Insufficient funds [REQUIRED: $" + Double.toString(( (double)popCost ) / 100) + "]");	//Display price of pop
			
			try {	//Wait for 4 seconds
				Thread.sleep(4000);
			}
			catch (Exception eb) {/*Catch all*/}
			
			disp.display(oldMessage);		//Restore old message
			
		}
		
	}

}
