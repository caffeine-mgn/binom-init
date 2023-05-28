package pw.binom.init

import pw.binom.*
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
    val project = readProject() ?: return
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
    val multiProject = yesNo(
            text = "Мультимодульный проект?",
            default = YesNoRequest.DEFAULT_NO,
    ) ?: return

    val useBinomRepository = yesNo(
            text = "Использовать репозиторий repo.binom.pw?",
            default = YesNoRequest.DEFAULT_NO,
    ) ?: return

    val useLocalRepository = yesNo(
            text = "Использовать репозиторий mavenLocal?",
            default = YesNoRequest.DEFAULT_YES,
    ) ?: return

    val project = if (multiProject) {
        val rootProjectName = text("Введите Название главного проекта", default = Environment.workDirectoryFile.name) {
            require(it.length > 1) { "Имя проекта не может быть пустым" }
            require("." !in it) { "В имени проекта не допускается символ точки" }
            require(" " !in it) { "В имени проекта не допускается символ пробела" }
        } ?: return
        val projects = ArrayList<Project>()
        do {
            val project = readProject() ?: break
            projects += project
            val createMore = yesNo(text = "Создать еще проект?", default = YesNoRequest.DEFAULT_NO) ?: return
            if (!createMore) {
                break
            }
        } while (true)
        MultiProject(
                config = GlobalConfig(
                        useBinomRepository = useBinomRepository,
                        rootName = rootProjectName,
                        useLocalRepository = useLocalRepository,
                ),
                projects = projects,
        )
    } else {
        val project = readProject() ?: return
        SingleProject(
                config = GlobalConfig(
                        useBinomRepository = useBinomRepository,
                        rootName = project.name,
                        useLocalRepository = useLocalRepository,
                ),
                project = project,
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

fun readProject(): Project? {
    val kind = selector(query = "Выберите тип проекта", items = Kind.values().toList()) {
        when (it) {
            Kind.APPLICATION -> "Приложение"
            Kind.LIBRARY -> "Библиотека"
        }
    } ?: return null
    val projectName = text("Введите имя проекта") {
        require(it.length > 1) { "Имя проекта не может быть пустым" }
        require("." !in it) { "В имини проекта не допускается символ точки" }
        require(" " !in it) { "В имини проекта не допускается символ пробела" }
    } ?: return null

    val packageName = text("Введите имя пакета") {
        require(it.length > 1) { "Имя пакета не может быть пустым" }
        require("-" !in it) { "В имини пакета не допускается символ \"-\"" }
        require(" " !in it) { "В имини пакета не допускается символ пробела" }
    } ?: return null
//    val libs = ArrayList<Library>()
//    var network = false
//    if (yesNo(text = "Добавить библиотеку работы с файлами?", default = YesNoRequest.DEFAULT_NO) == true) {
//        libs += Library("pw.binom.io", "file", "1.0.0-SNAPSHOT")
//    }
//    if (yesNo(text = "Добавить библиотеку HTTP сервер?", default = YesNoRequest.DEFAULT_NO) == true) {
//        libs += Library("pw.binom.io", "httpServer", "1.0.0-SNAPSHOT")
//        network = true
//    }
//    if (yesNo(text = "Добавить библиотеку HTTP клиент?", default = YesNoRequest.DEFAULT_NO) == true) {
//        libs += Library("pw.binom.io", "httpClient", "1.0.0-SNAPSHOT")
//        network = true
//    }
//    if (!network && yesNo(text = "Добавить библиотеку работы с сетью?", default = YesNoRequest.DEFAULT_NO) == true) {
//        libs += Library("pw.binom.io", "network", "1.0.0-SNAPSHOT")
//    }
    val selected = multiSelect(
            query = "Какие библиотеки добавить?",
            items = BinomLibraries.libs.toList(),
            toString = { it -> it.name },
    ) ?: return null
    val targets = multiSelect(
            query = "Введите цели сборки",
            items = Targets.values().toList(),
    ) ?: return null
    return Project(
            name = projectName,
            packageName = packageName,
            kind = kind,
            targets = targets,
            libs = selected.map { it.library },
    )
}
