import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

fun Project.configureMavenPublish() {
    plugins.apply("maven-publish")

    val publicationVersion = project.version.toString() + "-SNAPSHOT"
    val isReleaseVersion = !publicationVersion.endsWith("SNAPSHOT")

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("javaOSSRH") {
                groupId = rootProject.group.toString()
                artifactId = project.name
                version = publicationVersion
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
                        "https://ossrh-staging-api.central.sonatype.com/service/local/"
                    else
                        "https://central.sonatype.com/repository/maven-snapshots/"
                )
                credentials {
                    username = findProperty("ossrh.username") as String? ?: System.getenv("OSSRH_USERNAME")
                    password = findProperty("ossrh.password") as String? ?: System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }
}