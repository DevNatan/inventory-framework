package me.devnatan.inventoryframework.component;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

public class TestItemComponentBuilder extends AbstractComponentBuilder<TestItemComponentBuilder, IFContext>
        implements ItemComponentBuilder<TestItemComponentBuilder, IFContext>, ComponentFactory {

    int slot;

    public TestItemComponentBuilder() {
        this(null, null, false, false, false, null, false, null);
    }

    protected TestItemComponentBuilder(
            String referenceKey,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
            Predicate<IFContext> displayCondition) {
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
