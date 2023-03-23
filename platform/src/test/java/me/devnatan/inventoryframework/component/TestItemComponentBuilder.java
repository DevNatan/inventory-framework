package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

public class TestItemComponentBuilder extends DefaultComponentBuilder<TestItemComponentBuilder>
        implements ItemComponentBuilder<TestItemComponentBuilder>, ComponentFactory {

    @Override
    public @NotNull Component create() {
        return null;
    }

    @Override
    public TestItemComponentBuilder withSlot(int slot) {
        return this;
    }
}
