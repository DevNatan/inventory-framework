import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    `java-library`
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kotlin) apply false
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "maven-publish")

    group = "me.devnatan"
    version = "3.1.0-rc"

    dependencies {
        annotationProcessor(rootProject.libs.lombok)
        compileOnly(rootProject.libs.lombok)
        compileOnly(rootProject.libs.jetbrains.annotations)
        testCompileOnly(rootProject.libs.jetbrains.annotations)
        testRuntimeOnly(rootProject.libs.junit.engine)
        testImplementation(rootProject.libs.junit.api)
        testImplementation(rootProject.libs.mockito.core)
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    configure<PublishingExtension> {
        val isReleaseVersion = System.getProperties().containsKey("release")
        repositories {
            maven("ossrh") {
                val repoUrl = if (isReleaseVersion)
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                else
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                setUrl(repoUrl)
            }
        }

        publications.withType<MavenPublication> {
            version = if (isReleaseVersion)
                project.version.toString()
            else
                "${project.version}-SNAPSHOT"
            from(components["java"])

            pom {
                name = "inventory-framework"
                description = "Inventory Framework is a Bukkit Inventory API framework that allows you to create custom inventories with varied interaction treatments"
                url = "https://github.com/DevNatan/inventory-framework"
                inceptionYear = "2020"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/DevNatan/inventory-framework/blob/main/LICENSE"
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
                    connection = "scm:git:git:github.com/DevNatan/inventoryframework.git"
                    developerConnection = "scm:git:https://github.com/DevNatan/inventoryframework.git"
                    url = "https://github.com/DevNatan/inventoryframework"
                }
            }
        }
    }

    configure<SpotlessExtension> {
        java {
            removeUnusedImports()
            palantirJavaFormat()
        }
    }

    tasks {
        javadoc {
            options.memberLevel = JavadocMemberLevel.PACKAGE
        }
    }
}
