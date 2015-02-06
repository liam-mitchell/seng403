package org.lsmr.vendingmachine.simulator;

public class PopSelector implements SelectionButtonSimulatorListener {
	private PopCanRackSimulator connectedPopRack;	//The pop rack for this button
	private int popCost;							//Cost of this pop
	private MoneyManager moneyManager;				//Money manager for knowing if we have enough money
	private int indexNumber;						//Index number of the PopSelector (and the button and pop rack it is connected to.);
	private DisplaySimulator disp;

	public PopSelector(HardwareSimulator hw, int cost, int index) {
		connectedPopRack = hw.getPopCanRack(index);		//Link this to the associated pop rack
		hw.getSelectionButton(index).register(this);	//Register to appropriate button
		disp = hw.getDisplay();
		
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
		
		if (moneyManager.getSum() >= popCost)	//Check if we have enough money
		{
			try {
				connectedPopRack.dispensePop();	//Try to dispense the pop
				
				System.out.println("Pop successfully dispensed from index " + indexNumber);
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				System.out.println("DisabledException in PopSelector while dispensing pop from index " + indexNumber);
				e.printStackTrace();
			} catch (EmptyException e) {
				// TODO Auto-generated catch block
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
				
			} catch (CapacityExceededException e) {
				// TODO Auto-generated catch block
				System.out.println("CapacityExceededException in PopSelector while dispensing pop from index " + indexNumber);
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Insufficient coins for pop.");
		}
		
	}

}
