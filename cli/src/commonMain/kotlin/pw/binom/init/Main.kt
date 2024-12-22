package pw.binom.init

import pw.binom.Environment
import pw.binom.OS
import com.jakewharton.mosaic.runMosaicBlocking
import pw.binom.console.Terminal
import pw.binom.init.libs.BinomLibraries
import pw.binom.init.libs.Kotlin
import pw.binom.io.bufferedWriter
import pw.binom.io.file.*
import pw.binom.io.use
import pw.binom.os
import pw.binom.userDirectory

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


val shadowPlugin = Plugin.IdPlugin(
    id = "com.github.johnrengelman.shadow",
    version = Version("shadow", version = "5.2.0"),
)

//val binomPublicationPlugin = Plugin.IdPlugin(
//    id = "pw.binom.publish",
//    version = binomPublicationVersion,
//)

fun main(args: Array<String>) {
    val rootDirectory = Environment.workDirectoryFile
    val multiProject = yesNo(
        text = "Мультимодульный проект?",
        default = YesNoRequest.DEFAULT_NO,
    ) ?: return


    val project = if (multiProject) {
        val projects = HashMap<ProjectName, KotlinProject>()
        do {

            val projectName = text("Введите имя проекта") {
                require(it.length > 1) { "Имя проекта не может быть пустым" }
                require("." !in it) { "В имине проекта не допускаются точки" }
                require(" " !in it) { "В имине проекта не допускаются пробелы" }
                require(":" !in it) { "В имине проекта не допускаются двоеточие" }
                require("\\" !in it) { "В имине проекта не допускаются обратный слеш" }
//            require(!otherProjects.any { o -> o.name == it }) { "Проект с таким именем уже существует" }
            } ?: return
            projects[ProjectName(projectName)] = ProjectReader.read() ?: return
            val createMore = yesNo(text = "Создать еще проект?", default = YesNoRequest.DEFAULT_NO) ?: return
            if (!createMore) {
                break
            }
        } while (true)
        MainProject(
            projects = projects,
        )
    } else {
        ProjectReader.read() ?: return
    }

    project.generate(
        name = rootDirectory.name,
        file = rootDirectory,
    )


    val wrapperDir = rootDirectory.relative("gradle/wrapper")
    wrapperDir.mkdirs()
    GradleResources.unpackGradleWrapper(wrapperDir.relative("gradle-wrapper.jar"))
    GradleResources.unpackGradlewBat(rootDirectory.relative("gradlew.bat"))
    val destGradlew = rootDirectory.relative("gradlew")
    GradleResources.unpackGradlew(destGradlew)
    if (Environment.os == OS.LINUX || Environment.os == OS.MACOS) {
        destGradlew.takeIfExist()?.setPosixMode(
            destGradlew.getPosixMode()
                .withOwnerExecute()
        )
    }

    wrapperDir.relative("gradle-wrapper.properties").openWrite().bufferedWriter().use {
        it.append(GradleResources.gradleWrapperProperties("8.12"))
    }
    /*
    return
    val rootDir = File(Environment.userDirectory).relative(".binom-init")
    val gradleDir = rootDir.relative("gradle")

    val existProjectDir = findExistProject(Environment.workDirectoryFile)
    if (existProjectDir != null) {
        addSubProject(existProjectDir)
    } else {
        createNewProject(gradleDir = gradleDir)
    }
    */
}

/*
fun addSubProject(projectDirection: File) {
    if (yesNo(
            text = "Добавить новый подпроект в \"$projectDirection\"?",
            default = YesNoRequest.DEFAULT_YES,
        ) != true
    ) {
        return
    }
    val project = readProject(otherProjects = emptyList(), defaultGroup = null) ?: return
    project.generate(projectDirection.relative(project.name), globalConfig = null)
    val settingsFile = projectDirection.relative("settings.gradle.kts")
    settingsFile.openWrite(append = true).also {
        it.position = it.size
        it.bufferedWriter().use { output ->
            output.append("\ninclude(\":${project.name}\")")
        }
    }
}
*/
fun File.writer(func: (Writer) -> Unit) {
    openWrite().bufferedWriter().use { writer ->
        writer.write {
            func(this)
        }
    }
}

