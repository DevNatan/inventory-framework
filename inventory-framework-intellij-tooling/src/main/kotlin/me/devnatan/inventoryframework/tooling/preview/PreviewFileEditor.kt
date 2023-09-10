package me.devnatan.inventoryframework.tooling.preview

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import me.devnatan.inventoryframework.ViewType
import me.devnatan.inventoryframework.tooling.model.IFDeclaration
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class PreviewFileEditor(project: Project, @JvmField val file: VirtualFile) : UserDataHolderBase(), FileEditor {

    private val ui = PreviewComponent(project, file)

    override fun getComponent(): JComponent = ui

    override fun getPreferredFocusedComponent(): JComponent? = null

    override fun getName(): String = "Inventory Framework Preview"

    override fun setState(state: FileEditorState) {
    }

    override fun isModified() = false
    override fun isValid() = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun dispose() {
    }

    override fun selectNotify() {
        ui.redraw()
    }
}