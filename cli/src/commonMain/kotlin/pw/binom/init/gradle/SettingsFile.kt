package pw.binom.init.gradle

import pw.binom.init.MainProject
import pw.binom.init.Project2
import pw.binom.init.Writer
import pw.binom.init.write
import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.use

object SettingsFile {
    fun generate(file: File, name: String, project: Project2) {
        val allPluginRepositories = project.allPluginRepositories
        file.openWrite().bufferedWriter().use { writer ->
            writer.write {
                if (allPluginRepositories.isNotEmpty()) {
                    "pluginManagement" {
                        "repositories" {
                            allPluginRepositories.forEach {
                                it.write(this)
                            }
                        }
                    }
                }
                +"rootProject.name = \"$name\""
                if (project is MainProject) {
                    project.projects.keys.forEach {
                        +"include(\"${it.gradlePath}\")"
                    }
                }
            }
        }
    }
}