package me.saiintbrisson.minecraft.logging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class BukkitLogger implements Logger {

	private final java.util.logging.Logger logger;
	private final String viewName;
	private final boolean isShaded;

	private String finalPrefix;

	@Override
	@Nullable
	public String getPrefix() {
		if (finalPrefix == null) {
			finalPrefix = String.format("%s[%s]", isShaded ? "[IF]" : "", viewName) + " ";
		}

		return finalPrefix;
	}

	@Override
	public void debug(String message) {
		logger.info(getPrefix() + message);
	}

	@Override
	public void warn(String message) {
		logger.warning(getPrefix() + message);
	}

	@Override
	public void error(String message) {
		logger.severe(getPrefix() + message);
	}
}
