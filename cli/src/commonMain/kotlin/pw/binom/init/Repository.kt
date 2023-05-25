package pw.binom.init

import pw.binom.url.URL

sealed interface Repository {
    fun write(output: Writer)
    data class UrlRepository(val url: URL) : Repository {
        override fun write(output: Writer) {
            output.apply {
                +"maven(url = \"$url\")"
            }
        }
    }

    data class StandardRepository(val name: String) : Repository {
        override fun write(output: Writer) {
            output.apply {
                +"$name()"
            }
        }
    }
}
