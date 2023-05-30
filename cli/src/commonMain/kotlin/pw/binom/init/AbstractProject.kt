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
    protected abstract val kotlinVersion: Version
    protected abstract fun getAllLibs(): Collection<Library>
    protected abstract fun getAllRepositories(): Collection<Repository>
    protected abstract val projects: Collection<Project>
    protected val plugins by lazy {
        (projects.flatMap { it.plugins } + projects.flatMap { it.libs.flatMap { it.plugins } }).toSet()
    }
    protected val pluginsInSection by lazy { plugins.mapNotNull { it as? Plugin.PluginSection } }
    protected val pluginsInClasspath by lazy { plugins.mapNotNull { it as? Plugin.Classpath } }
    private val libs by lazy {
        projects.flatMap { it.libs }.distinct()
    }

    override fun generate(rootDirectory: File) {
        println("Generating buildSrc")
        val buildSrc = rootDirectory.relative("buildSrc")
        val versions =
            (projects.flatMap { it.libs.map { it.version } } + pluginsInSection.map { it.version }).filterNotNull()
                .toSet()
//        val versions = getAllLibs().asSequence().map { it.version }.toSet()
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
//                        if (config.useLocalRepository) {
//                            +"mavenLocal()"
//                        }
//                        +"mavenCentral()"
//                        +"maven(url = \"https://maven.google.com\")"
//                        +"gradlePluginPortal()"
//                        if (config.useBinomRepository) {
//                            +"maven(url = \"https://repo.binom.pw\")"
//                        }
                    }

                    "dependencies" {
                    }
                }
                "plugins" {
                    +"kotlin(\"jvm\") version \"${kotlinVersion.version}\""
                    +"id(\"com.github.gmazzo.buildconfig\") version \"3.0.3\""
                }
                versions.forEach { version ->
                    val value = if (version === kotlinVersion) {
                        "kotlin.coreLibrariesVersion"
                    } else {
                        "project.property(\"${version.propertyName}\") as String"
                    }
                    +"val ${version.variableName} = $value"
                }
                "buildConfig" {
                    +"packageName(project.group.toString())"
                    versions.forEach {
                        +"""buildConfigField("String", "${it.constName}", "\"${'$'}${it.variableName}\"")"""
                    }
                }
                "repositories" {
//                    if (config.useLocalRepository) {
//                        +"mavenLocal()"
//                    }
                    +"mavenCentral()"
//                    +"maven(url = \"https://repo.binom.pw\")"
//                    +"maven(url = \"https://plugins.gradle.org/m2/\")"
//                    +"maven(url = \"https://maven.google.com\")"
//                    +"gradlePluginPortal()"
//                    if (config.useBinomRepository) {
//                        +"maven(url = \"https://repo.binom.pw\")"
//                    }
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
                if (version === kotlinVersion) {
                    return@forEach
                }
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
                    pluginsInSection.forEach {
                        it.repository?.write(this)
                    }
//                    if (config.useLocalRepository) {
//                        +"mavenLocal()"
//                    }
//                    +"mavenCentral()"
//                    +"maven(url = \"https://plugins.gradle.org/m2/\")"
//                    +"gradlePluginPortal()"
//                    if (config.useBinomRepository) {
//                        +"maven(url = \"https://repo.binom.pw\")"
//                    }
                }
            }
            +"rootProject.name=\"${config.rootName}\""
        }
    }
}
