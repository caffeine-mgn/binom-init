package pw.binom.init

import pw.binom.init.gradle.ProjectBuildFile
import pw.binom.init.gradle.SettingsFile
import pw.binom.io.file.File

class KotlinProject(
    val kind: Kind,
    val libs: Set<Library>,
    val plugins: List<Plugin>,
    val targets: Set<Target>,
) : Project2 {
    override val allPlugins = (libs
        .asSequence()
        .map { it.plugins }
        .flatten() + plugins.asSequence())
        .toSet()

    override val allPluginRepositories = allPlugins
        .mapNotNull { it.repository }
        .toSet()

    override val allDependencyRepositories = libs
        .asSequence()
        .map { it.repository }
        .toSet()

    override fun generate(name: String, file: File) {
        SettingsFile.generate(
            file = file.relative("settings.gradle.kts"),
            name = name,
            project = this,
        )
        ProjectBuildFile.generate(
            file = file.relative("build.gradle.kts"),
            project = this,
            isRoot = true,
        )
        file.relative("src/commonMain/kotlin").mkdirs()
        file.relative("src/commonTest/kotlin").mkdirs()
    }
}