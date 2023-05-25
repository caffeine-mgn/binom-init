package pw.binom.init

class CompositLibrary(
    val name: String,
    val library: Library,
    val dependencies: List<Library>,
)
