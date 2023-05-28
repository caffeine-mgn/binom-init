package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.*
import pw.binom.io.use

class Project(
        val name: String,
        val packageName: String,
        val kind: Kind,
        val targets: Set<Targets>,
        val libs: Collection<Library>,
) {

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
            if (Targets.JVM in targets) {
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

    fun generateBuildKts(output: Appendable, globalConfig: GlobalConfig?) {
        output.write {
            "plugins" {
                if (globalConfig == null) {
                    +"kotlin(\"multiplatform\")"
                } else {
                    +"kotlin(\"multiplatform\") version \"1.8.21\""
                }
                if (kind == Kind.APPLICATION && Targets.JVM in targets) {
                    +"id(\"com.github.johnrengelman.shadow\") version \"5.2.0\""
                }
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
                        Targets.JS -> {
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

                        Targets.JVM -> "jvm" {
                            +"jvmToolchain(pw.binom.Versions.JVM_VERSION)"
                        }

                        Targets.LINUX_X64 -> native("linuxX64")
                        Targets.MINGW_X64 -> native("mingwX64")

                        else -> TODO()
                    }
                }

                "sourceSets" {
                    "val commonMain by getting" {
                        "dependencies" {
                            +"api(kotlin(\"stdlib\"))"
                            libs.forEach {
                                +"api(\"${it.group}:${it.artifact}:${it.version}\")"
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
            if (kind == Kind.APPLICATION && Targets.JVM in targets) {
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
        }
    }
}
