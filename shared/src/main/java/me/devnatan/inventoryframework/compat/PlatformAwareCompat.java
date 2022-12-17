package me.devnatan.inventoryframework.compat;

import org.jetbrains.annotations.NotNull;

/**
 * Interface denoting a virtual view that may be platform-specific.
 * <p>
 * This is just to keep the code backwards compatible with versions before 2.5.4 that had specific
 * platform references in the public API.
 *
 * @param <TViewer> The viewer type for that platform.
 */
public interface PlatformAwareCompat<TViewer> {

    /**
     * The FIRST viewer linked to this context.
     * <p>
     * "First" because contexts can be shared and contain multiple viewers, this function will
     * always return the first player in the viewer list.
     *
     * @return The first viewer linked to this context.
     */
    @NotNull
    TViewer getPlayer();
}
