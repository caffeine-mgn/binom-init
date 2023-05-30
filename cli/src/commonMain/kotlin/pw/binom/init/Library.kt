package pw.binom.init

data class Library(
    val group: String,
    val artifact: String,
    val version: Version,
    val repository: Repository,
    val plugins: List<Plugin>,
)
