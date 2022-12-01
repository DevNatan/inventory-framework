package me.devnatan.inventoryframework.tooling

data class ViewDeclaration(
    val title: String,
    val lines: Int,
    val slots: List<SlotDeclaration>,
    val type: ViewType
)

sealed class ViewType(
    val width: Int,
    val scale: Int,
    val singleLineHeight: Int,
    val lineDiff: Int
) {

    object Chest : ViewType(
        width = 176,
        scale = 2,
        singleLineHeight = 42,
        lineDiff = 18,
    )

}

data class SlotDeclaration(
    val line: Int,
    val slot: Int,
    val item: String,
    val isSelected: Boolean
)