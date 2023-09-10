package me.devnatan.inventoryframework.tooling.preview

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.task.ProjectTaskManager
import kotlinx.datetime.Clock

class RebuildTask(val project: Project) {

    private val rebuildStart = mutableListOf<() -> Unit>()
    private val rebuildFinish = mutableListOf<() -> Unit>()
    private val projectTaskManager = ProjectTaskManager.getInstance(project)

    private var lastRebuild = Clock.System.now()

    var rebuildInProgress = false
        private set
    private var shouldRebuild = false

    init {
        with(project.messageBus.connect()) {
            subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    run()
                }
            })
        }
    }

    fun onRebuildStart(rebuild: () -> Unit) {
        rebuildStart.add(rebuild);
    }

    fun onRebuildFinish(rebuild: () -> Unit) {
        rebuildFinish.add(rebuild)
    }

    fun run(reschedule: Boolean = true) {
        val now = Clock.System.now()
        if ((now - lastRebuild).inWholeMilliseconds < 100)
            return

        lastRebuild = now
        if (rebuildInProgress) {
            shouldRebuild = reschedule
            return
        }

        rebuildInProgress = true
        rebuildStart.forEach { it() }
        val buildTask = projectTaskManager.createModulesBuildTask(
            /* modules = */ ModuleManager.getInstance(project).modules,
            /* isIncrementalBuild = */ true,
            /* includeDependentModules = */ false,
            /* includeRuntimeDependencies = */ false,
            /* includeTests = */ false
        )
        projectTaskManager
            .run(buildTask)
            .onError {
                rebuildInProgress = false
                shouldRebuild = false
            }.onSuccess {
                rebuildInProgress = false
                rebuildFinish.forEach { it() }

                if (shouldRebuild) {
                    shouldRebuild = false
                    run()
                }
            }
    }
}