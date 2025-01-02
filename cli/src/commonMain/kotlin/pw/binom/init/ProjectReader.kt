package pw.binom.init

import pw.binom.init.libs.*

object ProjectReader {
    fun read(): KotlinProject? {
        val kind = selector(query = "Выберите тип проекта", items = Kind.entries) {
            when (it) {
                Kind.APPLICATION -> "Приложение"
                Kind.LIBRARY -> "Библиотека"
            }
        } ?: return null

        val selected = multiSelect(
            query = "Какие библиотеки добавить?",
            items = BinomLibraries.libs + UUIDLibrary.libs + Kotlin.libs + CliLibrary.libs + AtomicLibrary.libs,
            toString = { "${it.group}:${it.artifact}${it.description?.let { " $it" } ?: ""}" },
        ) ?: return null

        val targets = multiSelect(
            query = "Введите цели сборки",
            items = Target.entries,
        ) ?: return null

        return KotlinProject(
            kind = kind,
            libs = selected + listOf(Kotlin.stdLib),
            plugins = emptyList(),
            targets = targets,
        )
    }
}