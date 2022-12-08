package me.devnatan.inventoryframework.state;

public interface ComputedState<T> extends State<T> {

	T fetchValue();

}
