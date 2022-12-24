package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public class View extends AbstractView {

    protected View() {
        this(0);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(int size) {
        this(size, null);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(String title) {
        this(0, title);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(String title, @NotNull ViewType type) {
        this(0, title, type);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(@NotNull ViewType type) {
        this(0, null, type);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(int size, String title) {
        this(size, title, ViewType.CHEST);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    @Deprecated
    public View(int size, String title, @NotNull ViewType type) {
        super(size, title, type);
    }
}
