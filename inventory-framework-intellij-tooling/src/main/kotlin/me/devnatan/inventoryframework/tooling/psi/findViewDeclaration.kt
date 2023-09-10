package me.devnatan.inventoryframework.tooling.psi

import com.intellij.psi.PsiElement
import me.devnatan.inventoryframework.ViewType
import me.devnatan.inventoryframework.tooling.model.IFDeclaration
import me.devnatan.inventoryframework.tooling.preview.ViewComponent

val IF_VIEW_NAME_EXPR = Regex("me.devnatan.inventoryframework.([-a-zA-Z]*)View")

fun elementToDeclaration(element: PsiElement): IFDeclaration {
    return IFDeclaration(
        title = "Macaco lol",
        lines = 5,
        type = ViewType.CHEST,
    )
}

fun elementToComponent(element: PsiElement): ViewComponent {
    val declaration = elementToDeclaration(element)
    return ViewComponent(declaration)
}