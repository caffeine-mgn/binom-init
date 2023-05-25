package pw.binom.init

fun <T : Any> multiSelect(query: String, items: List<T>, selected: List<T> = emptyList(), toString: (T) -> String = { it.toString() }): Set<T>? {
    val selectedSet = HashSet(selected)
    while (true) {
        println(query)
        items.forEachIndexed { index, value ->
            print("${index + 1}. ")
            if (value in selectedSet) {
                print("[x]")
            } else {
                print("[ ]")
            }
            println(" ${toString(value)}")
        }
        println("------")
        println("0. Закончить выделение")
        val txt = readlnOrNull() ?: return null
        val num = txt.toIntOrNull()
        if (num == null || num < 0 || num > items.size) {
            println("Введите значение от 0 до ${items.size}")
            continue
        }
        if (num == 0) {
            return selectedSet
        }
        val item = items[num - 1]
        if (item in selectedSet) {
            println("exist")
            selectedSet -= item
        } else {
            println("not exist")
            selectedSet += item
        }
        println("item: $item, selectedSet=$selectedSet")
    }
}

enum class YesNoRequest {
    DEFAULT_YES,
    DEFAULT_NO,
    REQUIRE,
}

fun text(query: String, trim: Boolean = true, default: String? = null, validator: (String) -> Unit = {}): String? {
    while (true) {
        print(query)
        if (default != null) {
            println(" (default: $default)")
        } else {
            println()
        }
        var text = readlnOrNull() ?: return null
        if (text.isEmpty() && default != null) {
            text = default
        }
        if (trim) {
            text = text.trim()
        }
        try {
            validator(text)
        } catch (e: IllegalArgumentException) {
            println(e.message ?: "Invalid input")
            continue
        }
        return text
    }
}

fun yesNo(text: String, default: YesNoRequest): Boolean? {
    while (true) {
        print(text)
        val answerDescription = when (default) {
            YesNoRequest.DEFAULT_YES -> " (Y/n)"
            YesNoRequest.DEFAULT_NO -> " (y/N)"
            YesNoRequest.REQUIRE -> ""
        }
        println(answerDescription)
        val resp = readlnOrNull()?.trim() ?: return null
        return when (resp) {
            "y", "Y" -> true
            "n", "N" -> false
            "" -> when (default) {
                YesNoRequest.DEFAULT_YES -> true
                YesNoRequest.DEFAULT_NO -> false
                YesNoRequest.REQUIRE -> continue
            }

            else -> {
                println("Unknown command: \"$resp\".")
                continue
            }
        }
    }
}

fun <T : Any> selector(query: String, items: List<T>, toString: (T) -> String = { it.toString() }): T? {
    while (true) {
        println(query)
        items.forEachIndexed { index, value ->
            println("${index + 1}. ${toString(value)}")
        }
        val txt = readlnOrNull() ?: return null
        val num = txt.toIntOrNull()
        if (num == null || num <= 0 || num > items.size) {
            println("Введите значение от 1 до ${items.size}")
            continue
        }
        return items[num - 1]
    }
}
