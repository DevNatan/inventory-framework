package me.devnatan.inventoryframework.tooling.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import me.devnatan.inventoryframework.tooling.IFBundle
import me.devnatan.inventoryframework.tooling.services.MyProjectService
import javax.swing.JButton


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    private val contentFactory = ContentFactory.SERVICE.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = contentFactory.createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(IFBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(IFBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = IFBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }
}
