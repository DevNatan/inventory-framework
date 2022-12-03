package br.com.devsrsouza.kotlinbukkitapi.tooling.menu

import com.intellij.psi.util.collectDescendantsOfType
import com.intellij.psi.util.findDescendantOfType
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isExtensionFunctionType
import org.jetbrains.kotlin.idea.core.util.getLineNumber
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.intentions.isMethodCall
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.typeUtil.isUnit

private val menuFqSupportedNames = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.menu",
    "br.com.devsrsouza.bukkript.script.definition.api.menu"
)

fun findMenuDeclaration(
    element: KtCallExpression,
    currentSelectedLine: Int? = null
): MenuDeclaration? {
    val simpleName = "menu"

    if (!element.isCallFrom(simpleName, menuFqSupportedNames)) return null

    val menuFunction = element.findDescendantOfType<KtCallExpression> {
        isMenuCall(it)
    } ?: return null

    // TODO: Support named parameters

    val displayName = menuFunction.valueArguments.getOrNull(0)?.text?.replace("\"", "") ?: return null
    val lines = menuFunction.valueArguments.getOrNull(1)?.text?.toIntOrNull() ?: return null

    val menuLambdaBlock = element.findDescendantOfType<KtLambdaExpression> {
        it.isBuilderBlockFor("br.com.devsrsouza.kotlinbukkitapi.dsl.menu.MenuDSL")
    } ?: return null

    val slotFqName = listOf("br.com.devsrsouza.kotlinbukkitapi.dsl.menu.slot")
    val slotSimpleName = "slot"

    val slotCalls = menuLambdaBlock.collectDescendantsOfType<KtCallExpression> {
        it.isCallFrom(slotSimpleName, slotFqName)
    }.mapNotNull {

        val args = it.valueArguments

        // TODO: support named parameters

        val line = args.getOrNull(0)?.text?.toIntOrNull() ?: return@mapNotNull null
        val slot = args.getOrNull(1)?.text?.toIntOrNull() ?: return@mapNotNull null

        // TODO: support just slot: slot(15, item(...)) {}

        val itemValue = args.getOrNull(2) ?: return@mapNotNull null
        val expression = itemValue.getArgumentExpression() ?: return@mapNotNull null

        val materialExpression = expression.findDescendantOfType<KtDotQualifiedExpression> {
            it.text.startsWith("Material")
                    && it.receiverExpression.mainReference?.resolve()?.getKotlinFqName()?.asString()
                ?.equals("org.bukkit.Material") == true
        } ?: return@mapNotNull null

        val typeExpression = materialExpression.selectorExpression as? KtNameReferenceExpression ?: return@mapNotNull null
        val materialName = typeExpression.text ?: return@mapNotNull null

        val isSelected = it.getLineNumber() == currentSelectedLine

        MenuSlotDeclaration(
            line,
            slot,
            materialName,
            isSelected
        )
    }

    return MenuDeclaration(displayName, lines, slotCalls)
}

fun isMenuCall(it: KtCallExpression) =
    it.isCallFrom("menu", menuFqSupportedNames)

fun KtLambdaExpression.isBuilderBlockFor(fqName: String): Boolean {
    val type = resolveType()

    val extensionFunctionType = type.isExtensionFunctionType

    if(!extensionFunctionType) return false

    val returnTypeFromFunctionType = type.getReturnTypeFromFunctionType()
    val receiverTypeFromFunctionType = type.getReceiverTypeFromFunctionType() ?: return false
    val receiverFqName = receiverTypeFromFunctionType.fqName ?: return false

    return returnTypeFromFunctionType.isUnit() && receiverFqName.asString().equals(fqName)
}

fun KtCallExpression.isCallFrom(
    simpleName: String,
    fqNames: List<String>
): Boolean {
    return text.startsWith(simpleName) && fqNames.any { isMethodCall(it) }
}