package pw.binom.init

sealed interface Library {
    val repository: Repository
    val plugins: List<Plugin>

    data class Kotlin(
        val name: String,
        override val repository: Repository,
        override val plugins: List<Plugin>
    ) : Library {
        fun write(writer: Writer) {
            writer {
                "api(kotlin(\"$name\"))"
            }
        }
    }

    data class Define(
        val group: String,
        val artifact: String,
        val version: Version,
        override val repository: Repository,
        override val plugins: List<Plugin>,
        val dependencies: List<Library>
    ) : Library {
        fun write(writer: Writer, versionInline: Boolean) {
            writer {
                val ver = if (versionInline) version.version else version.variableName
                +"api(\"${group}:${artifact}:\$$ver\")"
            }
        }
    }
}
