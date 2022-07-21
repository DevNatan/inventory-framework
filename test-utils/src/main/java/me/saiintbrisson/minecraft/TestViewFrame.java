package me.saiintbrisson.minecraft;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
class TestViewFrame implements PlatformViewFrame<Void, Void, TestViewFrame> {

    private ViewErrorHandler errorHandler;
    private boolean registered;
    private Function<PaginatedViewContext<?>, ViewItem> previousPageItem, nextPageItem;

    @Override
    public @NotNull Void getPlatform() {
        return null;
    }

    @Override
    public Collection<Feature<?, ?>> getInstalledFeatures() {
        return null;
    }

    @Override
    public <C, R> @NotNull R install(@NotNull Feature<C, R> feature, @NotNull UnaryOperator<C> configure) {
        return null;
    }

    @Override
    public void uninstall(@NotNull Feature<?, ?> feature) {}

    @Override
    public TestViewFrame with(@NotNull AbstractView... views) {
        return null;
    }

    @Override
    public TestViewFrame remove(@NotNull AbstractView... views) {
        return null;
    }

    @Override
    public TestViewFrame register() {
        registered = true;
        return this;
    }

    @Override
    public void unregister() {
        registered = false;
    }

    @Override
    public @NotNull Void getOwner() {
        return null;
    }

    @Override
    public @NotNull ViewComponentFactory getFactory() {
        return null;
    }

    @Override
    public <R extends AbstractView> R open(@NotNull Class<R> viewClass, @NotNull Void viewer) {
        return null;
    }

    @Override
    public <R extends AbstractView> R open(
            @NotNull Class<R> viewClass, @NotNull Void viewer, Map<String, Object> data) {
        return null;
    }

    @Override
    public void nextTick(Runnable runnable) {
        try {
            Thread.sleep(50); // simulate a "next tick"
            runnable.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
        return null;
    }

    @Override
    public void setDefaultPreviousPageItem(
            Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItemFactory) {}

    @Override
    public TestViewFrame setNavigateBackItemFactory(
            BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory) {
        return null;
    }

    @Override
    public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
        return null;
    }

    @Override
    public void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItemFactory) {}

    @Override
    public TestViewFrame setNavigateNextItemFactory(
            BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory) {
        return null;
    }
}
