package pw.binom

import org.gradle.api.Plugin
import org.gradle.buildinit.plugins.internal.BuildInitializer
import org.gradle.buildinit.plugins.internal.InitSettings
import org.gradle.buildinit.plugins.internal.ProjectLayoutSetupRegistry
import org.gradle.buildinit.plugins.internal.modifiers.*
import org.gradle.configurationcache.extensions.serviceOf
import org.gradle.invocation.DefaultGradle
import java.io.File
import java.util.*

class MyBuildInitializer : BuildInitializer {
    override fun generate(settings: InitSettings) {
        val workDirectory = File(System.getProperty("user.dir"))
        workDirectory.resolve("settings.kts").writeText(
            """pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://repo.binom.pw")
        gradlePluginPortal()
    }
}
rootProject.name = "${settings.packageName}"
            """.trimIndent(),
        )
        File("build.gradle.kts").writeText(
            """
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}
repositories {
    mavenCentral()
    maven(url = "https://repo.binom.pw")
}

val nativeEntryPoint = "${settings.packageName}.main"

fun KotlinNativeTarget.configNative() {
    binaries {
        executable {
            entryPoint = nativeEntryPoint
        }
    }
}

kotlin {
    linuxX64 {
        configNative()
    }
    linuxArm64 {
        configNative()
    }
    mingwX64 {
        configNative()
    }
    macosX64 {
        configNative()
    }
    macosArm64 {
        configNative()
    }
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

tasks {
    val jvmJar by getting(Jar::class)

    val shadowJar by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        from(jvmJar.archiveFile)
        group = "build"
        configurations = listOf(project.configurations["jvmRuntimeClasspath"])
        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
            "META-INF/*.txt",
            "META-INF/NOTICE",
            "LICENSE",
        )
        manifest {
            attributes("Main-Class" to "${settings.packageName}.JvmMain")
        }
    }
}
            """.trimIndent(),
        )
        val mainCommonFile = workDirectory.resolve("src/commonMain/kotlin/${settings.packageName.replace('.', '/')}/Main.kt")
        val mainJvmFile = workDirectory.resolve("src/jvmMain/kotlin/${settings.packageName.replace('.', '/')}/Main.kt")
        mainCommonFile.parentFile.mkdirs()
        mainCommonFile.writeText(
            """
            package ${settings.packageName}
            
            fun main(args: Array<String>) {
                // Do nothing
            }
            """.trimIndent(),
        )
        mainJvmFile.parentFile.mkdirs()
        mainJvmFile.writeText(
            """
            package ${settings.packageName}
                
            object Main {
                @JvmStatic
                fun main(args: Array<String>) {
                    ${settings.packageName}.main(args)
                }
            }
            """.trimIndent(),
        )
    }

    override fun getId(): String = "binom-native-application"

    override fun getComponentType(): ComponentType = ComponentType.APPLICATION

    override fun getLanguage(): Language = Language.KOTLIN

    override fun supportsJavaTargets(): Boolean = true

    override fun getModularizationOptions(): MutableSet<ModularizationOption> =
        mutableSetOf(ModularizationOption.SINGLE_PROJECT, ModularizationOption.WITH_LIBRARY_PROJECTS)

    override fun getDsls(): MutableSet<BuildInitDsl> = mutableSetOf(BuildInitDsl.KOTLIN, BuildInitDsl.GROOVY)

    override fun getDefaultDsl(): BuildInitDsl = BuildInitDsl.KOTLIN

    override fun supportsProjectName(): Boolean = true

    override fun supportsPackage(): Boolean = true

    override fun getTestFrameworks(): MutableSet<BuildInitTestFramework> =
        mutableSetOf(BuildInitTestFramework.KOTLINTEST)

    override fun getDefaultTestFramework(): BuildInitTestFramework = BuildInitTestFramework.KOTLINTEST

    override fun getFurtherReading(settings: InitSettings?): Optional<String> {
        return Optional.empty()
    }
}

class BinomInitPlugin : Plugin<DefaultGradle> {
    override fun apply(project: DefaultGradle) {
        println("Apply kotlin init plugin!---1")
        project.rootProject {
            it.tasks.create("ololo") {
                it.doLast {
                    println("ololo!")
                }
            }
            val projectLayoutSetupRegistry = it.serviceOf<ProjectLayoutSetupRegistry>()
            projectLayoutSetupRegistry.add(MyBuildInitializer())
        }
    }
}
