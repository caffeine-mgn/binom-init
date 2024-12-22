package pw.binom.init

import pw.binom.init.libs.BinomLibraries
import pw.binom.init.libs.Kotlin
import pw.binom.init.libs.UUIDLibrary

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
            items = BinomLibraries.libs + UUIDLibrary.libs,
            toString = { "${it.group}:${it.artifact}" },
        ) ?: return null

        val targets = multiSelect(
            query = "Введите цели сборки",
            items = Target.entries,
        ) ?: return null

        return KotlinProject(
            kind = kind,
            libs = selected,
            plugins = listOf(Kotlin.kotlinMultiplatformPlugin),
            targets = targets,
        )
    }
}