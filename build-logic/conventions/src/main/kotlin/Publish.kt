import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureInventoryFrameworkPublication() {
    plugins.apply("com.vanniktech.maven.publish.base")

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
        pomFromGradleProperties()
        configureBasedOnAppliedPlugins()
    }
}
