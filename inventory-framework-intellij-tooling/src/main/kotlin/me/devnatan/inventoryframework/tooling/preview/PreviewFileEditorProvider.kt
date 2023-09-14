package me.devnatan.inventoryframework.tooling.preview

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
        private const val EDITOR_ID = "inventory-framework-preview"
        private const val EDITOR_NAME = "Inventory Framework"
    }

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.nameWithoutExtension.endsWith("View")
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return TextEditorWithPreview(
            /* editor = */ TextEditorProvider.getInstance().createEditor(project, file) as TextEditor,
            /* preview = */ PreviewFileEditor(project, file),
            /* editorName = */ EDITOR_NAME
        )
    }

    override fun getEditorTypeId(): String {
        return EDITOR_ID
    }

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}