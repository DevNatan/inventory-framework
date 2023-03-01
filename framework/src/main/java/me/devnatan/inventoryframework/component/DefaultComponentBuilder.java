package me.devnatan.inventoryframework.component;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter(AccessLevel.PROTECTED)
@SuppressWarnings("unchecked")
class DefaultComponentBuilder<S extends ComponentBuilder<S>> implements ComponentBuilder<S> {

	private String referenceKey;
	private Map<String, Object> data;
	private boolean cancelOnClick, closeOnClick;

	@Override
	public S referencedBy(@NotNull String key) {
		this.referenceKey = key;
		return (S) this;
	}

	@Override
	public S withData(@NotNull String key, Object value) {
		if (data == null)
			data = new HashMap<>();
		data.put(key, value);
		return (S) this;
	}

	@Override
	public S cancelOnClick() {
		cancelOnClick = !cancelOnClick;
		return (S) this;
	}

	@Override
	public S closeOnClick() {
		closeOnClick = !closeOnClick;
		return (S) this;
	}
}
