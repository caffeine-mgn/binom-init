package pw.binom.init.lang

object LangRu : Lang {
    private val tokens = mapOf<String, String>()
    override fun get(token: LangToken): String = tokens[token.rawToken] ?: token.rawToken
}
