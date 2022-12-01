package me.devnatan.inventoryframework.tooling

import com.intellij.psi.PsiClass

fun findDeclaration(element: PsiClass): ViewDeclaration {
    return ViewDeclaration(
        "Piroca de foice",
        1,
        slots = listOf(),
        type = ViewType.Chest
    )
}