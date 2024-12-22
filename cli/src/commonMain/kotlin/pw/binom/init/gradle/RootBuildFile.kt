package pw.binom.init.gradle

import pw.binom.init.Plugin
import pw.binom.init.Project2
import pw.binom.init.Writer
import pw.binom.init.write
import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.use

object RootBuildFile {
    fun generate(file: File, project: Project2) {
        val allPlugins = project.allPlugins.filterIsInstance<Plugin.PluginSection>()
        val allDependencyRepositories = project.allDependencyRepositories
        file.openWrite().bufferedWriter().use { writer ->
            writer.write {
                if (allPlugins.isNotEmpty()) {
                    "plugins" {
                        allPlugins.filterIsInstance<Plugin.PluginSection>().forEach {
                            it.write(output = this, withVersion = true, apply = false)
                        }
                    }
                }
                if (allDependencyRepositories.isNotEmpty()) {
                    "allprojects" {
                        "repositories" {
                            allDependencyRepositories.forEach {
                                it.write(this)
                            }
                        }
                    }
                }
            }
        }
    }
}
