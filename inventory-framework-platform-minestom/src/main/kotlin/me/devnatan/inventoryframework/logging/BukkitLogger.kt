package me.devnatan.inventoryframework.logging

class BukkitLogger(
    private val logger: java.util.logging.Logger,
    private val viewName: String,
    private val isShaded: Boolean
) : Logger {
    private var finalPrefix: String? = null

    override fun getPrefix(): String? {
        if (finalPrefix == null) {
            finalPrefix = String.format("%s[%s]", if (isShaded) "[IF]" else "", viewName) + " "
        }

        return finalPrefix
    }

    override fun debug(message: String) {
        logger.info(prefix + message)
    }

    override fun warn(message: String) {
        logger.warning(prefix + message)
    }

    override fun error(message: String) {
        logger.severe(prefix + message)
    }
}
