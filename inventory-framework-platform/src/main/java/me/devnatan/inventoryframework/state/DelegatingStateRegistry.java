package me.devnatan.inventoryframework.state;

import java.util.Iterator;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class DelegatingStateRegistry implements StateRegistry {

    private final Supplier<StateRegistry> delegate;

    @Override
    public State<?> getState(long id) {
        return delegate.get().getState(id);
    }

    @Override
    public void registerState(@NotNull State<?> state, Object caller) {
        delegate.get().registerState(state, caller);
    }

    @Override
    public void unregisterState(long stateId, Object caller) {
        delegate.get().unregisterState(stateId, caller);
    }

    @NotNull
    @Override
    public Iterator<State<?>> iterator() {
        return delegate.get().iterator();
    }
}
