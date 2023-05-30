package pw.binom.init

import pw.binom.io.*
import pw.binom.io.file.*

class MultiProject(
    override val config: GlobalConfig,
    override val projects: List<Project>,
    override val kotlinVersion: Version,
) : AbstractProject() {
    override fun getAllLibs() = projects.flatMap { it.libs }.distinct()
    override fun getAllRepositories() =
        (projects.flatMap { it.libs }.map { it.repository } + config.repositories).distinct()

    override fun generate(rootDirectory: File) {
        super.generate(rootDirectory)
        rootDirectory.relative("settings.gradle.kts").openWrite().bufferedWriter().use { output ->
            baseSettingsPart(output)
            projects.forEach { project ->
                output.appendLine("include(\":${project.name}\")")
            }
        }
        projects.forEach { project ->
            project.generate(projectDirectory = rootDirectory.relative(project.name), globalConfig = null)
        }

        rootDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
            output.write {
                "plugins" {
                    pluginsInSection.forEach {
                        if ((it as? Plugin.KotlinPlugin)?.name == "multiplatform") {
                            return@forEach
                        }
                        it.write(
                            output = this,
                            withVersion = true,
                            apply = false,
                        )
                    }
                }
                if (pluginsInClasspath.isNotEmpty()) {
                    "buildscript" {
                        "repositories" {
                            pluginsInClasspath.forEach {
                                it.repository?.write(this)
                            }
                        }
                        "dependencies" {
                            pluginsInClasspath.forEach {
                                it.writeClasspath(this)
                            }
                        }
                    }
                }
                "allprojects" {
                    "repositories" {
                        getAllRepositories().forEach {
                            it.write(this)
                        }
                    }
                }
            }
//            output.appendLine("allprojects {")
//                .appendLine("${tab}repositories {")
//            if (config.useLocalRepository) {
//                output.appendLine("${tab}${tab}mavenLocal()")
//            }
//            output.appendLine("${tab}${tab}mavenCentral()")
//            if (config.useBinomRepository) {
//                output.appendLine("${tab}${tab}maven(url = \"https://repo.binom.pw\")")
//            }
//            output.appendLine("$tab}")
//                .appendLine("}")
        }
    }
}
