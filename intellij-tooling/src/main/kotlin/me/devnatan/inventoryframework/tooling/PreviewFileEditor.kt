package me.devnatan.inventoryframework.tooling

import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.progress.ModalTaskOwner.component
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiImportList
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiPackageStatement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.Alarm
import java.awt.BorderLayout
import java.awt.Point
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

class PreviewFileEditor(
    private val project: Project,
    private val docFile: VirtualFile,
    private val editor: TextEditor
) : UserDataHolderBase(), FileEditor {

    companion object {
        const val NAME = "Preview"
        const val REBUILD_DELAY = 80
        private val REGEX = Regex("me.saiintbrisson.minecraft.([-a-zA-Z]*)View")
    }

    private lateinit var currScroll: JBScrollPane
    private var selectedLine: Int? = null

    private val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, this)
    private val doc = FileDocumentManager.getInstance().getDocument(docFile)!!
    private val ui = JPanel().apply {
        layout = BorderLayout()
        isVisible = true
        minimumSize = dimensionOf(ViewType.Chest, 1)
    }

    private val docListener = object : DocumentListener {
        override fun beforeDocumentChange(event: DocumentEvent) {
            alarm.cancelAllRequests()
        }

        override fun documentChanged(event: DocumentEvent) {
            alarm.addRequest({
                rebuild(currScroll.viewport.viewPosition)
            }, REBUILD_DELAY)
        }
    }

    private val caretListener = object : CaretListener {
        override fun caretPositionChanged(event: CaretEvent) {
            selectedLine = event.newPosition.line
            alarm.addRequest({
                rebuild(currScroll.viewport.viewPosition)
            }, REBUILD_DELAY)
        }
    }

    private fun setup() {
        rebuild(null)
        doc.addDocumentListener(docListener)
        editor.editor.caretModel.addCaretListener(caretListener)
    }

    @Suppress("ComplexMethod")
    private fun rebuild(position: Point?) {
        if (!ui.isShowing) return

        val tree = PsiDocumentManager.getInstance(project).getPsiFile(doc) ?: return
        ui.removeAll()

        println("drawing at $position")
        var componentsCount = 0
        currScroll = JBScrollPane(
            panel {
                @Suppress("LoopWithTooManyJumpStatements")
                for (el in tree.children) {
                    if (el is PsiWhiteSpace
                        || el is PsiImportList
                        || el is PsiPackageStatement
                    ) continue

                    println("el: $el")
                    if (el !is PsiClass) continue
                    if (el.isInterface) continue

                    val child = try {
                        el.extendsList?.children?.firstOrNull {
                            it is PsiJavaCodeReferenceElement
                        }
                    } catch (_: IndexNotReadyException) {
                        break
                    }

                    val ref = (child as? PsiJavaCodeReferenceElement)?.qualifiedName ?: continue

                    println("ref: $ref")

                    if (!REGEX.matches(ref)) {
                        println("$ref not matching regex")
                        continue
                    }

                    row {
                        cell(ViewComponent(findDeclaration(el)))
                    }

                    componentsCount++
                }
            }.centered(),
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        ).apply {
            if (position != null)
                viewport.viewPosition = position
        }

        if (componentsCount == 0) {
            ui.add(JLabel("No views found").centered(), BorderLayout.CENTER)
            return
        }

        ui.add(currScroll, BorderLayout.CENTER)
    }

    override fun dispose() {
        alarm.cancelAllRequests()
        doc.removeDocumentListener(docListener)
        editor.editor.caretModel.removeCaretListener(caretListener)
    }

    override fun getName(): String = NAME
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = true
    override fun getComponent(): JComponent = ui
    override fun getPreferredFocusedComponent(): JComponent? = null
    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun setState(state: FileEditorState) {
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun selectNotify() {
        setup()
    }

    override fun deselectNotify() {
        dispose()
    }

}