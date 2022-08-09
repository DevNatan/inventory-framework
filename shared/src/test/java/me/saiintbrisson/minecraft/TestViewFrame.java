package me.saiintbrisson.minecraft;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import me.saiintbrisson.minecraft.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class TestViewFrame implements PlatformViewFrame<Void, Void, TestViewFrame> {

    @Getter
    @Setter
    private ViewErrorHandler errorHandler;

    @Override
    public @NotNull Void getPlatform() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Feature<?, ?>> getInstalledFeatures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C, R> @NotNull R install(@NotNull Feature<C, R> feature, @NotNull UnaryOperator<C> configure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame with(@NotNull AbstractView... views) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame remove(@NotNull AbstractView... views) {
        return null;
    }

    @Override
    public TestViewFrame register() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregister() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRegistered() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Void getOwner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ViewComponentFactory getFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextTick(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultPreviousPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame setNavigateBackItemFactory(
            BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame setNavigateNextItemFactory(
            BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends AbstractView> @NotNull R open(@NotNull Class<R> viewClass, @NotNull Void viewer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends AbstractView> @NotNull R open(
            @NotNull Class<R> viewClass, @NotNull Void viewer, @NotNull Map<String, Object> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends AbstractView> @NotNull T open(
            @NotNull Class<T> viewClass, @NotNull Viewer viewer, Map<String, Object> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C, R> @NotNull R install(@NotNull Feature<C, R> feature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void uninstall(@NotNull Feature<?, ?> feature) {
        throw new UnsupportedOperationException();
    }
}
