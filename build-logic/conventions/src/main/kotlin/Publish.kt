import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureInventoryFrameworkPublication() {
    plugins.apply("com.vanniktech.maven.publish.base")

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()
        pomFromGradleProperties()
        configureBasedOnAppliedPlugins()
    }
}
