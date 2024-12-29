package pw.binom.init

sealed interface Plugin {
    companion object {
//        val KOTLIN_MULTIPLATFORM =
    }

    val repository: Repository?

    sealed interface PluginSection : Plugin {
        val version: Version?
        fun write(output: Writer, withVersion: Boolean, apply: Boolean)
    }

    object MavenPublication : PluginSection {
        override val version: Version?
            get() = null

        override fun write(output: Writer, withVersion: Boolean, apply: Boolean) {
            output.apply {
                if (apply) {
                    +"id(\"maven-publish\")"
                } else {
                    +"id(\"maven-publish\") apply false"
                }
            }
        }

        override val repository: Repository?
            get() = null
    }

    class IdPlugin(
        val id: String,
        override val version: Version,
        override val repository: Repository = Repository.GRADLE_PLUGIN_PORTAL,
    ) : PluginSection {
        override fun write(output: Writer, withVersion: Boolean, apply: Boolean) {
            val sb = StringBuilder()
            sb.append("id(\"").append(id).append("\")")
            if (withVersion) {
                sb.append(" version \"").append(version.version).append("\"")
            }
            if (!apply) {
                sb.append(" apply false")
            }
            output.apply {
                +sb.toString()
            }
        }
    }

    class KotlinPlugin(
        val name: String,
        override val version: Version,
        override val repository: Repository = Repository.MAVEN_CENTRAL,
        val embedded: Boolean,
    ) : PluginSection {
        override fun write(output: Writer, withVersion: Boolean, apply: Boolean) {
            val sb = StringBuilder()
            sb.append("kotlin(\"").append(name).append("\")")
            if (withVersion && !embedded) {
                sb.append(" version \"").append(version.version).append("\"")
            }
            if (!apply) {
                sb.append(" apply false")
            }
            output.apply {
                +sb.toString()
            }
        }
    }

    sealed interface Classpath : Plugin {
        fun writeClasspath(output: Writer)
        fun writeApply(output: Writer)
    }
}
