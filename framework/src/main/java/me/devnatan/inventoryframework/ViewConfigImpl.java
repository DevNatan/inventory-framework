package me.devnatan.inventoryframework;

import lombok.Setter;
import lombok.experimental.Accessors;
import me.saiintbrisson.minecraft.ViewType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation for ViewConfig.
 */
@Setter
@Accessors(chain = true, fluent = true)
final class ViewConfigImpl implements ViewConfig {

	final List<Modifier> modifierList = new LinkedList<>();

	private String title;
	private ViewType type;
	private int size;
	private String[] layout;
	private int flags;

	@Override
	public @NotNull @Unmodifiable List<Modifier> getAppliedModifiers() {
		return Collections.unmodifiableList(modifierList);
	}

	@Override
	public ViewConfig inheritFrom(@NotNull ViewConfig other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ViewConfig with(@NotNull Modifier modifier) {
		modifierList.add(modifier);
		return this;
	}

	@Override
	public ViewConfig layout(String... layout) {
		this.layout = layout;
		return this;
	}

	@Override
	public ViewConfig flags(int flags) {
		this.flags = flags;
		return this;
	}

	@Override
	public ViewConfig flags(int flag, int... others) {
		int value = flag;
		for (final int other : others) value = value | other;
		this.flags = value;
		return this;
	}
}
