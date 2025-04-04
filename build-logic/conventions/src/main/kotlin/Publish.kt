import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.signing.SigningExtension

fun Project.configureMavenPublish() {
    plugins.apply("maven-publish")
    plugins.apply("signing")

    val isReleaseVersion = !project.version.toString().endsWith("SNAPSHOT")

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("javaOSSRH") {
                groupId = rootProject.group.toString()
                artifactId = project.name
                version = rootProject.version.toString()
                from(components.named("java").get())

                pom {
                    name.set("inventory-framework")
                    description.set("Minecraft Inventory API framework")
                    url.set("https://github.com/DevNatan/inventory-framework")
                    inceptionYear.set("2020")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/DevNatan/inventory-framework/blob/main/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            name.set("Natan Vieira do Nascimento")
                            email.set("natanvnascimento@gmail.com")
                            url.set("https://github.com/DevNatan")
                        }
                    }
                    scm {
                        connection.set("scm:git:git:github.com/DevNatan/inventoryframework.git")
                        developerConnection.set("scm:git:https://github.com/DevNatan/inventoryframework.git")
                        url.set("https://github.com/DevNatan/inventoryframework")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = uri(
                    if (isReleaseVersion)
                        "https://central.sonatype.com/repository/maven-snapshots/"
                    else
                        "https://ossrh-staging-api.central.sonatype.com/service/local/"
                )
                credentials {
                    username = findProperty("ossrh.username") as String? ?: System.getenv("OSSRH_USERNAME")
                    password = findProperty("ossrh.password") as String? ?: System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }

    configure<SigningExtension> {
        isRequired = isReleaseVersion && gradle.taskGraph.hasTask("publish")
        useInMemoryPgpKeys(
            findProperty("signing.keyId") as String? ?: System.getenv("OSSRH_SIGNING_KEY"),
            findProperty("signing.password") as String? ?: System.getenv("OSSRH_SIGNING_PASSWORD")
        )

        sign(the<PublishingExtension>().publications.named("javaOSSRH").get())
    }
}