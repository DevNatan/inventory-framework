package me.saiintbrisson.minecraft.exception;

public final class MissingFeatureException extends InventoryFrameworkException {

	public MissingFeatureException(String featureName, String featureImport) {
		super(String.format(
			"Feature \"%s\" is missing. You need to import \"%s\" and install on your view frame with #install(%s).",
			featureName,
			featureImport,
			featureName
		), null);
	}

}
