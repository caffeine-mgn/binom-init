package pw.binom.init.libs

import pw.binom.init.Plugin
import pw.binom.init.Repository
import pw.binom.init.Version

object Kotlin {
    private val kotlinVersion = Version("kotlin", "2.1.0")
    val kotlinMultiplatformPlugin = Plugin.KotlinPlugin(
        name = "multiplatform",
        version = kotlinVersion,
        embedded = false,
        repository = Repository.MAVEN_CENTRAL,
    )
    val kotlinSerializationPlugin = Plugin.IdPlugin(
        id = "kotlinx-serialization",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
    )
    val kotlinComposePlugin = Plugin.KotlinPlugin(
        name = "plugin.compose",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false,
    )
    val kotlinSpringPlugin = Plugin.KotlinPlugin(
        name = "plugin.spring",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false,
    )
    val kotlinAllOpenPlugin = Plugin.KotlinPlugin(
        name = "plugin.allopen",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false,
    )
    val kotlinNoArgPlugin = Plugin.KotlinPlugin(
        name = "plugin.noarg",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false,
    )
    val kotlinLombokPlugin = Plugin.KotlinPlugin(
        name = "plugin.lombok",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false,
    )
}