fun readGroup(default: String?): String? = text("Введите группу проекта", default = default) {
    require(it.length > 1) { "Группа не может быть пустым" }
    require(" " !in it) { "В группе не допускается символ пробела" }
    require(it[0] != '.') { "Группа не должна начинаться с точки" }
    require(it.last() != '.') { "Группа не должна заканчиваться на точку" }
}
/*
fun createNewProject(gradleDir: File) {
    val rootDirectory = Environment.workDirectoryFile
    val wrapperDir = rootDirectory.relative("gradle/wrapper")
    wrapperDir.mkdirs()
    Terminal.clear()
    val multiProject = yesNo(
        text = "Мультимодульный проект?",
        default = YesNoRequest.DEFAULT_NO,
    ) ?: return

    val repository = HashSet<Repository>()
    repository += Repository.MAVEN_CENTRAL
    repository += Repository.MAVEN_LOCAL
    repository += Repository.MAVEN_CENTRAL
    if (multiProject) {
        Terminal.clear()
        val rootProjectName = readGroup(default = Environment.workDirectoryFile.name) ?: return
        val rootProjectGroup = text("Введите группу главного проекта", default = rootProjectName) {
            require(it.length > 1) { "Имя проекта не может быть пустым" }
            require(" " !in it) { "В группе проекта не допускается символ пробела" }
            require(it[0] != '.') { "Группа не должна начинаться с точки" }
            require(it.last() != '.') { "Группа не должна заканчиваться на точку" }
        } ?: return
        val projects = ArrayList<Project>()
        do {
            if (projects.isNotEmpty()) {
                println("Введите данные первого подпроекта")
            }
            val project = readProject(otherProjects = projects, defaultGroup = rootProjectGroup) ?: break
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
        val project = MultiProject(
            name = rootProjectName,
            projects = projects,
            group = rootProjectGroup,
        )
        rootDirectory.relative("build.gradle.kts").writer { writer ->
            writer.write {
                BuildFileUtils.buildRoot(output = this, m = project)
            }
        }
        rootDirectory.relative("settings.gradle.kts").writer { writer ->
            BuildFileUtils.buildSettings(output = writer, m = project)
        }
        project.projects.forEach { project ->
            val projectDir = rootDirectory.relative(project.name)
            projectDir.mkdirs()
            projectDir.relative("build.gradle.kts").writer { writer ->
                BuildFileUtils.build(output = writer, project = project, isSubProject = true)
                generateSource(project = project, projectDir = projectDir)
            }
        }
        project.versions.takeIf { it.isNotEmpty() }
            ?.let { versions ->
                rootDirectory.relative("gradle.properties").writer { writer ->
                    BuildFileUtils.buildProperties(
                        output = writer,
                        lib = versions
                    )
                }
            }
    } else {
        val project = readProject(otherProjects = emptyList(), defaultGroup = null) ?: return
        rootDirectory.relative("build.gradle.kts").writer { writer ->
            writer.write {
                BuildFileUtils.build(output = this, project = project, isSubProject = false)
            }
        }
        rootDirectory.relative("settings.gradle.kts").writer { writer ->
            BuildFileUtils.buildSettings(output = writer, m = project)
        }
        project.versions.takeIf { it.isNotEmpty() }
            ?.let { versions ->
                rootDirectory.relative("gradle.properties").writer { writer ->
                    BuildFileUtils.buildProperties(
                        output = writer,
                        lib = versions
                    )
                }
            }
    }
    GradleResources.unpackGradleWrapper(wrapperDir.relative("gradle-wrapper.jar"))
    GradleResources.unpackGradlewBat(rootDirectory.relative("gradlew.bat"))
    val destGradlew = rootDirectory.relative("gradlew")
    GradleResources.unpackGradlew(destGradlew)
    if (Environment.os == OS.LINUX || Environment.os == OS.MACOS) {
        destGradlew.takeIfExist()?.setPosixMode(
            destGradlew.getPosixMode()
                .withOwnerExecute()
        )
    }

    wrapperDir.relative("gradle-wrapper.properties").openWrite().bufferedWriter().use {
        it.append(GradleResources.gradleWrapperProperties("8.8"))
    }
//    gradleDir.relative("wrapper/gradle-wrapper.jar")
//        .takeIfExist()
//        ?.copyInto(rootDirectory.relative("gradle/wrapper/gradle-wrapper.jar"))
//    gradleDir.relative("wrapper/gradle-wrapper.properties")
//        .takeIfExist()
//        ?.copyInto(rootDirectory.relative("gradle/wrapper/gradle-wrapper.properties"))
}

fun generateSource(project: Project, projectDir: File) {
    val mainDir =
        projectDir.relative("src/commonMain/kotlin").relative(project.packageName.replace('.', File.SEPARATOR))
    mainDir.mkdirs()
    if (project.kind == Kind.APPLICATION) {
        mainDir.relative("Main.kt").writer {
            it.apply {
                +"package ${project.packageName}"
                +""
                "fun main(args:Array<String>)" {
                    +"// Your code here"
                }
            }
        }
    }
}

fun readProject(otherProjects: List<Project>, defaultGroup: String?): Project? {
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
    val kind = selector(query = "Выберите тип проекта", items = Kind.entries) {
        when (it) {
            Kind.APPLICATION -> "Приложение"
            Kind.LIBRARY -> "Библиотека"
        }
    } ?: return null
    init()
    val projectName = text("Введите имя проекта", default = otherProjects.firstOrNull()?.name) {
        require(it.length > 1) { "Имя проекта не может быть пустым" }
        require("." !in it) { "В имине проекта не допускаются точки" }
        require(" " !in it) { "В имине проекта не допускаются пробелы" }
        require(!otherProjects.any { o -> o.name == it }) { "Проект с таким именем уже существует" }
    } ?: return null

    init()
    val packageName = readGroup(defaultGroup ?: otherProjects.firstOrNull()?.packageName) ?: return null

    val selected = multiSelect(
        query = "Какие библиотеки добавить?",
        items = BinomLibraries.libs.toList(),
        toString = { it.name },
    ) ?: return null
    val targets = multiSelect(
        query = "Введите цели сборки",
        items = Target.entries,
    ) ?: return null
    val plugins = HashSet<Plugin>()
    plugins += Kotlin.kotlinMultiplatformPlugin
    return Project(
        name = projectName,
        packageName = packageName,
        kind = kind,
        targets = targets,
        libs = selected.map { it.library },
        plugins = plugins,
    )
}
*/
