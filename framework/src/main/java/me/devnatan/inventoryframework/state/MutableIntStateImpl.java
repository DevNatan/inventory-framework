package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

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
