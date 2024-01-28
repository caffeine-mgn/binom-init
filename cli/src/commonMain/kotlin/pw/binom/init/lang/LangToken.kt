package pw.binom.init.lang

import kotlin.jvm.JvmInline

@JvmInline
value class LangToken(val rawToken: String) {
    companion object {
        fun create(token: String) = LangToken(token)
    }
}
