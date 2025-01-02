package pw.binom.init.libs

import pw.binom.init.*

object Kotlin {
    private val kotlinVersion = Version("kotlin", "2.1.0")
    val kotlinMultiplatformPlugin = Plugin.KotlinPlugin(
        name = "multiplatform",
        version = kotlinVersion,
        embedded = false,
        repository = Repository.MAVEN_CENTRAL,
    )
    val kotlinSerializationPlugin = Plugin.KotlinPlugin(
        name = "plugin.serialization",
        version = kotlinVersion,
        repository = Repository.MAVEN_CENTRAL,
        embedded = false
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
    val stdLib = Library.Kotlin(
        name = "stdlib",
        repository = Repository.MAVEN_CENTRAL,
        plugins = listOf(kotlinMultiplatformPlugin),
        runtime = Library.Runtime.MAIN,
    )
    val serializationVersion = Version("kotlin_serialization", "1.7.3")
    val kamlSerializationVersion = Version("kaml_serialization", "0.66.0")
    val coroutinesVersion = Version("kotlin_coroutines", "1.10.1")
    val libs = listOf(
        Library.Define(
            group = "org.jetbrains.kotlinx",
            artifact = "kotlinx-serialization-json",
            version = serializationVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(kotlinSerializationPlugin),
            description = "JSON Serialization",
            category = Category.SERIALIZATION,
        ),
        Library.Define(
            group = "com.charleskorn.kaml",
            artifact = "kaml",
            version = kamlSerializationVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(kotlinSerializationPlugin),
            description = "YAML Serialization",
            category = Category.SERIALIZATION,
        ),
        Library.Define(
            group = "org.jetbrains.kotlinx",
            artifact = "kotlinx-serialization-protobuf",
            version = serializationVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(kotlinSerializationPlugin),
            description = "Protobuff Serialization",
            category = Category.SERIALIZATION,
        ),
        Library.Define(
            group = "org.jetbrains.kotlinx",
            artifact = "kotlinx-coroutines-core",
            version = coroutinesVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(),
        ),
    )
}