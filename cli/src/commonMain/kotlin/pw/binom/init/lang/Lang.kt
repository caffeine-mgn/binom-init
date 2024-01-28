package pw.binom.init.lang

interface Lang {
    fun get(token: LangToken): String
}
