package pw.binom.init

fun Appendable.write(func: Writer.() -> Unit) {
    func(
        Writer(
            tabCount = 0,
            output = this,
        ),
    )
}

class Writer(val tabCount: Int, val output: Appendable) {
    private val tab = run {
        val sb = StringBuilder(tabCount * 4)
        repeat(tabCount) {
            sb.append("  ")
        }
        sb.toString()
    }

    operator fun String.unaryPlus() {
        output.append(tab).appendLine(this)
    }

    operator fun String.invoke(func: Writer.() -> Unit) {
        output.append(tab).append(this).appendLine(" {")
        func(Writer(tabCount = tabCount + 1, output = output))
        +"}"
    }
}
