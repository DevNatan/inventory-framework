@file:JvmSynthetic

package me.saiintbrisson.minecraft

import me.devnatan.inventoryframework.context.IFContext
import me.devnatan.inventoryframework.context.IFSlotClickContext
import me.devnatan.inventoryframework.context.IFSlotContext

internal typealias ContextBlock = @ViewDsl IFContext.() -> Unit
internal typealias SlotContextBlock = @ViewDsl IFSlotContext.() -> Unit
internal typealias SlotClickContextBlock = @ViewDsl IFSlotClickContext.() -> Unit
internal typealias ItemReleaseBlock = @ViewDsl IFSlotContext.(to: IFSlotContext) -> Unit
internal typealias HotbarInteractBlock = @ViewDsl IFSlotContext.(hotbarButton: Int) -> Unit
