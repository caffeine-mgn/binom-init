package pw.binom.init

import pw.binom.io.*
import pw.binom.io.file.*

class MultiProject(override val config: GlobalConfig, val projects: List<Project>) : AbstractProject() {
    override fun generate(rootDirectory: File) {
        rootDirectory.relative("settings.gradle.kts").openWrite().bufferedWriter().use { output ->
            baseSettingsPart(output)
            projects.forEach { project ->
                output.appendLine("include(\":${project.name}\")")
            }
        }
        projects.forEach { project ->
            val projectDirectory = rootDirectory.relative(project.name)
            projectDirectory.mkdirs()
            project.generateSources(projectDirectory)
            projectDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
                project.generateBuildKts(output = output, globalConfig = null)
            }
        }

        rootDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
            output.write {
                "allprojects" {
                    "repositories" {
                        if (config.useLocalRepository) {
                            +"mavenLocal()"
                        }
                        +"mavenCentral()"
                        if (config.useBinomRepository) {
                            +"maven(url = \"https://repo.binom.pw\")"
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
