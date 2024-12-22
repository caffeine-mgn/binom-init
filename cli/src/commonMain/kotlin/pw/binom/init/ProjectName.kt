package pw.binom.init

import pw.binom.io.file.File

value class ProjectName(private val path: String) {
    init {
        require(':' !in path)
    }

    val gradlePath
        get() = ":" + path.replace('/', ':')

    fun relative(file: File) = file.relative(path)
}