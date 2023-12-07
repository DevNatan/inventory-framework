package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.feature.Feature;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

@SuppressWarnings({"unused"})
public final class ProxyFeature implements Feature<Void, Void, ViewFrame> {

	/**
	 * Instance of the Proxy feature.
	 *
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/inventory-proxy">Inventory Proxy on Wiki</a>
	 */
	public static final Feature<Void, Void, ViewFrame> Proxy = new ProxyFeature();

	@Override
	public @NotNull String name() {
		return "Proxy";
	}

	@Override
	public @NotNull Void install(ViewFrame framework, UnaryOperator<Void> configure) {
		return null;
	}

	@Override
	public void uninstall(ViewFrame framework) {
	}
}
