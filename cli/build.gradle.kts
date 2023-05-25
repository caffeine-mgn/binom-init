import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
}

val nativeEntryPoint = "aa.bb.main"

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
                api("pw.binom.io:file:1.0.0-SNAPSHOT")
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

    val install by creating(Copy::class) {
        dependsOn(linkReleaseExecutableLinuxX64)
        this.from("build/bin/linuxX64/debugExecutable/init.kexe")
        into("/home/subochev/.bin")
        rename { "binom-init" }
    }
}