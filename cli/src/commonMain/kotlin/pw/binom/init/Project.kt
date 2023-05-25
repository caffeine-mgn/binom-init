package pw.binom.init

import pw.binom.io.bufferedWriter
import pw.binom.io.file.*
import pw.binom.io.use

class Project(
    val name: String,
    val packageName: String,
    val kind: Kind,
    val targets: Set<Targets>,
    val libs: List<Library>,
) {
    private val tab = "    "
    fun generateSources(projectDirectory: File) {
        val packageDir = packageName.replace('.', '/')
        val baseSourcePath = projectDirectory.relative("src/commonMain/kotlin").relative(packageDir)
        baseSourcePath.mkdirs()
        if (kind == Kind.APPLICATION) {
            baseSourcePath.relative("Main.kt").openWrite().bufferedWriter().use { output ->
                output.write {
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
                        output.append("package ").appendLine(packageName)
                    }
                    output.write {
                        "object MainJvm" {
                            +"@JvmStatic"
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
        output.appendLine("plugins {")
            .appendLine("${tab}kotlin(\"multiplatform\") version \"1.8.21\"")
        if (kind == Kind.APPLICATION && Targets.JVM in targets) {
            output.appendLine("${tab}id(\"com.github.johnrengelman.shadow\") version \"5.2.0\"")
        }
        output.appendLine("}")
        if (kind == Kind.APPLICATION) {
            val entryPoint = if (packageName.isEmpty()) {
                "main"
            } else {
                "$packageName.main"
            }
            output.appendLine("val nativeEntryPoint = \"$entryPoint\"")
        }
        output.appendLine("kotlin {")
        targets.forEach {
            fun native() = if (kind == Kind.LIBRARY) {
                "()"
            } else {
                " {\n" +
                    "${tab}${tab}binaries {\n" +
                    "${tab}${tab}${tab}executable {\n" +
                    "${tab}${tab}${tab}${tab}entryPoint = nativeEntryPoint\n" +
                    "${tab}${tab}$tab}\n" +
                    "${tab}$tab}\n" +
                    "$tab}"
            }
            when (it) {
                Targets.JS -> {
                    output.append("${tab}js(IR)")
                    when (kind) {
                        Kind.APPLICATION -> {
                            output.appendLine(" {")
                                .appendLine("${tab}${tab}browser()")
                                .appendLine("${tab}${tab}binaries.executable()")
                                .appendLine("$tab}")
                        }

                        Kind.LIBRARY -> output.appendLine()
                    }
                }

                Targets.JVM -> output.appendLine("${tab}jvm()")
                Targets.LINUX_X64 -> output.append("${tab}linuxX64").appendLine(native())
                Targets.MINGW_X64 -> output.append("${tab}mingwX64").appendLine(native())

                else -> TODO()
            }
        }
        output.appendLine("${tab}sourceSets {")
            .appendLine("${tab}${tab}val commonMain by getting {")
            .appendLine("${tab}${tab}${tab}dependencies {")
            .appendLine("${tab}${tab}${tab}${tab}api(kotlin(\"stdlib\"))")
        libs.forEach {
            output.appendLine("${tab}${tab}${tab}${tab}api(\"${it.group}:${it.artifact}:${it.version}\")")
        }
        output.appendLine("${tab}${tab}$tab}")
            .appendLine("${tab}$tab}")
            .appendLine("${tab}${tab}val commonTest by getting {")
            .appendLine("${tab}${tab}${tab}dependencies {")
            .appendLine("${tab}${tab}${tab}${tab}implementation(kotlin(\"test-common\"))")
            .appendLine("${tab}${tab}${tab}${tab}implementation(kotlin(\"test-annotations-common\"))")
            .appendLine("${tab}${tab}$tab}")
            .appendLine("${tab}$tab}")

        output.appendLine("$tab}")
        output.appendLine("}")
        if (kind == Kind.APPLICATION && Targets.JVM in targets) {
            val mainClass = if (packageName.isEmpty()) {
                "JvmMain"
            } else {
                "$packageName.JvmMain"
            }
            output.appendLine("tasks {")
                .appendLine("${tab}val jvmJar by getting(Jar::class)")
                .appendLine("${tab}val shadowJar by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {")
                .appendLine("${tab}${tab}from(jvmJar.archiveFile)")
                .appendLine("${tab}${tab}group = \"build\"")
                .appendLine("${tab}${tab}configurations = listOf(project.configurations[\"jvmRuntimeClasspath\"])")
                .appendLine("${tab}${tab}manifest {")
                .appendLine("${tab}${tab}${tab}attributes(\"Main-Class\" to \"$mainClass\")")
                .appendLine("${tab}$tab}")
                .appendLine("$tab}")
                .appendLine("}")
        }
    }
}
