import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File

internal abstract class GenerateVersionFileTask : DefaultTask() {

    init {
        group = "inventoryFramework"
    }

    @get:Input
    abstract val version: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val packageDir = outputDir.get().dir("me/devnatan/inventoryframework/runtime").asFile
        packageDir.mkdirs()

        val file = File(packageDir, "InventoryFramework.java")
        file.writeText(
            """
            package me.devnatan.inventoryframework.runtime;

            import org.bukkit.plugin.java.JavaPlugin;

            public final class InventoryFramework extends JavaPlugin {

                public static final String LIBRARY_VERSION = "${version.get()}";
            }
            
            """.trimIndent()
        )
    }
}

internal fun Project.registerGenerateVersionFileTask() {
    val generateVersionFileTask = tasks.register<GenerateVersionFileTask>("generateVersionFile") {
        version.set(project.version.toString())
        outputDir.set(layout.buildDirectory.dir("generated/sources/ifversion"))
    }

    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main") {
            java.srcDir(generateVersionFileTask.flatMap { it.outputDir })
        }
    }

    tasks.named("compileJava") {
        dependsOn(generateVersionFileTask)
    }
}