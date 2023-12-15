package me.devnatan.inventoryframework.internal;

public class PaperElementFactory extends BukkitElementFactory {

	private static final boolean PAPER_SUPPORTED;
	private static final boolean FOLIA_SUPPORTED;

	static {
		PAPER_SUPPORTED = checkClass("com.destroystokyo.paper.ParticleBuilder");
		FOLIA_SUPPORTED = checkClass("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
	}

	@Override
	public synchronized boolean worksInCurrentPlatform() {
		return PAPER_SUPPORTED;
	}

	private static boolean checkClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (final ClassNotFoundException ignored) {
		}
		return false;
	}
}
