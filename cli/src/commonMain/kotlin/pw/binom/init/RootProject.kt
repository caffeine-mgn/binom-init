package pw.binom.init

import pw.binom.io.file.File

interface RootProject {
    fun generate(rootDirectory: File)
}
