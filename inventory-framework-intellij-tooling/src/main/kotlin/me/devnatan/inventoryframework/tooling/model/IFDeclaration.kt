package me.devnatan.inventoryframework.tooling.model

import me.devnatan.inventoryframework.ViewType

data class IFDeclaration(
    val title: String,
    val lines: Int,
    val type: ViewType,
)