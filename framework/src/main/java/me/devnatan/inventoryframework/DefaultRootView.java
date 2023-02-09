package me.devnatan.inventoryframework;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

class DefaultRootView implements RootView {

    private ViewConfig config;
    private final Pipeline<? super VirtualView> pipeline = new Pipeline<>();
    private final Set<IFContext> contexts = Collections.newSetFromMap(Collections.synchronizedMap(new HashMap<>()));

    @Override
    public final @NotNull @UnmodifiableView Set<IFContext> getContexts() {
        return Collections.unmodifiableSet(contexts);
    }

    @Override
    public final void addContext(@NotNull IFContext context) {
        synchronized (contexts) {
            contexts.add(context);
        }
    }

    @Override
    public final void removeContext(@NotNull IFContext context) {
        synchronized (contexts) {
            contexts.add(context);
        }
    }

    @Override
    public final void renderContext(@NotNull IFContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void renderItem(@NotNull IFContext context, @NotNull IFItem<?> item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void removeItem(@NotNull IFContext context, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final @NotNull Pipeline<? super VirtualView> getPipeline() {
        return pipeline;
    }

    @Override
    public final @UnmodifiableView List<Component> getComponents() {
        return null;
    }

    @Override
    public final @Nullable IFItem<?> getItem(int index) {
        return null;
    }

    @Override
    public void onInit(ViewConfigBuilder config) {}
}
