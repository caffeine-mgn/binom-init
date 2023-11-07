import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
}

val nativeEntryPoint = "pw.binom.init.main"

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
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                api("pw.binom.io:file:1.0.0-SNAPSHOT")
                api("pw.binom.io:console:1.0.0-SNAPSHOT")
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
            attributes("Main-Class" to "pw.binom.init.JvmMain")
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
