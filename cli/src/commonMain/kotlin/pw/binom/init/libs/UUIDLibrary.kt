package pw.binom.init.libs

import pw.binom.init.*

object UUIDLibrary {
    private const val group = "pw.binom:uuid"
    private val version = Version("KUUID", "0.0.6")
    val uuid = Library.Define(
        group = group,
        version = version,
        artifact = "uuid",
        repository = Repository.BINOM_REPOSITORY,
        plugins = emptyList(),
        dependencies = emptyList(),
    )

    val uuidSerialization = Library.Define(
        group = group,
        version = version,
        artifact = "uuid-serialization",
        repository = Repository.BINOM_REPOSITORY,
        plugins = listOf(kotlinSerializationPlugin),
        dependencies = emptyList(),
    )
}