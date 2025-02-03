package me.devnatan.inventoryframework.util

object IsTypeOf {
    fun isTypeOf(superCls: Class<*>, cls: Class<*>): Boolean {
        return superCls.isAssignableFrom(cls)
    }
}
