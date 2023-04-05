package me.devnatan.inventoryframework.tooling.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import me.devnatan.inventoryframework.tooling.IFBundle

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    fun getRandomNumber() = (1..100).random()
}
