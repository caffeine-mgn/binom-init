package pw.binom.init

import pw.binom.io.file.File

sealed interface Project2 {
    val allPluginRepositories: Set<Repository>
    val allPlugins: Set<Plugin>
    val allDependencyRepositories: Set<Repository>

    fun generate(name: String, file: File)
}
