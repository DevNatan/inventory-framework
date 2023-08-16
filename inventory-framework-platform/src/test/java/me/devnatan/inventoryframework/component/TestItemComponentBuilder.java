package me.devnatan.inventoryframework.component;

import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

public class TestItemComponentBuilder extends DefaultComponentBuilder<TestItemComponentBuilder>
        implements ItemComponentBuilder<TestItemComponentBuilder>, ComponentFactory {

    int slot;

    protected TestItemComponentBuilder(
            String referenceKey,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
            BooleanSupplier displayCondition) {
        super(
                referenceKey,
                data,
                cancelOnClick,
                closeOnClick,
                updateOnClick,
                watchingStates,
                isManagedExternally,
                displayCondition);
    }

    @Override
    public @NotNull Component create() {
        return null;
    }

    @Override
    public TestItemComponentBuilder withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    @Override
    public TestItemComponentBuilder withSlot(int row, int column) {
        return null;
    }

    @Override
    public TestItemComponentBuilder watch(State<?>... states) {
        return null;
    }

    @Override
    public TestItemComponentBuilder copy() {
        return this;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return slot == position;
    }
}
