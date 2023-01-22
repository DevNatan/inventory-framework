package me.saiintbrisson.minecraft;

import java.util.Collections;
import java.util.List;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.IFPaginatedSlotContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class TestUtils {

    static ViewContainer createContainer(int columns, int rows) {
        return new ViewContainer() {
            @Override
            public @NotNull ViewType getType() {
                return ViewType.CHEST;
            }

            @Override
            public int getFirstSlot() {
                return 0;
            }

            @Override
            public int getLastSlot() {
                return 0;
            }

            @Override
            public boolean hasItem(int slot) {
                return false;
            }

            @Override
            public void renderItem(int slot, Object item) {}

            @Override
            public void removeItem(int slot) {}

            @Override
            public boolean matchesItem(int slot, Object item, boolean exactly) {
                return false;
            }

            @Override
            public Object convertItem(Object source) {
                return null;
            }

            @Override
            public boolean isSupportedItem(Object item) {
                return false;
            }

            @Override
            public int getSize() {
                return rows * columns;
            }

            @Override
            public int getSlotsCount() {
                return getSize() - 1;
            }

            @Override
            public int getRowsCount() {
                return rows;
            }

            @Override
            public int getColumnsCount() {
                return columns;
            }

            @Override
            public @NotNull @Unmodifiable List<Viewer> getViewers() {
                return Collections.emptyList();
            }

            @Override
            public void open(@NotNull Viewer viewer) {}

            @Override
            public void close() {}

            @Override
            public void changeTitle(@Nullable String title) {}

            @Override
            public boolean isEntityContainer() {
                return false;
            }
        };
    }

    static <T> AbstractPaginatedView<T> createInitializedPaginatedView(int rows) {
        AbstractPaginatedView<T> root = new AbstractPaginatedView<T>(rows, null, ViewType.CHEST) {
            @Override
            protected void onItemRender(
                    @NotNull IFPaginatedSlotContext<T> context, @NotNull IFItem viewItem, @NotNull T value) {
                viewItem.withItem(null);
            }
        };
        root.init(true);
        return root;
    }

    static <T> IFPaginatedContext<T> createPaginatedContext(AbstractPaginatedView<T> root, ViewContainer container) {
        return new BasePaginatedViewContext<>(root, container);
    }
}
