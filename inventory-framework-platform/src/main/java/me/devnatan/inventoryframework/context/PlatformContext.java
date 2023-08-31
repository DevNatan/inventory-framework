package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import org.jetbrains.annotations.NotNull;

abstract class PlatformContext extends AbstractIFContext {

    @SuppressWarnings("rawtypes")
    @Override
    public abstract @NotNull PlatformView getRoot();

    @SuppressWarnings("unchecked")
    @Override
    public final void openForEveryone(Class<? extends RootView> other, Object initialData) {
        getRoot().getFramework().getRegisteredViewByType(other).open(getViewers(), initialData);
    }
}
