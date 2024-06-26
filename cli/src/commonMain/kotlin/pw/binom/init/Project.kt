package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.File
import pw.binom.io.file.openWrite
import pw.binom.io.use

class Project(
    val name: String,
    val packageName: String,
    val kind: Kind,
    val targets: Set<Target>,
    val libs: Collection<Library>,
    val plugins: Collection<Plugin>,
) {
    val allPlugins
        get() = (libs.flatMap { it.plugins } + plugins).distinct()

    val versions
        get() = libs.filterIsInstance<Library.Define>()
            .map { it.version }
            .distinct()

    val pluginRepositories
        get() = plugins.mapNotNull { it.repository }.distinct()

    fun generate(projectDirectory: File, globalConfig: GlobalConfig?) {
        projectDirectory.mkdirs()
        generateSources(projectDirectory)
        projectDirectory.relative("build.gradle.kts").openWrite().bufferedWriter().use { output ->
            generateBuildKts(output = output, globalConfig = globalConfig)
        }
    }

    private val tab = "    "
    fun generateSources(projectDirectory: File) {
        val packageDir = packageName.replace('.', '/')
        val baseSourcePath = projectDirectory.relative("src/commonMain/kotlin").relative(packageDir)
        baseSourcePath.mkdirs()
        if (kind == Kind.APPLICATION) {
            baseSourcePath.relative("Main.kt").openWrite().bufferedWriter().use { output ->
                output.write {
                    if (packageName.isNotEmpty()) {
                        output.append("package ").appendLine(packageName).appendLine()
                    }
                    "fun main(args: Array<String>)" {
                        +"// Your code here"
                    }
                }
//                output.appendLine("fun main(args: Array<String>) {")
//                    .appendLine("${tab}// Your code here")
//                    .appendLine("}")
            }
            if (Target.JVM in targets) {
                val jvmSourcePath = projectDirectory.relative("src/jvmMain/kotlin").relative(packageDir)
                jvmSourcePath.mkdirs()
                jvmSourcePath.relative("Main.kt").openWrite().bufferedWriter().use { output ->
                    if (packageName.isNotEmpty()) {
                        output.append("package ").appendLine(packageName).appendLine()
                    }
                    output.write {
                        "object MainJvm" {
                            +"@JvmStatic"
                            +"@JvmName(\"main\")"
                            "fun mainJvm(args: Array<String>)" {
                                +"main(args)"
                            }
                        }
                    }
//                    output.appendLine("object MainJvm {")
//                        .appendLine("$tab@JvmStatic")
//                        .appendLine("$tab@JvmName(\"main\")")
//                        .appendLine("${tab}fun mainJvm(args: Array<String>) {")
//                    output.appendLine("${tab}${tab}main(args)")
//                    output.appendLine("$tab}")
                }
            }
        }
    }

    private fun getAllRepositories() =
        libs.map { it.repository }.distinct()

    fun generateBuildKts(output: Appendable, globalConfig: GlobalConfig?) {
        output.write {
            "plugins" {
                plugins.mapNotNull { it as? Plugin.PluginSection }.forEach {
                    it.write(
                        output = this,
                        withVersion = globalConfig != null,
                        apply = true,
                    )
                }
//                if (globalConfig==null){
//
//                }
//                if (globalConfig == null) {
//                    +"kotlin(\"multiplatform\")"
//                } else {
//                    +"kotlin(\"multiplatform\") version \"1.8.21\""
//                }
//                if (kind == Kind.APPLICATION && Targets.JVM in targets) {
//                    +"id(\"com.github.johnrengelman.shadow\") version \"5.2.0\""
//                }
            }
            if (kind == Kind.APPLICATION) {
                val entryPoint = if (packageName.isEmpty()) {
                    "main"
                } else {
                    "$packageName.main"
                }
                +"val nativeEntryPoint = \"$entryPoint\""
            }

            "kotlin" {
                targets.forEach {
                    fun native(name: String) = if (kind == Kind.LIBRARY) {
                        +"$name()"
                    } else {
                        name {
                            "binaries" {
                                "executable" {
                                    +"entryPoint = nativeEntryPoint"
                                }
                            }
                        }
                    }
                    when (it) {
                        Target.JS -> {
                            when (kind) {
                                Kind.APPLICATION -> {
                                    "js(IR)" {
                                        +"browser()"
                                        +"binaries.executable()"
                                    }
                                }

                                Kind.LIBRARY -> +"js(IR)"
                            }
                        }

                        Target.JVM -> if (kind == Kind.APPLICATION) {
                            val mainClass = if (packageName.isEmpty()) {
                                "JvmMain"
                            } else {
                                "$packageName.JvmMain"
                            }
                            "jvm" {
                                "compilations.all" {
                                    +"mainRun { mainClass=\"$mainClass\" }"
                                }
                            }
                        } else {
                            +"jvm()"
                        }

                        else -> native(it.target)
                    }
                }

                "sourceSets" {
                    "val commonMain by getting" {
                        "dependencies" {
                            +"api(kotlin(\"stdlib\"))"
                            libs.forEach { lib ->
                                when (lib) {
                                    is Library.Kotlin -> lib.write(this)
                                    is Library.Define -> lib.write(this, versionInline = false)
                                }
                            }
                        }
                    }
                    "val commonTest by getting" {
                        "dependencies" {
                            +"implementation(kotlin(\"test-common\"))"
                            +"implementation(kotlin(\"test-annotations-common\"))"
                        }
                    }
                }
            }
            if (kind == Kind.APPLICATION && Target.JVM in targets) {
                val mainClass = if (packageName.isEmpty()) {
                    "JvmMain"
                } else {
                    "$packageName.JvmMain"
                }
                "tasks" {
                    +"val jvmJar by getting(Jar::class)"
                    "val shadowJar by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class)" {
                        +"from(jvmJar.archiveFile)"
                        +"group = \"build\""
                        +"configurations = listOf(project.configurations[\"jvmRuntimeClasspath\"])"
                        "manifest" {
                            +"attributes(\"Main-Class\" to \"$mainClass\")"
                        }
                    }
                }
            }
            if (globalConfig != null) {
                "repositories" {
                    val repos = (globalConfig.repositories + getAllRepositories()).distinct()
                    repos.forEach {
                        it.write(this)
                    }
                }
            }
        }
    }
}
