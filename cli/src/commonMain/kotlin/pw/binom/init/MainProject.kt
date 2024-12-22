package pw.binom.init

import pw.binom.init.gradle.ProjectBuildFile
import pw.binom.init.gradle.RootBuildFile
import pw.binom.init.gradle.SettingsFile
import pw.binom.io.file.File


class MainProject(
    val projects: Map<ProjectName, KotlinProject>,
) : Project2 {
    override val allPlugins = projects.values
        .asSequence()
        .map { it.allPlugins }
        .flatten()
        .toSet()

    override val allPluginRepositories = allPlugins
        .mapNotNull { it.repository }
        .toSet()

    override val allDependencyRepositories = projects.values
        .asSequence()
        .map { it.allDependencyRepositories }
        .flatten()
        .toSet()

    override fun generate(name: String, file: File) {
        SettingsFile.generate(
            file = file.relative("settings.gradle.kts"),
            name = name,
            project = this,
        )
        RootBuildFile.generate(
            file = file.relative("build.gradle.kts"),
            project = this,
        )
        projects.forEach { (name, project) ->
            val projectDir = name.relative(file)
            projectDir.mkdirs()
            ProjectBuildFile.generate(
                file = projectDir.relative("build.gradle.kts"),
                project = project,
                isRoot = false,
            )
        }
    }
}
