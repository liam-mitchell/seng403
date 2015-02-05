package org.lsmr.vendingmachine.simulator;

public class PopSelector implements SelectionButtonSimulatorListener {
	private PopCanRackSimulator connectedPopRack;
	private int popCost;
	private MoneyManager moneyManager;

	public PopSelector(HardwareSimulator hw, PopCanRackSimulator pr, int cost) {
		connectedPopRack = pr;
		popCost = cost;
		moneyManager = hw.getMoneyManager();
	}
	
	//Default constructor
	public PopSelector() {
		connectedPopRack = null;
		popCost = 0;
		moneyManager = null;
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
		{
			try {
				connectedPopRack.dispensePop();
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CapacityExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
