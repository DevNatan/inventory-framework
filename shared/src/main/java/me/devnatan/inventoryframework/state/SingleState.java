package me.devnatan.inventoryframework.state;

public interface SingleState<T> extends State<T> {

	T value();

}
