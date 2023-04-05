package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

public class TestItemComponentBuilder extends DefaultComponentBuilder<TestItemComponentBuilder>
        implements ItemComponentBuilder<TestItemComponentBuilder>, ComponentFactory {

    int slot;

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
    public TestItemComponentBuilder watch(State<?>... states) {
        return null;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return slot == position;
    }
}
