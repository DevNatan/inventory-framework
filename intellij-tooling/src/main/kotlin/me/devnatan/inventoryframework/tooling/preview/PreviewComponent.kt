package me.devnatan.inventoryframework.tooling.preview

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.ui.AncestorListenerAdapter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import me.devnatan.inventoryframework.tooling.model.IFDeclaration
import me.devnatan.inventoryframework.tooling.preview.ViewComponent.Companion.CHEST_SCALE
import me.devnatan.inventoryframework.tooling.preview.ViewComponent.Companion.CHEST_WIDTH
import me.devnatan.inventoryframework.tooling.psi.IF_VIEW_NAME_EXPR
import me.devnatan.inventoryframework.tooling.psi.elementToComponent
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.event.AncestorEvent

class PreviewComponent(private val project: Project, file: VirtualFile) : JLayeredPane() {

    private val doc = FileDocumentManager.getInstance().getDocument(file)!!

    init {
        layout = BorderLayout()
        isVisible = true
        minimumSize = Dimension(
            CHEST_WIDTH * CHEST_SCALE + 10,
            0
        )

        addMouseWheelListener {
            // DO NOTHING
        }

        redraw()
        addAncestorListener(object: AncestorListenerAdapter() {
            override fun ancestorMoved(event: AncestorEvent?) {
                redraw()
            }
        })
    }

    fun redraw() {
        val ui = this
        if (!ui.isShowing) return

        val tree = PsiDocumentManager.getInstance(project).getPsiFile(doc) ?: return

        ui.removeAll()

        val content = JBScrollPane(
            panel {
                row {
                    button("Rebuild") {
                        redraw()
                    }
                }

                for (el in tree.children) {
                    if (el !is PsiClass || el.isInterface) continue
                    

                    try {
                        val child = el.extendsList?.children?.firstOrNull {
                                other -> other is PsiJavaCodeReferenceElement
                        } as? PsiJavaCodeReferenceElement ?: continue

                        if (!IF_VIEW_NAME_EXPR.matches(child.qualifiedName))
                            continue
                    } catch (_: IndexNotReadyException) { return@panel }

                    row {
                        label("COnfia no pai q vai da bão")
                    }
                    row("Preview") {
                        scrollCell(elementToComponent(el))
                    }
                }
            },
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        )

//        ui.add(
//            JLabel("Currently this source code has no menu declaration!").centerComponent(),
//            BorderLayout.CENTER
//        )
        ui.add(content)

//        val component = _component
//        if (component == null) {
//            ui.add(JPanel().apply {
//                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
//                border = EmptyBorder(10, 10, 0, 0)
//
//                add(JLabel("Piroca de foice"), BorderLayout.CENTER)
//            })
//        }
//
//        val panel = JPanel().apply {
//            layout = FlowLayout(FlowLayout.CENTER, 0, 0)
//            setBounds(
//                ui.width - (preferredSize.width + 20),
//                ui.height - (preferredSize.height + 50),
//                preferredSize.width,
//                preferredSize.height
//            )
//        }
////        ui.setLayer(panel, 1)
//        ui.add(panel)
//        ui.validate()
    }

    private fun JComponent.centerComponent() = JPanel().apply {
        isVisible = true
        layout = GridBagLayout()
        add(this@centerComponent)
    }
}