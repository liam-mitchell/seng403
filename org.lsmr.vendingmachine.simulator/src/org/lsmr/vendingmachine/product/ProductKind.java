package org.lsmr.vendingmachine.product;

import java.util.ArrayList;

import org.lsmr.vendingmachine.funds.Currency;
import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.CapacityExceededException;
import org.lsmr.vendingmachine.simulator.DisabledException;
import org.lsmr.vendingmachine.simulator.EmptyException;
import org.lsmr.vendingmachine.simulator.PopCan;
import org.lsmr.vendingmachine.simulator.PopCanRackListener;
import org.lsmr.vendingmachine.simulator.PopCanRackSimulator;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulator;
import org.lsmr.vendingmachine.simulator.SelectionButtonSimulatorListener;

public class ProductKind implements PopCanRackListener, SelectionButtonSimulatorListener {
    private Currency cost;
    private String name;
    private int quantity;
    private PopCanRackSimulator rack;
    private SelectionButtonSimulator button;
    private ArrayList<ProductKindListener> listeners = new ArrayList<ProductKindListener>();

    public ProductKind(String name, Currency cost, PopCanRackSimulator rack, SelectionButtonSimulator button) {
	this.name = name;
	this.cost = cost;
	this.rack = rack;
	this.button = button;
	rack.register(this);
	button.register(this);
    }
    
    public int getQuantityAvailable() {
	return quantity;
    }

    public Currency getStandardCost() {
	return cost;
    }

    public String getName() {
	return name;
    }

    public void dispense() {
	if(quantity < 1)
	    notifyHardwareFailure();
	else
	    try {
		rack.dispensePop();
	    }
	    catch(DisabledException e) {
		notifyHardwareFailure();
	    }
	    catch(EmptyException e) {
		quantity = 0;
		notifyHardwareFailure();
	    }
	    catch(CapacityExceededException e) {
		notifyHardwareFailure();
	    }
    }

    public void register(ProductKindListener listener) {
	listeners.add(listener);
    }

    private void notifyHardwareFailure() {
	for(ProductKindListener l : listeners)
	    l.hardwareFailure(this);
    }

    private void notifyOutOfStock() {
	for(ProductKindListener l : listeners)
	    l.outOfStock(this);
    }

    @Override
    public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
    }

    @Override
    public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
    }

    @Override
    public void popAdded(PopCanRackSimulator popRack, PopCan pop) {
	if(popRack != rack) {
	    notifyHardwareFailure();
	    return;
	}
	
	quantity++;
    }

    @Override
    public void popRemoved(PopCanRackSimulator popRack, PopCan pop) {
	quantity--;
    }

    @Override
    public void popFull(PopCanRackSimulator popRack) {
    }

    @Override
    public void popEmpty(PopCanRackSimulator popRack) {
	notifyOutOfStock();
    }
    
    @Override
    public void pressed(SelectionButtonSimulator b) {
	if(b != button) {
	    notifyHardwareFailure();
	    return;
	}
	
	notifySelected();
    }
    
    private void notifySelected() {
	for(ProductKindListener l : listeners)
	    l.selected(this);
    }
}
