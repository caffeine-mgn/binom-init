package pw.binom.init

import pw.binom.*
import pw.binom.console.Terminal
import pw.binom.io.bufferedWriter
import pw.binom.io.file.*
import pw.binom.io.use

fun findExistProject(searchFrom: File): File? =
    searchFrom.relative("settings.gradle.kts").takeIfFile()

fun findExistProject2(searchFrom: File): File? {
    var wd = searchFrom
    while (true) {
        val settingsFile = wd.relative("settings.gradle.kts")
        if (settingsFile.isExist) {
            if (!settingsFile.isFile) {
                TODO()
            }
            return wd
        }
        wd = wd.parent ?: return null
    }
}

private val kotlinVersion = Version("kotlin", "1.9.20")
val shadowPlugin = Plugin.IdPlugin(
    id = "com.github.johnrengelman.shadow",
    version = Version("shadow", version = "5.2.0"),
)
val kotlinMultiplatformPlugin = Plugin.KotlinPlugin(
    name = "multiplatform",
    version = kotlinVersion,
    embedded = true,
)
val kotlinSerializationPlugin = Plugin.IdPlugin(
    id = "kotlinx-serialization",
    version = kotlinVersion,
)

fun main(args: Array<String>) {
    val rootDir = File(Environment.userDirectory).relative(".binom-init")
    val gradleDir = rootDir.relative("gradle")

    val existProjectDir = findExistProject(Environment.workDirectoryFile)
    if (existProjectDir != null) {
        addSubProject(existProjectDir)
    } else {
        createNewProject(gradleDir = gradleDir)
    }
}

fun addSubProject(projectDirection: File) {
    if (yesNo(
            text = "Добавить новый подпроект в \"$projectDirection\"?",
            default = YesNoRequest.DEFAULT_YES,
        ) != true
    ) {
        return
    }
    val project = readProject(emptyList()) ?: return
    project.generate(projectDirection.relative(project.name), globalConfig = null)
    val settingsFile = projectDirection.relative("settings.gradle.kts")
    settingsFile.openWrite(append = true).also {
        it.position = it.size
        it.bufferedWriter().use { output ->
            output.append("\ninclude(\":${project.name}\")")
        }
    }
}

fun createNewProject(gradleDir: File) {
    Terminal.clear()
    val multiProject = yesNo(
        text = "Мультимодульный проект?",
        default = YesNoRequest.DEFAULT_NO,
    ) ?: return

    val repository = HashSet<Repository>()
    repository += Repository.MAVEN_CENTRAL
    repository += Repository.MAVEN_LOCAL

//    val useBinomRepository = yesNo(
//        text = "Использовать репозиторий repo.binom.pw?",
//        default = YesNoRequest.DEFAULT_NO,
//    ) ?: return

//    val useLocalRepository = yesNo(
//        text = "Использовать репозиторий mavenLocal?",
//        default = YesNoRequest.DEFAULT_YES,
//    ) ?: return
//    if (useLocalRepository) {
//        repository += Repository.MAVEN_LOCAL
//    }
    repository += Repository.MAVEN_CENTRAL
    val project = if (multiProject) {
        Terminal.clear()
        val rootProjectName = text("Введите Название главного проекта", default = Environment.workDirectoryFile.name) {
            require(it.length > 1) { "Имя проекта не может быть пустым" }
            require("." !in it) { "В имени проекта не допускается символ точки" }
            require(" " !in it) { "В имени проекта не допускается символ пробела" }
        } ?: return
        val projects = ArrayList<Project>()
        do {
            val project = readProject(projects) ?: break
            projects += project
            Terminal.clear()
            println("Созданные проекты:")
            projects.forEach {
                println("${it.name} (${it.packageName})")
            }
            println()
            val createMore = yesNo(text = "Создать еще проект?", default = YesNoRequest.DEFAULT_NO) ?: return
            if (!createMore) {
                break
            }
        } while (true)
        MultiProject(
            config = GlobalConfig(
                rootName = rootProjectName,
                repositories = repository,
            ),
            projects = projects,
            kotlinVersion = kotlinVersion,
        )
    } else {
        val project = readProject(emptyList()) ?: return
        SingleProject(
            config = GlobalConfig(
                rootName = project.name,
                repositories = repository,
            ),
            project = project,
            kotlinVersion = kotlinVersion,
        )
    }

    val rootDirectory = Environment.workDirectoryFile
    project.generate(rootDirectory)
    gradleDir.relative("gradlew.bat")
        .takeIfExist()
        ?.copyInto(rootDirectory.relative("gradlew.bat"))
    val destGradlew = rootDirectory.relative("gradlew")
    gradleDir.relative("gradlew").takeIfExist()?.copyInto(destGradlew)
    val linuxPlatforms = setOf(
        Platform.LINUX_64,
        Platform.LINUX_ARM_32,
        Platform.LINUX_ARM_64,
        Platform.LINUX_MIPSEL_32,
        Platform.LINUX_MIPS_32,
        Platform.LINUX_X86,
    )
    if (Environment.platform in linuxPlatforms) {
        destGradlew.takeIfExist()?.setPosixMode(destGradlew.getPosixMode() + PosixPermissions.OWNER_EXECUTE)
    }
    gradleDir.relative("wrapper/gradle-wrapper.jar")
        .takeIfExist()
        ?.copyInto(rootDirectory.relative("gradle/wrapper/gradle-wrapper.jar"))
    gradleDir.relative("wrapper/gradle-wrapper.properties")
        .takeIfExist()
        ?.copyInto(rootDirectory.relative("gradle/wrapper/gradle-wrapper.properties"))
}

fun readProject(otherProjects: List<Project>): Project? {
    fun init() {
        Terminal.clear()
        if (otherProjects.isNotEmpty()) {
            println("Созданные проекты:")
            otherProjects.forEach {
                println("${it.name} (${it.packageName})")
            }
            println()
        }
    }
    init()
    val kind = selector(query = "Выберите тип проекта", items = Kind.values().toList()) {
        when (it) {
            Kind.APPLICATION -> "Приложение"
            Kind.LIBRARY -> "Библиотека"
        }
    } ?: return null
    init()
    val projectName = text("Введите имя проекта", default = otherProjects.firstOrNull()?.name) {
        require(it.length > 1) { "Имя проекта не может быть пустым" }
        require("." !in it) { "В имине проекта не допускается символ точки" }
        require(" " !in it) { "В имине проекта не допускается символ пробела" }
    } ?: return null

    init()
    val packageName = text("Введите имя пакета", default = otherProjects.firstOrNull()?.packageName) {
        require(it.length > 1) { "Имя пакета не может быть пустым" }
        require("-" !in it) { "В имине пакета не допускается символ \"-\"" }
        require(" " !in it) { "В имине пакета не допускается символ пробела" }
    } ?: return null

    val selected = multiSelect(
        query = "Какие библиотеки добавить?",
        items = BinomLibraries.libs.toList(),
        toString = { it -> it.name },
    ) ?: return null
    val targets = multiSelect(
        query = "Введите цели сборки",
        items = Targets.values().toList(),
    ) ?: return null
    val plugins = HashSet<Plugin>()
    plugins += kotlinMultiplatformPlugin
    if (Targets.JVM in targets) {
        plugins += shadowPlugin
    }
    return Project(
        name = projectName,
        packageName = packageName,
        kind = kind,
        targets = targets,
        libs = selected.map { it.library },
        plugins = plugins,
    )
}
