@file:JvmSynthetic
@file:JvmName("ViewDslExtensions")
@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

import me.devnatan.inventoryframework.ViewType

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
internal annotation class ViewDsl

@PublishedApi
internal val factory: me.devnatan.inventoryframework.internal.ViewComponentFactory
    inline get() = me.devnatan.inventoryframework.internal.PlatformUtils.getFactory()

public inline fun createView(
    size: Int = 0,
    title: String? = null,
    type: ViewType = ViewType.CHEST,
    content: ViewBuilder.() -> Unit
): AbstractView {
    val view = factory.createView(size, title, type)
    // TODO fix it
//    val builder = ViewBuilder().apply(content)
//    builder.slots.forEach { view.items[it.slot] = it.toItem() }
    return view
}
