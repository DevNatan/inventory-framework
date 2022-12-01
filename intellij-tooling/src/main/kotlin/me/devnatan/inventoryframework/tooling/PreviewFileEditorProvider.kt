package me.devnatan.inventoryframework.tooling

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.TextEditorWithPreview
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PreviewFileEditorProvider : WeighedFileEditorProvider() {

    companion object {
        const val ID = "inventory-framework-preview"
        const val POSTFIX = "View"
    }

    override fun getEditorTypeId(): String = ID
    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.nameWithoutExtension.endsWith(POSTFIX)
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val editor = TextEditorProvider.getInstance().createEditor(project, file)
        val preview = PreviewFileEditor(project, file, editor as TextEditor)

        return TextEditorWithPreview(editor, preview, PreviewFileEditor.NAME)
    }

}