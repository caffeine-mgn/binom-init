import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.Family
import pw.binom.ResourcePackerTask

plugins {
    kotlin("multiplatform")
//    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    kotlin("plugin.compose") version "2.1.0"
//    id("com.github.johnrengelman.shadow")
}

val binomIoVersion = project.property("binom.io.version")

kotlin {
    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()
    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib"))
            api("pw.binom.io:file:$binomIoVersion")
            api("pw.binom.io:httpClient:$binomIoVersion")
            api("pw.binom.io:console:$binomIoVersion")
            implementation("com.jakewharton.mosaic:mosaic-runtime:0.14.0")
        }
    }
    targets.withType(KotlinNativeTarget::class.java).configureEach {
        binaries.executable {
            entryPoint = "pw.binom.init.main"
            if (buildType == NativeBuildType.DEBUG) {
                linkTaskProvider.configure {
//                    enabled = false
                }
            }
        }
    }
}

tasks {
    withType(KotlinNativeLink::class.java).onEach { target ->
        if (target.processTests) {
            return@onEach
        }
        val prefix = if (target.debuggable) {
            "Debug"
        } else {
            "Release"
        }
        val appendResourceTask =
            register("appendResources-$prefix-${target.binary.target.konanTarget.name}", ResourcePackerTask::class.java)
        appendResourceTask.configure {
            dependsOn(target)
            resource(
                file = rootProject.file("gradlew"),
                name = "gradlew",
            )
            resource(
                file = rootProject.file("gradlew.bat"),
                name = "gradlew.bat",
            )
            resource(
                file = rootProject.file("gradle/wrapper/gradle-wrapper.jar"),
                name = "gradle-wrapper.jar",
            )
            inputBinaryFile.set(target.binary.outputFile)
            val name = "full-${target.binary.outputFile.name}"
            outputBinaryFile.set(target.binary.outputFile.parentFile.resolve(name))
            gradleWrapperJar.set(rootProject.rootDir.resolve("gradle/wrapper/gradle-wrapper.jar"))
        }

        val installTask = register("install$prefix-${target.binary.target.konanTarget.name}", Copy::class)

        installTask.configure {
            group = "install"
            dependsOn(appendResourceTask)
            from(appendResourceTask.get().outputBinaryFile)
            into("${System.getProperty("user.home")}/.bin")
            val suffix = if (target.binary.target.konanTarget.family == Family.MINGW) {
                ".exe"
            } else {
                ""
            }
            rename { "binom-init$suffix" }
            val outFile = File("${System.getProperty("user.home")}/.bin/binom-init$suffix")
            doLast {
                outFile.setExecutable(true)
            }
        }

    }

    val linkReleaseExecutableLinuxX64 by getting
    val linkReleaseExecutableMingwX64 by getting

    val isLinux = System.getProperty("os.name").lowercase().let { "nix" in it || "linux" in it }
    val isWindows = "windows" in System.getProperty("os.name").lowercase()

    val installLinux by creating(Copy::class) {
        this.onlyIf { isLinux }
        dependsOn(linkReleaseExecutableLinuxX64)
        this.from("build/bin/linuxX64/releaseExecutable/cli.kexe")
        into("${System.getProperty("user.home")}/.bin")
        rename { "binom-init" }
    }

    val binPath = if (hasProperty("bin-path")) property("bin-path") as String? else null

    val installWindows by creating(Copy::class) {
        this.onlyIf { isWindows }
        dependsOn(linkReleaseExecutableMingwX64)
        this.from("build/bin/mingwX64/releaseExecutable/cli.exe")
        val binDir = binPath?.let { File(it) } ?: File(System.getProperty("user.home")).resolve("bin")
        into(binDir)
        rename { "binom-init.exe" }
    }

    val install by creating {
        dependsOn(installLinux)
        dependsOn(installWindows)
    }
}
