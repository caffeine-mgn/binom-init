package pw.binom.init.libs

import pw.binom.init.*

object UUIDLibrary {
    private const val group = "pw.binom:uuid"
    private val version = Version("KUUID", "0.0.6")

    private val internalLibs = ArrayList<Library.Define>()
    val libs: List<Library.Define>
        get() = internalLibs

    val uuid = Library.Define(
        group = group,
        version = version,
        artifact = "uuid",
        repository = Repository.BINOM_REPOSITORY,
        plugins = emptyList(),
        dependencies = emptyList(),
    ).also {
        internalLibs += it
    }

    val uuidSerialization = Library.Define(
        group = group,
        version = version,
        artifact = "uuid-serialization",
        repository = Repository.BINOM_REPOSITORY,
        plugins = listOf(Kotlin.kotlinSerializationPlugin),
        dependencies = emptyList(),
    ).also {
        internalLibs += it
    }
}