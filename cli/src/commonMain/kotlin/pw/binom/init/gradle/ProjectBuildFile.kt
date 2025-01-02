package pw.binom.init.gradle

import pw.binom.init.*
import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.use

object ProjectBuildFile {
    fun generate(file: File, project: KotlinProject, isRoot: Boolean) {
        val allSectionPlugins = project.allPlugins.filterIsInstance<Plugin.PluginSection>()
        val libs = project.libs
        val allDependencyRepositories = project.allDependencyRepositories
        file.openWrite().bufferedWriter().use { writer ->
            writer.write {
                if (allSectionPlugins.isNotEmpty()) {
                    "plugins" {
                        allSectionPlugins.forEach {
                            it.write(output = this, withVersion = isRoot, apply = true)
                        }
                    }
                }
                "kotlin" {
                    project.targets.forEach {
                        +"${it.target}()"
                    }
                    if (libs.isNotEmpty()) {
                        "sourceSets" {
                            val main = libs.filter { it.runtime == Library.Runtime.MAIN }
                            val test = libs.filter { it.runtime == Library.Runtime.TEST }
                            if (main.isNotEmpty()) {
                                "commonMain.dependencies" {
                                    main.forEach {
                                        when (it) {
                                            is Library.Kotlin -> it.write(writer = this)
                                            is Library.Define -> it.write(writer = this, versionInline = true)
                                        }
                                    }
                                }
                            }

                            if (test.isNotEmpty()) {
                                "commonTest.dependencies" {
                                    test.forEach {
                                        when (it) {
                                            is Library.Kotlin -> it.write(writer = this)
                                            is Library.Define -> it.write(writer = this, versionInline = true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (isRoot) {
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