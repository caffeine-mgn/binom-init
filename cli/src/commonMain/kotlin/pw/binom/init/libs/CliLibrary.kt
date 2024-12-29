package pw.binom.init.libs

import pw.binom.init.Library
import pw.binom.init.Repository
import pw.binom.init.Version

object CliLibrary {
    val mordantVersion=Version("mordant","3.0.1")
    val mosaicVersion=Version("mosaic","0.14.0")
    val libs = listOf(
        Library.Define(
            group = "com.github.ajalt.mordant",
            artifact = "mordant",
            version = mordantVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(),
            description = "TUI",
        ),
        Library.Define(
            group = "com.jakewharton.mosaic",
            artifact = "mosaic-runtime",
            version = mosaicVersion,
            repository = Repository.MAVEN_CENTRAL,
            plugins = listOf(Kotlin.kotlinComposePlugin),
            description = "TUI Compose",
        ),
    )
}