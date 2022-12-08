package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface BiMultitonState<T, A, B> extends State<T> {

	T value(A key1, B key2);
}
