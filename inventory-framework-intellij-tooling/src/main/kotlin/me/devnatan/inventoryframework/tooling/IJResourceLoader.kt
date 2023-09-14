package me.devnatan.inventoryframework.tooling

import io.github.classgraph.ClassGraph
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

object IJResourceLoader {
    var dir: String? = null

    init {
        if (Thread.currentThread().getContextClassLoader() == null) {
            // without jar
            dir = System.getProperty("user.dir")
            if (dir == null) {
                val currentDirFile = File(".")
                val helper = currentDirFile.absolutePath
                try {
                    dir = helper.substring(0, helper.length - currentDirFile.getCanonicalPath().length)
                } catch (ignore: Exception) {
                }
            }
            dir = File(dir, "resource").absolutePath
        }
    }

    fun getResource(anyClassFromYouProject: Class<*>, filePath: String?): URL? {
        if (dir != null) {
            try {
                return URL("file://" + File(dir, filePath).absolutePath)
            } catch (ignore: Exception) {
            }
        }
        var url: URL? = null
        url = anyClassFromYouProject.getClassLoader().getResource(filePath)
        if (url != null) {
            return url
        }
        Thread.currentThread().setContextClassLoader(anyClassFromYouProject.getClassLoader())
        url = Thread.currentThread().getContextClassLoader().getResource(filePath)
        if (url != null) {
            return url
        }
        val resources = ClassGraph().acceptPaths(filePath).scan().allResources
        if (!resources.isEmpty()) {
            resources[0].uri
        }
        return null
    }

    fun getResourceAsStream(anyClassFromYouProject: Class<*>, filePath: String?): InputStream? {
        val url = getResource(anyClassFromYouProject, filePath)
        if (url != null) {
            try {
                return url.openStream()
            } catch (ignore: IOException) {
            }
        }
        return null
    }
}