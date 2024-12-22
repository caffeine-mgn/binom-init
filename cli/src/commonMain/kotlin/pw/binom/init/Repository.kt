package pw.binom.init

import pw.binom.url.URL
import pw.binom.url.toURL

sealed interface Repository {
    companion object {
        val MAVEN_LOCAL = StandardRepository("mavenLocal")
        val MAVEN_CENTRAL = StandardRepository("mavenCentral")
        val GRADLE_PLUGIN_PORTAL = StandardRepository("gradlePluginPortal")
        val GOOGLE = StandardRepository("google")
        val BINOM_REPOSITORY = UrlRepository("https://repo.binom.pw".toURL())
    }

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
