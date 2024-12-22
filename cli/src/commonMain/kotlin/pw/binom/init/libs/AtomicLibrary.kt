package pw.binom.init.libs

import pw.binom.init.Library
import pw.binom.init.Repository
import pw.binom.init.Version

object AtomicLibrary {
    private const val group = "pw.binom"
    private val version = Version("ATOMIC", "0.0.4")
    val atomic = Library.Define(
        group = group,
        version = version,
        artifact = "atomic",
        repository = Repository.BINOM_REPOSITORY,
    )
}