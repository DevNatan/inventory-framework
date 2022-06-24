@file:JvmSynthetic

package me.saiintbrisson.minecraft

internal typealias ContextBlock = @ViewDsl ViewContext.() -> Unit
internal typealias SlotContextBlock = @ViewDsl ViewSlotContext.() -> Unit
internal typealias SlotMoveContextBlock = @ViewDsl ViewSlotMoveContext.() -> Unit
internal typealias ItemReleaseBlock = @ViewDsl ViewSlotContext.(to: ViewSlotContext) -> Unit
internal typealias HotbarInteractBlock = @ViewDsl ViewSlotContext.(hotbarButton: Int) -> Unit
