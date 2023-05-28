package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.mkdirs
import pw.binom.io.file.openWrite
import pw.binom.io.file.relative
import pw.binom.io.use

abstract class AbstractProject : RootProject {
    protected val tab = "    "
    protected abstract val config: GlobalConfig
    protected abstract fun getAllLibs(): Collection<Library>
    override fun generate(rootDirectory: File) {
        println("Generating buildSrc")
        val buildSrc = rootDirectory.relative("buildSrc")
        val versions = getAllLibs().asSequence().map { it.version }.toSet()
        buildSrc.relative("src/main/kotlin/pw/binom").also {
            it.mkdirs()
            it.relative("Versions.kt").openWrite().bufferedWriter().use {
                it.appendLine("package pw.binom")
                        .appendLine()
                        .appendLine("import BuildConfig")
                        .appendLine()
                it.write {
                    "object Versions" {
                        +"val JVM_VERSION = org.gradle.internal.jvm.Jvm.current().javaVersion!!.ordinal + 1"
                        +"const val KOTLIN_VERSION = BuildConfig.KOTLIN_VERSION"
                        versions.forEach {
                            +"const val ${it.constName} = BuildConfig.${it.constName}"
                        }
                    }
                }
            }
        }
        buildSrc.relative("build.gradle.kts").openWrite().bufferedWriter().use {
            it.write {
                "buildscript" {

                    "repositories" {
                        if (config.useLocalRepository) {
                            +"mavenLocal()"
                        }
                        +"mavenCentral()"
                        +"maven(url = \"https://maven.google.com\")"
                        +"gradlePluginPortal()"
                        if (config.useBinomRepository) {
                            +"maven(url = \"https://repo.binom.pw\")"
                        }
                    }

                    "dependencies" {
                    }
                }
                "plugins" {
                    +"kotlin(\"jvm\") version \"1.8.21\""
                    +"id(\"com.github.gmazzo.buildconfig\") version \"3.0.3\""
                }
                +"val kotlinVersion = kotlin.coreLibrariesVersion"
                versions.forEach {
                    +"val ${it.variableName} = project.property(\"${it.propertyName}\") as String"
                }
                "buildConfig" {
                    +"packageName(project.group.toString())"
                    +"""buildConfigField("String", "KOTLIN_VERSION", "\"${'$'}kotlinVersion\"")"""
                    versions.forEach {
                        +"""buildConfigField("String", "${it.constName}", "\"${'$'}${it.variableName}\"")"""
                    }

                }
                "repositories" {
                    if (config.useLocalRepository) {
                        +"mavenLocal()"
                    }
                    +"mavenCentral()"
                    +"maven(url = \"https://repo.binom.pw\")"
                    +"maven(url = \"https://plugins.gradle.org/m2/\")"
                    +"maven(url = \"https://maven.google.com\")"
                    +"gradlePluginPortal()"
                    if (config.useBinomRepository) {
                        +"maven(url = \"https://repo.binom.pw\")"
                    }
                }
                "dependencies" {
                    +"api(\"org.jetbrains.kotlin:kotlin-stdlib:\$kotlinVersion\")"
                    +"api(\"org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion\")"
                    +"api(\"org.jetbrains.kotlin:kotlin-compiler-embeddable:\$kotlinVersion\")"
                    +"api(\"org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion\")"
                    +"api(\"org.jetbrains.kotlin:kotlin-serialization:\$kotlinVersion\")"
                }
            }
        }
        buildSrc.relative("gradle.properties").openWrite().bufferedWriter().use {
            versions.forEach { version ->
                it.appendLine("${version.propertyName}=${version.version}")
            }
        }
    }

    protected fun baseSettingsPart(out: Appendable) {
        out.write {
            "pluginManagement" {
                "plugins" {
                }
                "repositories" {
                    if (config.useLocalRepository) {
                        +"mavenLocal()"
                    }
                    +"mavenCentral()"
                    +"maven(url = \"https://plugins.gradle.org/m2/\")"
                    +"gradlePluginPortal()"
                    if (config.useBinomRepository) {
                        +"maven(url = \"https://repo.binom.pw\")"
                    }
                }
            }
            +"rootProject.name=\"${config.rootName}\""
        }
    }
}
