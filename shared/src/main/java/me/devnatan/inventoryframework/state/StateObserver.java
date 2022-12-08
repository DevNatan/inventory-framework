package me.devnatan.inventoryframework.state;

import javax.swing.plaf.nimbus.State;

public interface StateObserver {

	void updateCaught(State<?> state, StateOwner target, Object oldValue, Object newValue);

}
