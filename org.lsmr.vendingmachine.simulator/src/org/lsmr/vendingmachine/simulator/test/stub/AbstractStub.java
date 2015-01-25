package org.lsmr.vendingmachine.simulator.test.stub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.lsmr.vendingmachine.simulator.AbstractHardware;
import org.lsmr.vendingmachine.simulator.AbstractHardwareListener;
import org.lsmr.vendingmachine.simulator.SimulationException;

public abstract class AbstractStub {
    public interface MethodName {
	String getName();
    }

    protected boolean[] allowed;
    protected int[] actual;
    protected Vector<Integer> expectedProtocol = null;
    protected Vector<Integer> actualProtocol = null;
    private Map<String, Integer> mapNameToIndex =
	    new HashMap<String, Integer>();
    private Map<Integer, String> mapIndexToName =
	    new HashMap<Integer, String>();

    protected AbstractStub() {
	Class<?> klass = this.getClass();
	Method[] methods = klass.getMethods();

	allowed = new boolean[methods.length];
	actual = new int[methods.length];

	for(int i = 0; i < methods.length; i++) {
	    mapNameToIndex.put(methods[i].getName(), new Integer(i));
	    mapIndexToName.put(new Integer(i), methods[i].getName());
	}
    }

    protected int findIndex(String s) {
	Integer i = mapNameToIndex.get(s);
	if(i == null)
	    return -1;
	return i.intValue();
    }

    public int allowCalls(String... methods) {
	int count = 0;
	for(String method : methods) {
	    int index = findIndex(method);
	    if(index >= 0) {
		allowed[index] = true;
		count++;
	    }
	    else
		throw new SimulationException(method + " is unknown");
	}
	return count;
    }

    public int prohibitCalls(String... methods) {
	int count = 0;
	for(String method : methods) {
	    int index = findIndex(method);
	    if(index >= 0) {
		allowed[index] = false;
		count++;
	    }
	    else
		throw new SimulationException(method + " is unknown");
	}
	return count;
    }

    public void expect(String... methods) {
	allowCalls(methods);
	if(expectedProtocol == null) {
	    expectedProtocol = new Vector<Integer>();
	    actualProtocol = new Vector<Integer>();
	}

	for(String method : methods)
	    expectedProtocol.add(findIndex(method));
    }

    public void allowAll() {
	for(int i = 0; i < allowed.length; i++)
	    allowed[i] = true;
    }

    public void prohibitAll() {
	for(int i = 0; i < allowed.length; i++)
	    allowed[i] = false;
    }

    public boolean isAllowed(String method) {
	int index = findIndex(method);
	if(index >= 0)
	    return allowed[index];
	return false;
    }

    protected void recordCallTo(String method) {
	if(isAllowed(method)) {
	    int index = findIndex(method);

	    if(actualProtocol != null)
		actualProtocol.add(index);

	    actual[index]++;

	    if(expectedProtocol != null)
		assertEquals(expectedProtocol.elementAt(actualProtocol.size() - 1).intValue(), index);
	}
	else
	    fail("Prohibited: " + method);
    }

    public void assertCalls(String method, int count) {
	assertEquals(actual[findIndex(method)], count);
    }

    public void assertProtocol() {
	assertEquals(expectedProtocol, actualProtocol);
    }

    public void init() {
	actualProtocol = null;
	expectedProtocol = null;
    }

    public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
	recordCallTo("enabled");
    }

    public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
	recordCallTo("disabled");
    }
}
