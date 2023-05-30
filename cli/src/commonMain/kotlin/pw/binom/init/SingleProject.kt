package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.file.relative
import pw.binom.io.use

class SingleProject(override val config: GlobalConfig, val project: Project, override val kotlinVersion: Version) : AbstractProject() {
    override fun getAllLibs() = project.libs
    override fun getAllRepositories() = (config.repositories + project.plugins.map { it.repository }).filterNotNull().toSet()

    override val projects: Collection<Project> = listOf(project)
    override fun generate(rootDirectory: File) {
        super.generate(rootDirectory)
        project.generateSources(rootDirectory)
        rootDirectory.relative("settings.gradle.kts").openWrite().bufferedWriter().use { output ->
            baseSettingsPart(output)
        }
        project.generate(projectDirectory = rootDirectory, globalConfig = config)
//        rootDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
//            project.generateBuildKts(
//                output = output,
//                globalConfig = config,
//            )
//        }
    }
}
