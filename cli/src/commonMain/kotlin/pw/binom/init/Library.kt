package pw.binom.init

data class Library(
    val group: String,
    val artifact: String,
    val version: String,
    val repository: Repository,
)
