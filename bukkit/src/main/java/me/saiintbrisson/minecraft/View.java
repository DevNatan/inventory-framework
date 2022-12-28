package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.RootView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public class View extends AbstractView implements RootView {

	protected View() {
		this(0);
	}

	protected View(int size) {
		this(size, null);
	}

	protected View(String title) {
		this(0, title);
	}

	protected View(String title, @NotNull ViewType type) {
		this(0, title, type);
	}

	protected View(@NotNull ViewType type) {
		this(0, null, type);
	}

	protected View(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	public View(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

}
