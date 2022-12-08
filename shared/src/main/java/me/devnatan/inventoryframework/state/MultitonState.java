package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface MultitonState<K, T> extends State<T> {

	T value(K key);
}
