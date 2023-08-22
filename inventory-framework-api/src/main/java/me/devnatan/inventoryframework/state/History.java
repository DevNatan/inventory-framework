package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.RootView;
import org.jetbrains.annotations.ApiStatus;

/**
 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
 * such API may be changed or may be removed completely in any further release. </i></b>
 */
@ApiStatus.Experimental
public interface History extends StateValue {

	int getIndex();

	void rewind();

	void pop();
}
