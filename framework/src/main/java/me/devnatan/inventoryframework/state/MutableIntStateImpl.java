package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class MutableIntStateImpl extends BaseMutableState<Integer> implements MutableIntState {

    public MutableIntStateImpl(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public int increment(@NotNull StateValueHost host) {
        return update(host, 1);
    }

    @Override
    public int decrement(@NotNull StateValueHost host) {
        return update(host, -1);
    }

    private int update(@NotNull StateValueHost host, int diff) {
        final int curr = get(host);
        set(curr + diff, host);
        return curr + diff;
    }
}
