package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.file.relative
import pw.binom.io.use

class SingleProject(override val config: GlobalConfig, val project: Project) : AbstractProject() {
    override fun generate(rootDirectory: File) {
        project.generateSources(rootDirectory)
        rootDirectory.relative("settings.gradle.kts").openWrite().bufferedWriter().use { output ->
            baseSettingsPart(output)
        }
        rootDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
            project.generateBuildKts(
                output = output,
                globalConfig = config,
            )
        }
    }
}
