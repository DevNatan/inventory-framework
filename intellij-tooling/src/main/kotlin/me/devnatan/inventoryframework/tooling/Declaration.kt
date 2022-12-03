package br.com.devsrsouza.kotlinbukkitapi.tooling.menu

data class MenuDeclaration(
    val displayname: String,
    val lines: Int,
    val slots: List<MenuSlotDeclaration>
)

data class MenuSlotDeclaration(
        val line: Int,
        val slot: Int,
        val item: String,
        val isSelected: Boolean = false
)
