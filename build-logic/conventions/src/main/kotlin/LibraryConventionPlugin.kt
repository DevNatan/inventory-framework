import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        group = rootProject.group
        version = rootProject.version

        configureJava()
        configureLint()
        setupDefaultDependencies()

        val extension = project.extensions.create<InventoryFrameworkExtension>("inventoryFramework")
        project.afterEvaluate {
            if (extension.publish.get()) {
                configureMavenPublish()
            }
        }
    }

    private fun Project.configureJava() {
        pluginManager.apply("java-library")

        with(tasks) {
            withType<Javadoc> {
                options.memberLevel = JavadocMemberLevel.PACKAGE
            }
            withType<JavaCompile>().configureEach {
                options.encoding = "UTF-8"
            }
            withType<Test> { useJUnitPlatform() }
        }
    }

    private fun Project.configureLint() {
        pluginManager.apply("com.diffplug.spotless")
        extensions.configure<SpotlessExtension> {
            java {
                removeUnusedImports()
                palantirJavaFormat()
            }
            kotlin {
                ktfmt().kotlinlangStyle()
                ktlint()
            }
        }

        tasks.register("lint") {
            group = "verification"
            dependsOn("spotlessApply", "spotlessCheck")
        }
    }

    private fun Project.setupDefaultDependencies() {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies.apply {
            add("compileOnly", libs.findLibrary("jetbrains-annotations").get())
            add("testCompileOnly", libs.findLibrary("jetbrains-annotations").get())
            add("testImplementation", platform(libs.findLibrary("junit-bom").get()))
            add("testImplementation", libs.findLibrary("junit-jupiter").get())
            add("testRuntimeOnly", libs.findLibrary("junit-launcher").get())
            add("testImplementation", libs.findLibrary("mockito-core").get())
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}