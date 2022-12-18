package me.saiintbrisson.minecraft.test;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.pagination.IFPaginatedContext;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.PlatformViewFrame;
import me.saiintbrisson.minecraft.ViewComponentFactory;
import me.saiintbrisson.minecraft.ViewErrorHandler;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.Viewer;
import me.saiintbrisson.minecraft.feature.Feature;
import me.saiintbrisson.minecraft.internal.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Job schedule(@NotNull Runnable runnable, long interval, long delay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<IFPaginatedContext<?>, ViewItem> getDefaultPreviousPageItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultPreviousPageItem(Function<IFPaginatedContext<?>, ViewItem> defaultPreviousPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame setNavigateBackItemFactory(
            BiConsumer<IFPaginatedContext<?>, ViewItem> navigateBackItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<IFPaginatedContext<?>, ViewItem> getDefaultNextPageItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultNextPageItem(Function<IFPaginatedContext<?>, ViewItem> defaultNextPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame setNavigateNextItemFactory(
            BiConsumer<IFPaginatedContext<?>, ViewItem> navigateNextItemFactory) {
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

    @Override
    public TestViewFrame withPreviousPageItem(
            @Nullable BiConsumer<IFPaginatedContext<?>, ViewItem> previousPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame withNextPageItem(@Nullable BiConsumer<IFPaginatedContext<?>, ViewItem> nextPageItemFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestViewFrame withErrorHandler(@NotNull ViewErrorHandler errorHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BiConsumer<IFPaginatedContext<?>, ViewItem> getPreviousPageItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BiConsumer<IFPaginatedContext<?>, ViewItem> getNextPageItem() {
        throw new UnsupportedOperationException();
    }
}
